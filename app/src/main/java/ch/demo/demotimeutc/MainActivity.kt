package ch.demo.demotimeutc

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import ch.demo.demotimeutc.TimeUtil.toDateFormat
import ch.demo.demotimeutc.TimeUtil.toDayOfWeek
import ch.demo.demotimeutc.databinding.ActivityMainBinding
import org.intellij.lang.annotations.JdkConstants.CalendarMonth
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = this.javaClass.name
    val TAG_TIME = "TimeTest"

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var tzAsia: TimeZone? = null
        val date: Date = Date()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss E (z Z)")
        tzAsia = TimeZone.getTimeZone("Asia/Seoul")

        df.timeZone = tzAsia
        Log.d(TAG_TIME, " - ${tzAsia.displayName} :  ${df.format(date)} -> epoch: ${date.time}")

        val tzUtc: TimeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
        val dfUtc: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss E (z Z)")
        dfUtc.timeZone = tzUtc
        Log.i(TAG_TIME, " -> ${tzUtc.displayName} : ${dfUtc.format(date)} -> epoch: ${date.time}")


        val testDate: Long = date.time
        Log.w(TAG_TIME, " \n-> InputEpoch: $testDate \n-> DateIns: ${Date(testDate)}\n-> TimeZoneUTC:   ${dfUtc.format(testDate)} \n-> TimeZoneAsia: ${df.format(testDate)}")

        val test = 1687525095618
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.time = Date(test)       // 현재시간 -> 달력
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DATE)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK).toDayOfWeek("ko")
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        val mins = calendar.get(Calendar.MINUTE)
        val sec = calendar.get(Calendar.SECOND)

        Log.v("$TAG_TIME-Cal", " -> UTC:           ${year}년 ${month}월 ${day}일 ${dayOfWeek}요일 ${hour}시 ${mins}분 ${sec}초 -> calTime: ${calendar.timeInMillis}")
        val add = 9
        calendar.set(Calendar.HOUR_OF_DAY, hour + add)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        Log.d("$TAG_TIME-Cal", " -> 변환 후(+${add}hr) : ${year}년 ${month}월 ${day}일 ${dayOfWeek}요일 ${hour}시 ${mins}분 ${sec}초 -> calTime: ${calendar.timeInMillis}")

        // -------------------------------------------

        var datePickerDialog: DatePickerDialog
        binding.button.setOnClickListener {
            val startCalendar = Calendar.getInstance()
            val year = startCalendar.get(Calendar.YEAR)
            val month = startCalendar.get(Calendar.MONTH)
            val day = startCalendar.get(Calendar.DAY_OF_MONTH)

            datePickerDialog = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    val selected = "$year - $month - $dayOfMonth"
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    cal.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DATE, dayOfMonth)
                    }

                    val tzUtc: TimeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
                    val dfUtc: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss E")
                    dfUtc.timeZone = tzUtc
                    val selectedEpoch = cal.time.time
                    val log = "${tzUtc.displayName} : ${dfUtc.format(cal.time)} -> epoch: $selectedEpoch"
                    Log.i(TAG_TIME, "DatePicker -> $log")

                    Log.d(TAG, "DatePicker -> $selected -> cal: ${cal.time} ")

                    val offset = 10
                    val convert = TimeUtil.convertTime(selectedEpoch, offset, "ko").toDateFormat()
                    val convertEpoch = TimeUtil.convertTime(selectedEpoch, offset, "ko").time.time
                    Log.e(TAG, "변환 후 -> $convert")

                    val epochMinus = convertEpoch-selectedEpoch

                    val strBuilder = StringBuilder()
                    strBuilder.append("Epoch : (00:00:00 UTC on January 1, 1970) 부터의 시간\n\n")
                    strBuilder.append("선택값: \n")
                    strBuilder.append("$log\n\n\n")
                    strBuilder.append("변환후: \n")
                    strBuilder.append("[offset:$offset] $convert -> epoch: $convertEpoch \n")
                    strBuilder.append("Epoch1: $selectedEpoch \n")
                    strBuilder.append("Epoch2: $convertEpoch \n")
                    strBuilder.append("${epochMinus}ms (${epochMinus / 1000 / 60 / 60}hour)")

                    binding.textView.text = strBuilder.toString()
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }
}