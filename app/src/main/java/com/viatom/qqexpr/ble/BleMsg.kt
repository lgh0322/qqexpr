package com.viatom.demo.spotcheckm.ble

class BleMsg {
    companion object {
        const val MSG_CONNECT = 1
        const val MSG_DISCONNECT = 2
        const val MSG_GET_INFO = 3
        const val MSG_SET_TIME = 4

        const val MSG_CONN_DFU = 15


        const val MSG_REGISTER = 98
        const val MSG_UNREGISTER = 99

        const val MSG_SEND_CMD = 100

        const val CODE_SUCCESS = 200
        const val CODE_PROGRESSING = 201
        const val CODE_CONNECTED = 202
        const val CODE_DISCONNECTED = 203
        const val CODE_TIMEOUT = 300
        const val CODE_BUSY = 400
        const val CODE_ERROR = 500
    }


}
