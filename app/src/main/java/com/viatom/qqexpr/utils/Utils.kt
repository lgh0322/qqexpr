package com.viatom.demo.spotcheckm.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.util.DisplayMetrics
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

class Utils {
    companion object {
        fun convertDpToPixel(context: Context, dp: Float) = dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        fun convertPixelsToDp(context: Context, px: Float) = px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

        fun getScreenWidth(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
        fun getScreenHeight(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

        fun getStringWidth(paint: Paint, str: String) = paint.measureText(str)
        fun getStringHeight(paint: Paint): Float {
            val fontMetrics = paint.fontMetrics
            return Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom)
        }
        fun getStringTopHeight(paint: Paint): Float {
            val fontMetrics = paint.fontMetrics
            return Math.abs(fontMetrics.top)
        }
        fun getStringBottomHeight(paint: Paint): Float {
            val fontMetrics = paint.fontMetrics
            return Math.abs(fontMetrics.bottom)
        }
        fun getDateList(date: Date, pattern: String, totalDay: Int = 30, size: Int = 7, locale: Locale = Locale.US): ArrayList<String> {
            val list = ArrayList<Date>()
            val format = SimpleDateFormat(pattern, locale)
            val calendar = Calendar.getInstance()
            calendar.time = date
            var i = 0
            while(i < size) {
                if(i != 0) {
                    calendar.add(Calendar.DATE, -5)
                }
                list.add(calendar.time)
                i++
            }
            val listString = ArrayList<String>()
            listString.addAll(list.map { format.format(it) })
            return listString
        }

        fun getDateStr(date: Date, pattern: String, locale: Locale = Locale.getDefault()) = SimpleDateFormat(pattern, locale).format(date)
        fun getDateStr(time: Long, pattern: String, locale: Locale = Locale.getDefault()) = SimpleDateFormat(pattern, locale).format(Date(time))

        fun getDateStr(date: Date, pattern: String, calendarField: Int, offset: Int = 0, locale: Locale = Locale.getDefault()) : String {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(calendarField, offset)

            return getDateStr(calendar.time, pattern)
        }

        fun getVersionName(context: Context): String {
            var version = ""
            try { // 获取packagemanager的实例
                val packageManager = context.packageManager
                // getPackageName()获取当前类的包名
                val packInfo = packageManager.getPackageInfo(context.packageName, 0)
                version = packInfo.versionName
            } catch (e: Exception) {
            }
            return version
        }

        fun getHexUppercase(b: Byte): String {
            val sb = StringBuilder()
            val lh: Int = (b.toInt() and 0x0f)
            val fh: Int = (b.toInt() and 0xf0) shr 4
            sb.append("0x")
            sb.append(HEX_ARRAY.get(fh))
            sb.append(HEX_ARRAY.get(lh))
            return sb.toString()
        }

        fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 3)
            for (j in bytes.indices) {
                val v: Int = bytes[j].toInt() and 0xFF
                hexChars[j * 3] = HEX_ARRAY.get(v ushr 4)
                hexChars[j * 3 + 1] = HEX_ARRAY.get(v and 0x0F)
                hexChars[j * 3 + 2] = ','
            }
            return String(hexChars)
        }

        fun dp2px(resources: Resources, dp: Float): Float {
            val scale = resources.displayMetrics.density
            return dp * scale + 0.5f
        }

        fun sp2px(resources: Resources, sp: Float): Float {
            val scale = resources.displayMetrics.scaledDensity
            return sp * scale
        }
    }
}