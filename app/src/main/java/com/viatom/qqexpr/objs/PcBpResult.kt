package com.viatom.demo.spotcheckm.objs

data class PcBpResult(val byteArray: ByteArray) {
    val hrFlag = ((byteArray[5].toInt() and 0x80 shr 15) and 0x01) == 0x01
    val sys = (byteArray[5].toInt() and 0x7f shl 8) + (byteArray[6].toInt() and 0xff)
    val map = byteArray[7].toInt()
    val dia = byteArray[8].toInt()
    val bmp = byteArray[9].toInt()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PcBpResult

        if (!byteArray.contentEquals(other.byteArray)) return false
        if (hrFlag != other.hrFlag) return false
        if (sys != other.sys) return false
        if (map != other.map) return false
        if (dia != other.dia) return false
        if (bmp != other.bmp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = byteArray.contentHashCode()
        result = 31 * result + hrFlag.hashCode()
        result = 31 * result + sys
        result = 31 * result + map
        result = 31 * result + dia
        result = 31 * result + bmp
        return result
    }
}