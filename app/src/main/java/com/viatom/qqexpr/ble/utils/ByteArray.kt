package com.viatom.demo.spotcheckm.ble.utils

fun ByteArray.toHex() = joinToString("") {
    String.format("%02X", (it.toInt() and 0xff))
}

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

fun add(ori: ByteArray?, add: ByteArray): ByteArray {
    if (ori == null) {
        return add
    }

    val new: ByteArray = ByteArray(ori.size + add.size)
    for ((index, value) in ori.withIndex()) {
        new[index] = value
    }

    for ((index, value) in add.withIndex()) {
        new[index + ori.size] = value
    }

    return new
}



@ExperimentalUnsignedTypes fun toUInt(bytes: ByteArray): Int {
    var result : UInt = 0u
    for (i in bytes.indices) {
        result = result or ((bytes[i].toUInt() and 0xFFu) shl 8*i)
    }

    return result.toInt()
}

fun toInt(bytes: ByteArray): Int {
    var result : Int = 0
    for (i in bytes.indices) {
        result = result or ((bytes[i].toInt() and 0xFF) shl 8*i)
    }

    return result
}

fun toString(bytes: ByteArray): String {
    var str = ""
    for (byte in bytes) {
        str += byte.toChar()
    }

    return str
}