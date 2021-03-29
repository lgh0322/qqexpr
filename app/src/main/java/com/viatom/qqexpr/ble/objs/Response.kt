package com.viatom.demo.spotcheckm.ble.objs

import android.util.Log

import com.viatom.demo.spotcheckm.event.PcEvent
import com.viatom.demo.spotcheckm.objs.wave.BaseWaveData
import com.viatom.qqexpr.ble.BleCmd
import com.viatom.qqexpr.ble.CrcUtil
import com.viatom.qqexpr.ble.objs.Bluetooth
import org.greenrobot.eventbus.EventBus

object Response {
    const val HEAD_0 = 0xAA.toByte()
    const val HEAD_1 = 0x55.toByte()
    const val TOKEN_EPI_F0 = 0xF0.toByte() // (EPI -> Equipment Public information)
    const val TOKEN_PO_0F = 0x0F.toByte() // (PO -> Pulse Oximeter)

    var listener: ReceivedListener? = null
    fun setReceivedListener(listener: ReceivedListener?) {
        this.listener = listener
    }

    fun hasResponse(bytes: ByteArray?, model: Int): ByteArray? {
        Log.d("response:$model", model.toString());
        return when(model) {
            Bluetooth.MODEL_PC100 -> {
                pc100(bytes)
            }
            else -> {
                bytes
            }
        }
    }

    private fun pc100(bytes: ByteArray?): ByteArray? {
        val bytesLeft: ByteArray? = bytes

        if (bytes == null || bytes.size < 6) {
            return bytes
        }

        loop@ for (i in 0 until bytes.size-5) {
            if (bytes[i] != 0xAA.toByte()) {
                continue@loop
            }

            if (bytes[i + 1] != 0x55.toByte()) {
                continue@loop
            }

            val token = bytes[i + 2]
            if(token == 0x10.toByte()) {
                continue@loop
            }

            val length = bytes[i + 3]
            if(length < 0) {
                continue@loop
            }

            if (i + 4 + length > bytes.size) {
//                continue@loop
                return bytes.copyOfRange(i, bytes.size)
            }
            val byteSize = bytes.size
            val indexTo = i + 4 + length
            val temp: ByteArray = bytes.copyOfRange(i, indexTo)
            val crcValue = CrcUtil.calCRC8ByCCITT(temp)
            val tempCrc = temp[temp.size - 1]
//            Log.d(TAG, "Response pc100 crcValue == $crcValue tempCrc == $tempCrc")
            if(tempCrc == crcValue) {
                val msgType = BleCmd.getMsgType(Bluetooth.MODEL_PC100, temp)
//                Log.d(TAG, "Response pc100 msgType == $msgType HEX == ${Utils.bytesToHex(temp)}")
                if(msgType == BleCmd.MSG_TYPE_INVALID) {
                    continue@loop
                } else {
                    val bleResponse = PcBleResponse(temp)
                    if(BleCmd.getMsgType(Bluetooth.MODEL_PC100, bleResponse.bytes) == BleCmd.PcCmd.CMD_TYPE_SPO2_WAVE_DATA) { // spo2 wave data
                        BaseWaveData.addWave(bleResponse)
                    } else {
                        EventBus.getDefault().postSticky((PcEvent(bleResponse)))
                    }

                    val tempBytes: ByteArray? = bytes.copyOfRange(i + 4 + length, bytes.size)
                    return pc100(tempBytes)
                }
            } else {
                continue@loop
            }
        }

        return bytesLeft
    }

    class PcBleResponse(var bytes: ByteArray) {
        val token: Byte = bytes[2]
        private val length: Int = bytes[3].toInt()
        val type: Byte = bytes[4]
        val content: ByteArray = if (length == 1) ByteArray(0) else bytes.copyOfRange(5, bytes.size - 1)

        override fun equals(other: Any?): Boolean {
            if(this === other) return true
            if(other is PcBleResponse) {
                return this.bytes.contentEquals(other.bytes)
            }
            return false
        }

    }

    /**
     * Response Received Listener
     */
    interface ReceivedListener {
        fun onReceived(bytes: ByteArray)
    }
}