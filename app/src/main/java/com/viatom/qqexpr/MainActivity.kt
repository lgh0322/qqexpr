package com.viatom.qqexpr

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import com.viatom.demo.spotcheckm.ble.BleMsg
import com.viatom.demo.spotcheckm.ble.BleMsgUtils
import com.viatom.demo.spotcheckm.ble.KtBleService
import com.viatom.demo.spotcheckm.event.PcEvent
import com.viatom.demo.spotcheckm.objs.PcBpData
import com.viatom.demo.spotcheckm.objs.PcBpResult
import com.viatom.demo.spotcheckm.objs.PcStatus
import com.viatom.demo.spotcheckm.objs.Spo2Param
import com.viatom.qqexpr.ble.BleCmd
import com.viatom.qqexpr.ble.objs.Bluetooth
import com.viatom.qqexpr.data.GlobalData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {


    val scan=BleScanManager()
    var s:String="F6:2A:32:55:C5"
    lateinit var ble:Bluetooth

    lateinit var mHandler : Handler



    private var isBind: Boolean = false
    private var mService: Messenger? = null
    var reconnectingTime: Long = 1000L
    var needShowResult: Boolean = false

    @SuppressLint("HandlerLeak")
    inner class ClientHandle : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val state = msg.arg1
            when (msg.what) {
                BleMsg.MSG_REGISTER -> {
                    isBind = true
                }
                BleMsg.MSG_UNREGISTER -> {
                    isBind = false
                    mService = null
                }
                BleMsg.MSG_CONNECT -> {
                    if (state == BleMsg.CODE_CONNECTED) {

                    } else if (state == BleMsg.CODE_TIMEOUT) {

                        Log.d("DataFragment", "Connect timeout")
                        disconnect()
                    } else if (state == BleMsg.CODE_ERROR) {

                        Log.d("DataFragment", "Connect error")
                        disconnect()
                    } else if (state == BleMsg.CODE_DISCONNECTED) {

                        Log.d("DataFragment", "Disconnected")
                        onBTStateChanged()
                        GlobalData.isConnecting = false
                        reconnect()
                    } else if(state == BleMsg.CODE_SUCCESS) {

                        Log.d("DataFragment", "Connected")
                        GlobalData.isConnecting = false
                        onBTStateChanged()
                    } else if(state == BleMsg.CODE_BUSY) {
                        GlobalData.isConnecting = false
                        disconnect()
                        reconnectingTime += 2000L
                        if(reconnectingTime > 15 * 1000) {
                            reconnectingTime = 5000L
                        }
                    }
                }
            }
        }
    }

    private val mClient : Messenger = Messenger(ClientHandle())

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            isBind = true
            BleMsgUtils.register(mClient, mService!!)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBind = false
            BleMsgUtils.unregister(mClient, mService!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KtBleService.startService(applicationContext)
        mHandler = Handler(Looper.getMainLooper())
        scan.initScan(this)
        scan.setCallBack(object:BleScanManager.Scan{
            override fun scanReturn(name: String, bluetoothDevice: BluetoothDevice) {
                if (name.contains("PC-100:00245")) {
                    scan.stop()
                    GlobalData.isConnecting = true
                    ble = Bluetooth(Bluetooth.MODEL_PC100,name,bluetoothDevice,50)
                    GlobalData.bluetooth=ble
                    Log.e(s, name)
                    if(mService==null){
                        Log.e("fuck","fuckfuck")
                    }
                    BleMsgUtils.sendMsg(mClient, mService!!, BleMsg.MSG_CONNECT, GlobalData.bluetooth as Any)
                }
            }

        })
        EventBus.getDefault().register(this)
    }


    override fun onStart() {
        super.onStart()
        bindService()
    }



    fun onBTStateChanged() {
        Log.d("PC100_DashboardFragment", "onBTStateChanged GlobalData.isConnected == ${GlobalData.isConnected}")
        if(!GlobalData.isConnected) {
            getInfo()
            if(::mHandler.isInitialized) {
                mHandler.post{

                }
            }
        } else {
            if(::mHandler.isInitialized) {
                mHandler.post{

                }
            }
        }
    }

    private fun disconnect() {
        if(GlobalData.isConnecting) return
        GlobalScope.launch {
            GlobalData.isConnecting = true
            delay(reconnectingTime)
            BleMsgUtils.sendMsg(mClient, mService!!, BleMsg.MSG_DISCONNECT)
        }
    }

    private fun reconnect() {
        if(GlobalData.isConnected) return
        if(GlobalData.isConnecting) return
        GlobalScope.launch {
            GlobalData.isConnecting = true
            delay(reconnectingTime)
            BleMsgUtils.sendMsg(mClient, mService!!, BleMsg.MSG_CONNECT, GlobalData.bluetooth as Any)
        }
    }

    private fun getInfo() {
        if(isBind && GlobalData.isConnected) {
            BleMsgUtils.sendMsg(mClient, mService!!, BleMsg.MSG_SEND_CMD, BleCmd.PcCmd.CMD_TYPE_GET_DEVICE_INFO,false)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onPcEvent(event: PcEvent) {
        val response = event.response
        if(response.token == BleCmd.PcCmd.TOKEN_SPO2_RT_DATA) { // SPO2 Real-time data(1Hz)
            val spo2Param = Spo2Param(response.bytes)
            mHandler.post {
                if(!needShowResult) {

                }
            }
        } else if(BleCmd.getMsgType(Bluetooth.MODEL_PC100, response.bytes) == BleCmd.PcCmd.CMD_TYPE_SPO2_GET_STATE) {
            val pcStatus = PcStatus(response.bytes)
            if(pcStatus.status == 0xFF.toByte()) {
                if(!needShowResult) {

                }


            }
        } else if(BleCmd.getMsgType(Bluetooth.MODEL_PC100, response.bytes) == BleCmd.PcCmd.CMD_TYPE_SPO2_FINGER_OUT) {
            mHandler.post {
                if(!needShowResult) {

                }


            }
        } else if(response.token == BleCmd.PcCmd.TOKEN_BP_RT_DATA) { // BP Real-time value
            val bpData = PcBpData(response.bytes)
            mHandler.post {

            }
        } else if(BleCmd.getMsgType(Bluetooth.MODEL_PC100, response.bytes) == BleCmd.PcCmd.CMD_TYPE_BP_START) {
            // BleCmd.PcCmd.TOKEN_BP_MODULE_STATE 0x40
            mHandler.post {

            }
        } else if(BleCmd.getMsgType(Bluetooth.MODEL_PC100, response.bytes) == BleCmd.PcCmd.CMD_TYPE_BP_END) {
            needShowResult = true
            mHandler.post {

            }
            mHandler.postDelayed(Runnable {
                needShowResult = false
            }, 10000L)
        } else if(BleCmd.getMsgType(Bluetooth.MODEL_PC100, response.bytes) == BleCmd.PcCmd.CMD_TYPE_BP_GET_RESULT) {
            val type = response.type
            if(type == 0x01.toByte()) {
                val bpResult = PcBpResult(response.bytes)
                needShowResult = true
                mHandler.post {

                }
                mHandler.postDelayed(Runnable {
                    needShowResult = false

                }, 10000L)
            } else if(type == 0x02.toByte()) {
                needShowResult = true
                mHandler.post {

                }
                mHandler.postDelayed(Runnable {
                    needShowResult = false

                }, 10000L)
            }
        }
    }


    private fun bindService() {
        val intent = Intent(this, KtBleService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

}