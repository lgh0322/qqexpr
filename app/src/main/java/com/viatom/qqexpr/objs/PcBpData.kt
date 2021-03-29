package com.viatom.demo.spotcheckm.objs

data class PcBpData(val byteArray: ByteArray) {
    val bpValue = (byteArray[5].toInt() and 0x0f shl 8) + (byteArray[6].toInt() and 0xff)
    val flag: Boolean = ((byteArray[5].toInt() and 0x10 shr 4) and 0x01) == 0x01

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PcBpData

        if (!byteArray.contentEquals(other.byteArray)) return false
        if (bpValue != other.bpValue) return false
        if (flag != other.flag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = byteArray.contentHashCode()
        result = 31 * result + bpValue
        result = 31 * result + flag.hashCode()
        return result
    }
}