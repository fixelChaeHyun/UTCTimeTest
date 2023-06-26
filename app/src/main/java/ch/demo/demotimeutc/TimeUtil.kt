package ch.demo.demotimeutc

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    val TAG = this.javaClass.name
    val COMMON_DATE_FORMAT = "yyyy-MM-dd  HH:mm:ss  E  [z Z]"

    /** SimpleDateFormat 을 사용하여 Date 포맷 변환할 때,
     * 기본 TimeZone 을 UTC 로 설정 해준다. */
    fun Calendar.toDateFormat(
        format: String = COMMON_DATE_FORMAT,
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): String {
        try {
            val dateFormat = SimpleDateFormat(format).apply { this.timeZone = timeZone }
            val calTime = this.timeInMillis

            return dateFormat.format(calTime)
        } catch (e: Exception) {
            throw IllegalArgumentException("Error: Failed to convert Calendar to a specific DateFormat String.")
        }
    }

    /**
     * @param epoch : Unix time in UTC.
     * @param offsetHour : Hour offset value from the standard of UTC Time.
     * @param lang : the appropriate language type for the day of week.
     *              "ko" -> 한국어
     *              else -> 영어
     * */
    fun convertTime(epoch: Long, offsetHour: Float, lang: String = "en"): Calendar {
        Log.v(TAG, " #1-> convertTime 입력값 -> epoch: $epoch , offsetHour: $offsetHour")
//        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val cal = Calendar.getInstance(TimeZone.getDefault())
        cal.time = Date(epoch)
        Log.d(TAG, " #1-> input(epoch): $epoch -> InputDate: ${cal.time}")

        val hour = offsetHour.toInt()
        val minute: Float = offsetHour - hour
        val newMinutes: Int = (minute * 60).toInt()
        Log.d(TAG, "#1-> offset 시간 계산 -> ${hour}시간 & ${newMinutes}분[$minute]")

        cal.apply {
            val calMin = get(Calendar.MINUTE)
            set(Calendar.MINUTE, calMin + newMinutes)
            val calHour = get(Calendar.HOUR_OF_DAY)
            set(Calendar.HOUR_OF_DAY, calHour + hour)
        }
        with(cal) {
            Log.d(TAG, "캘린더 직접 변환 TEST: ${get(Calendar.YEAR)}-${get(Calendar.MONTH) + 1}-${get(Calendar.DATE)} ${get(Calendar.DAY_OF_WEEK).toDayOfWeek(lang)} ${get(Calendar.HOUR_OF_DAY)}:${get(Calendar.MINUTE)}:${get(Calendar.SECOND)} ")
        }

        return cal
    }

    fun convertTime2(epoch: Long, offsetHour: Float, Lang: String = "en") : Calendar {
        Log.v(TAG, " #2-> convertTime2 입력값 -> epoch: $epoch , offsetHour: $offsetHour")
        Log.d(TAG, " #2-> input(epoch): $epoch -> InputDate: ${Date(epoch)}")
        val hourAsMillis: Long = (offsetHour * 60 * 60 * 1000).toLong()
        val stdEpoch = hourAsMillis + epoch
        Log.v(TAG, " #2-> offsetHourInMillis : $hourAsMillis")

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = Date(stdEpoch)
        Log.d(TAG, "\n#2-> input(epoch+offset): $stdEpoch -> Date: ${cal.time}")

        return cal
    }

    private val koDayOfWeek = arrayOf("일", "월", "화", "수", "목", "금", "토")
    private val enDayOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    fun Int.toDayOfWeek(lang: String): String {
        if (this < 1 || this > 7) {
            throw IllegalArgumentException("Illegal Argument for the day of week. It only accept an integer between 1 and 7.")
        }
        val index = this - 1
//        Log.d(TAG, " ---> toDayOfWeek Input: $this, lang: $lang")
        return when (lang.lowercase()) {
            "ko" -> { koDayOfWeek[index] }
            else -> { enDayOfWeek[index] }
        }
    }
}