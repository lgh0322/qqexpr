package com.viatom.qqexpr.data

import com.viatom.qqexpr.ble.objs.Bluetooth

class GlobalData {
    companion object {
        var bluetooth : Bluetooth? = null
        var deviceName : String? = null
        var isConnected: Boolean = false
        var isConnecting: Boolean = false
        var adminMode: Boolean = false
    }
}