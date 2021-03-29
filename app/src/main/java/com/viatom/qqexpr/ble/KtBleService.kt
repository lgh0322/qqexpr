package com.viatom.demo.spotcheckm.ble

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.jakewharton.rx.ReplayingShare
import com.polidea.rxandroidble2.*
import com.polidea.rxandroidble2.exceptions.BleDisconnectedException

import com.viatom.demo.spotcheckm.ble.objs.Response
import com.viatom.demo.spotcheckm.ble.utils.add
import com.viatom.demo.spotcheckm.ble.utils.toHex
import com.viatom.qqexpr.data.GlobalData
import com.viatom.demo.spotcheckm.utils.Utils.Companion.bytesToHex
import com.viatom.qqexpr.ble.BleCmd
import com.viatom.qqexpr.ble.objs.Bluetooth
import com.viatom.qqexpr.ble.objs.Bluetooth.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.math.ceil

const val OXYSMART_WRITE_CHARACTERISTIC = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
const val OXYSMART_NOTIFY_CHARACTERISTIC = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

const val PO_WRITE_CHARACTERISTIC = "0000FFE4-0000-1000-8000-00805F9B34FB"
const val PO_NOTIFY_CHARACTERISTIC = "0000FFE4-0000-1000-8000-00805F9B34FB"

const val PC100_WRITE_CHARACTERISTIC = "0000FFF2-0000-1000-8000-00805F9B34FB"
const val PC100_NOTIFY_CHARACTERISTIC = "0000FFF1-0000-1000-8000-00805F9B34FB"

const val AOJ20A_WRITE_CHARACTERISTIC = "0000FFE2-0000-1000-8000-00805F9B34FB"
const val AOJ20A_NOTIFY_CHARACTERISTIC = "0000FFE1-0000-1000-8000-00805F9B34FB"

const val OXYFIT_WRITE_CHARACTERISTIC = "8b00ace7-eb0b-49b0-bbe9-9aee0a26e1a3"
const val OXYFIT_NOTIFY_CHARACTERISTIC = "0734594a-a8e7-4b1a-a6b1-cd5243059a57"

const val CHECKME_BLE_SERVICE_UUID = "14839ac4-7d7e-415c-9a42-167340cf2339"
const val CHECKME_BLE_WRITE_UUID = "8b00ace7-eb0b-49b0-bbe9-9aee0a26e1a3"
const val CHECKME_BLE_READ_UUID = "0734594a-a8e7-4b1a-a6b1-cd5243059a57"

const val TAG = "OxySmart_BleService"

var pool: ByteArray? = null

class KtBleService : Service() {
    var connected : Boolean = false
    var deviceName: String = ""
    var model: Int = Bluetooth.MODEL_PC100

    var curWork : Job? = null

    var bleDevice: RxBleDevice? = null
    lateinit var rxBleClient: RxBleClient
    private lateinit var connectionObservable: Observable<RxBleConnection>
    private val connectionDisposable = CompositeDisposable()
    private var state: Disposable? = null

    var write_uuid = UUID.fromString(OXYSMART_WRITE_CHARACTERISTIC)
    var notify_uuid = UUID.fromString(OXYSMART_NOTIFY_CHARACTERISTIC)

    private fun isBusy() : Boolean {
        if (curWork == null) {
            return false
        }
        if (curWork!!.isActive) {
            return true
        }
        return false
    }

    /**
     * handle message
     *
     * Service
     */
    private var mService : Messenger? = null

