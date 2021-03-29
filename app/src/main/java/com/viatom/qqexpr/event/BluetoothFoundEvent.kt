package com.viatom.qqexpr.event


import com.viatom.qqexpr.ble.objs.Bluetooth

class BluetoothFoundEvent(var key: String, bluetooth: Bluetooth) {
    private var bluetooth: Bluetooth

    fun getBluetooth(): Bluetooth {
        return bluetooth
    }

    fun setBluetooth(bluetooth: Bluetooth) {
        this.bluetooth = bluetooth
    }

    companion object {
        const val SUPPORT_DEVICE_FOUND = "support_device_found"
        const val PAIR_DEVICE_FOUND = "pair_device_found"
        const val PAIR_OXYFIT_DEVICE_FOUND = "pair_oxyfit_device_found"
    }

    init {
        this.bluetooth = bluetooth
    }
}