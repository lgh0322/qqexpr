package com.viatom.demo.spotcheckm.objs

data class PcDeviceInfo (val byteArray: ByteArray) {
    val swVer: String
        get() {
            val swByte = byteArray[5]
            val preVer = (swByte.toInt() and 0xf0 shr 4)
            val sufVer = (swByte.toInt() and 0x0f)
            return "${preVer}.${sufVer}"
        }

    val hwVer: String
        get() {
            val swByte = byteArray[6]
            val preVer = (swByte.toInt() and 0xf0 shr 4)
            val sufVer = (swByte.toInt() and 0x0f)
            return "${preVer}.${sufVer}"
        }


}