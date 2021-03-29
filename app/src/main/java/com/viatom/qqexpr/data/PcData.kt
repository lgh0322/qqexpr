package com.viatom.demo.spotcheckm.data

import java.util.*

class PcData{
    companion object{
        var spo2: Int = 0
        var spo2Pr: Int = 0
        var sys: Int = 0
        var dia: Int = 0
        var pr: Int = 0

        var currentCountDownTime: Int = 0
        var latestSpo2: Int = 0
        var latestSpo2Pr: Int = 0
        var latestSpO2Date: Date? = null

        var needRecordSpo2: Boolean = true
    }
}