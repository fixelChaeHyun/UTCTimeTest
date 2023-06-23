package ch.demo.demotimeutc

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    val TAG = this.javaClass.name

    fun Calendar.toDateFormat(
        format: String = "yyyy-MM-dd HH:mm:ss E (z Z)",
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
    fun convertTime(epoch: Long, offsetHour: Int, lang: String = "en"): Calendar {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = Date(epoch)
        Log.d(TAG, "input(epoch): $epoch -> time(UTC): ${cal.time}")

        cal.apply {
            val year = get(Calendar.YEAR)
            val month = get(Calendar.MONTH) + 1
            val date = get(Calendar.DATE)
            val dayOfWeek = get(Calendar.DAY_OF_WEEK).toDayOfWeek(lang)
            val hour = get(Calendar.HOUR_OF_DAY)
            val minute = get(Calendar.MINUTE)
            val second = get(Calendar.SECOND)
            set(Calendar.HOUR_OF_DAY, hour + offsetHour)
            Log.d(TAG, " -> (offset:${offsetHour}hr): ${year}-${month}-${date} $dayOfWeek $hour:$minute:$second ")
        }

        return cal
    }

    private val koDayOfWeek = arrayOf("일", "월", "화", "수", "목", "금", "토")
    private val enDayOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    fun Int.toDayOfWeek(lang: String): String {
        if (this < 1 || this > 7) {
            throw IllegalArgumentException("Illegal Argument for the day of week. It only accept an integer between 1 and 7.")
        }
        val index = this - 1
        Log.d(TAG, " ---> toDayOfWeek Input: $this, lang: $lang")
        return when (lang.lowercase()) {
            "ko" -> { koDayOfWeek[index] }
            else -> { enDayOfWeek[index] }
        }
    }
}