    inner class ServerHandler (
            context: Context,
            private val applicationContext: Context = context.applicationContext
    ): Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BleMsg.MSG_REGISTER -> {
                    BleMsgUtils.addClient(msg.replyTo)
                    BleMsgUtils.sendMsg(mService!!, msg.replyTo, BleMsg.MSG_REGISTER, BleMsg.CODE_SUCCESS)
                }
                BleMsg.MSG_UNREGISTER -> {
                    BleMsgUtils.removeClient(msg.replyTo)
                    BleMsgUtils.sendMsg(mService!!, msg.replyTo, BleMsg.MSG_UNREGISTER, BleMsg.CODE_SUCCESS)
                }
                BleMsg.MSG_CONNECT -> {
                    if (isBusy()) {
                        BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_BUSY)
                    }
                    if (!connected) {
                        connect(msg.obj as Bluetooth)
                    }
                }
                BleMsg.MSG_DISCONNECT -> {
                    disConnect()
                }
                BleMsg.MSG_SEND_CMD -> {
//                    if (isBusy()) {
//                        BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_BUSY)
//                    }
                    if (connected) {
                        val cmdType = msg.arg1
                        var needCallback = msg.obj as Boolean
                        if(model == Bluetooth.MODEL_AOJ20A) {
                            needCallback = false
                        }
                        sendMsg(cmdType, needCallback)
                    }
                }
            }
        }
    }

    fun connect(b: Bluetooth) {
        model = b.model
        when(model) {
            MODEL_OXYSMART -> {
                write_uuid = UUID.fromString(OXYSMART_WRITE_CHARACTERISTIC)
                notify_uuid = UUID.fromString(OXYSMART_NOTIFY_CHARACTERISTIC)
            }
            MODEL_TV221U -> {
                write_uuid = UUID.fromString(PO_WRITE_CHARACTERISTIC)
                notify_uuid = UUID.fromString(PO_NOTIFY_CHARACTERISTIC)
            }
            MODEL_PC100 -> {
                write_uuid = UUID.fromString(PC100_WRITE_CHARACTERISTIC)
                notify_uuid = UUID.fromString(PC100_NOTIFY_CHARACTERISTIC)
            }
            MODEL_AOJ20A -> {
                write_uuid = UUID.fromString(AOJ20A_WRITE_CHARACTERISTIC)
                notify_uuid = UUID.fromString(AOJ20A_NOTIFY_CHARACTERISTIC)
            }
            MODEL_OXYFIT -> {
                write_uuid = UUID.fromString(OXYFIT_WRITE_CHARACTERISTIC)
                notify_uuid = UUID.fromString(OXYFIT_NOTIFY_CHARACTERISTIC)
            }
            MODEL_CHECK_POD -> {
                write_uuid = UUID.fromString(CHECKME_BLE_WRITE_UUID)
                notify_uuid = UUID.fromString(CHECKME_BLE_READ_UUID)
            }
            else -> {
                write_uuid = UUID.fromString(OXYSMART_WRITE_CHARACTERISTIC)
                notify_uuid = UUID.fromString(OXYSMART_NOTIFY_CHARACTERISTIC)
            }
        }
        Log.d(TAG, "Service: start connect")
        curWork = GlobalScope.launch {
            /**
             * observe state
             */
            bleDevice = rxBleClient.getBleDevice(b.macAddr)
            deviceName = b.name
            if(bleDevice == null) {
                BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_ERROR)
                GlobalData.isConnected = false
                GlobalData.isConnecting = false
                return@launch
            }
            fun onConnectionStateChanged(newState: RxBleConnection.RxBleConnectionState) {
                Log.d(TAG, "connect state changed: $newState")
                if (newState == RxBleConnection.RxBleConnectionState.CONNECTED) {
                    connected = true
                    BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_CONNECTED)
                    setNotify()
                } else if (newState == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
                    disConnect()
                    BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_DISCONNECTED)
                    GlobalData.isConnected = false
                }
            }

            /**
             * observe
             */
            bleDevice!!.observeConnectionStateChanges()
                    .distinctUntilChanged()
                    .observeOn(Schedulers.io())
                    .subscribe{
                        onConnectionStateChanged(it)
                    }
                    .let { state = it }

            connectionObservable = bleDevice!!
                    .establishConnection(false)
                    .compose( ReplayingShare.instance() )

            connectionObservable
                    .flatMapSingle  { it.discoverServices() }
                    .flatMapSingle {
                        it.getCharacteristic(notify_uuid)
                    }
                    .observeOn(Schedulers.io())
                    .subscribe(
                            { characteristic ->
                                Log.d(TAG, characteristic.toString())
                            },
                            {
                                if(it is BleDisconnectedException) {
                                    BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_TIMEOUT)
                                }

                                it.printStackTrace()
                            }
                    )
                    .let { connectionDisposable.add(it) }
        }
    }

    /**
     * setup notify
     */
    private fun setNotify() {

        Log.d(TAG, "Service: start set notify")
        GlobalScope.launch {

            runBlocking {

                var done: Boolean = false

                val timeout = launch {
                    delay(10 * 1000)
                    // timeout
                    done = true
                    BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_TIMEOUT)
                    Log.d(TAG, "set notify time out")
                }

                val observable = connectionObservable
                        .flatMap { it.setupNotification(notify_uuid) }
                        .doOnNext {
                            Log.d(TAG, "notify set up success" )
                            if (done) {
                                Log.d(TAG, "set notify time out")
                            } else {
                                BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_SUCCESS)
                                GlobalData.isConnected = true
                            }
                            timeout.cancel()
                            done = true
                        }
                        .flatMap { it }
                registerNotification(observable)
            }
        }
    }

    private fun registerNotification(observable: Observable<ByteArray>) {
        if(model == MODEL_PC100) {
            observable.timeout(3, TimeUnit.SECONDS)
                    .observeOn(Schedulers.io())
                    .subscribe(
                            {
                                onNotificationReceived(it)
                            },
                            {
                                it.printStackTrace()
                                Log.d(TAG, "receive notify error: " + it.message.toString())
                                if(it is TimeoutException) { // SPO2 probe has been unplugged
                                    val byteArray: ByteArray = byteArrayOf(0xAA.toByte(), 0x55.toByte(), 0x51.toByte(), 0x03.toByte(), 0x02.toByte(), 0x28.toByte(), 0x17.toByte())
                                    onNotificationReceived(byteArray)
//                                    setNotify()
                                    // the ble notified characteristic has been reset, make it enable again
                                    registerNotification(observable)
                                } else {
                                    BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_ERROR)
                                    GlobalData.isConnected = false
                                    GlobalData.isConnecting = false
                                }

                            }
                    )
                    .let { connectionDisposable.add(it) }
        } else {
            observable.observeOn(Schedulers.io())
                    .subscribe(
                            {
                                onNotificationReceived(it)
                            },
                            {
                                it.printStackTrace()
                                Log.d(TAG, "receive notify error: " + it.message.toString())
                                BleMsgUtils.broadcastMsg(mService!!, BleMsg.MSG_CONNECT, BleMsg.CODE_ERROR)
                                GlobalData.isConnected = false
                                GlobalData.isConnecting = false
                            }
                    )
                    .let { connectionDisposable.add(it) }
        }

    }

    /**
     * disconnect device
     */
    private fun disConnect() {
        Log.d(TAG, "disconnect, dispose")
        connected = false
        state?.dispose()
        state = null
        connectionDisposable.clear()
    }

    private fun onNotificationReceived(bytes: ByteArray)  {
        Log.d(TAG, "onNotificationReceived == " + bytesToHex(bytes))
        handleResponse(bytes)
    }

    private fun handleResponse(bytes: ByteArray) {
        pool = add(pool, bytes)
        pool = Response.hasResponse(pool, model)
    }

    /**
     * deal with long write
     */
    private fun sendCmd(cmd: ByteArray, listener: Response.ReceivedListener?) {
        Log.d(TAG, "send cmd: " + cmd.toHex())

        val times: Int = ceil(cmd.size.toDouble() / 20).toInt()
        for (i in 1..times) {
            val des = if (i*20>cmd.size) cmd.size else i*20
            val tempCmd = cmd.copyOfRange((i-1)*20, des)

            connectionObservable
                    .firstOrError()
                    .flatMap {
                        it.writeCharacteristic(write_uuid, tempCmd)
                    }
                    .observeOn(Schedulers.io())
                    .subscribe(
                            {
                                Log.d(TAG, "send cmd success: " + tempCmd.toHex())
                            },
                            {
                                Log.d(TAG, "send cmd failed: "+ it.message.toString())
                            }
                    )
                    .let { connectionDisposable.add(it) }

            sleep(5)
        }
        Response.setReceivedListener(listener)
    }

    /**
     * send Msg to remote device
     */
    private fun sendMsg(msgType: Int, needCallback: Boolean = true) {

        Log.d(TAG, "Service: start getMsg")

        curWork = GlobalScope.launch {

            runBlocking {
                val cmd: ByteArray = BleCmd.getCmd(model, msgType)
                Log.d(TAG, "sendMsg cmd == " + bytesToHex(cmd))
                if(needCallback) {
                    var done: Boolean = false

                    val timeout = launch {
                        delay(5 * 1000)
                        // timeout
                        done = true
                        Log.d(TAG, "get real time state time out")
                        BleMsgUtils.broadcastMsg(mService!!, msgType, BleMsg.CODE_TIMEOUT)
                    }
                    val callback = object : Response.ReceivedListener {
                        override fun onReceived(bytes: ByteArray) {
                            val responseType = BleCmd.getMsgType(model, bytes)
                            if(responseType == msgType) {
                                if (done) {
                                    //
                                } else {
                                    BleMsgUtils.broadcastMsg(mService!!, msgType, BleMsg.CODE_SUCCESS, bytes)
                                    timeout.cancel()
                                    done = true
                                }
                            }
                        }
                    }
                    sendCmd(cmd, callback)
                } else {
                    sendCmd(cmd, null)
                }
            }
        }
    }

    override fun onCreate() {
        Log.d(TAG, "Service onCreate")
        rxBleClient = RxBleClient.create(this)
        RxBleClient.updateLogOptions(
                LogOptions.Builder()
                        .setLogLevel(LogConstants.INFO)
                        .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
                        .setUuidsLogSetting(LogConstants.UUIDS_FULL)
                        .setShouldLogAttributeValues(true)
                        .build()
        )
        mService = Messenger(ServerHandler(this))
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "Service onBind")
        return mService!!.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Service onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        disConnect()
        Log.d(TAG, "Service onDestroy")
    }

    companion object{
        @JvmStatic
        fun device() : RxBleDevice? = null

        @JvmStatic
        fun startService(context: Context) {
            val intent = Intent(context, KtBleService::class.java)
            context.startService(intent)
        }

        @JvmStatic
        fun stopService(context: Context) {
            val intent = Intent(context, KtBleService::class.java)
            context.stopService(intent)
        }

    }

}