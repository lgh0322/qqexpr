package com.viatom.demo.spotcheckm.objs

data class PcStatus(val byteArray: ByteArray) {
    val status = byteArray[5]
    val swVer: String
        get() {
            val swByte = byteArray[6]
            val preVer = (swByte.toInt() and 0xf0 shr 4)
            val sufVer = (swByte.toInt() and 0x0f)
            return "${preVer}.${sufVer}"
        }

    val hwVer: String
        get() {
            val swByte = byteArray[7]
            val preVer = (swByte.toInt() and 0xf0 shr 4)
            val sufVer = (swByte.toInt() and 0x0f)
            return "${preVer}.${sufVer}"
        }
}