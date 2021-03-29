package com.viatom.demo.spotcheckm.objs.wave

import com.viatom.demo.spotcheckm.ble.objs.Response
import java.util.*

class BaseWaveData {
    companion object {
        var data: MutableList<Wave> = ArrayList<Wave>()
        fun addWave(waveResponse: Response.PcBleResponse) {
            val length = waveResponse.content.size
            for(i in 0 until length) {
                val wave = Wave(waveResponse.content[i])
                data.add(wave)
            }
        }
    }

    data class Wave(val byte: Byte) {
        var data = byte.toInt() and 0x7F
        var flag = (byte.toInt() and 0x80 shr 8) and 0x01
    }
}