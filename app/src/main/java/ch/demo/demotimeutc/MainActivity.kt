package ch.demo.demotimeutc

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ch.demo.demotimeutc.TimeUtil.toDateFormat
import ch.demo.demotimeutc.TimeUtil.toDayOfWeek
import ch.demo.demotimeutc.databinding.ActivityMainBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = this.javaClass.name
    val TAG_TIME = "TimeTest"

    lateinit var binding: ActivityMainBinding

    val spinnerItems: Array<Float> = arrayOf(6f, 7f, 8.5f, 8.75f, 9f, 10f, 10.5f, 12.75f, -3.5f, -6.0f -10.5f, -11f, -12f)

    var selectOffset: Float = 9.0f

    var shareCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //----------- Spinner setup ----------
        val spinnerAdapter = ArrayAdapter<Float>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerItems.asList())
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "- position: $position , item: ${spinnerItems[position]}")
                selectOffset = spinnerItems[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // ---------- Time Picker ----------
        var timePickerDialog: TimePickerDialog
        // ---------- Date Picker ----------
        var datePickerDialog: DatePickerDialog
        binding.button.setOnClickListener {
            val startCalendar = Calendar.getInstance()
            val year = startCalendar.get(Calendar.YEAR)
            val month = startCalendar.get(Calendar.MONTH)
            val day = startCalendar.get(Calendar.DAY_OF_MONTH)
            val hour = startCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = startCalendar.get(Calendar.MINUTE)

            datePickerDialog = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    val selected = "$year - $month - $dayOfMonth"
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    Log.d(TAG, "DatePicker -> $selected -> cal: ${cal.time} ")
                    cal.time = Date()
                    cal.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DATE, dayOfMonth)
                    }
                    // TimePicker -
                    shareCalendar = cal
                    createTimePicker(this, hour, minute)

                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    fun createTimePicker(context: Context, hour: Int, minute: Int) {
        TimePickerDialog(
            context,
            { view, hour, minute ->
                shareCalendar.set(Calendar.HOUR_OF_DAY, hour)
                shareCalendar.set(Calendar.MINUTE, minute)

                val selected = "-> ${hour}시 ${minute}분"
                Log.d(TAG, "TimePicker -> $selected -> cal: ${shareCalendar.time} ")

                work(shareCalendar)
            }
            ,hour, minute, false

        ).show()
    }

    fun work(cal: Calendar) {
        val tzUtc: TimeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
        val dfUtc: DateFormat = SimpleDateFormat(TimeUtil.COMMON_DATE_FORMAT).apply { timeZone = tzUtc }    // SimpleDateFormat 에 timezone UTC 설정하기
        val selectedEpoch = cal.time.time
        val log = "${tzUtc.displayName} : ${dfUtc.format(cal.time)}\n ---> epoch: $selectedEpoch"

        val defaultDateFormat = SimpleDateFormat(TimeUtil.COMMON_DATE_FORMAT)
        val offset: Float = selectOffset
        // Test 1 # Calendar 의 Set 사용

        val convertCal = TimeUtil.convertTime(selectedEpoch, offset, "ko")
        val convertDateFormat = convertCal.toDateFormat()
        val convertEpoch = convertCal.time.time
        Log.e(TAG, "변환 후(#1) -> $convertDateFormat")
        Log.d(TAG, " 변환 후(#1) epoch -> $convertEpoch")
        Log.d(TAG, " 변환 후(#1) defaultDateFormat -> ${defaultDateFormat.format(convertCal.time)}")

        Log.i(TAG, "\n --------------------------------------------------------------------- \n")

        // Test 2 # epoch Long 으로 더해서 사용
        val convertCal2 = TimeUtil.convertTime2(selectedEpoch, offset, "ko")
        val convertDateFormat2 = convertCal2.toDateFormat()
        val convertEpoch2 = convertCal2.time.time
        Log.w(TAG, " 변환 후(#2) -> $convertDateFormat2")
        Log.d(TAG, " 변환 후(#2) epoch -> $convertEpoch2")
        Log.d(TAG, " 변환 후(#2) defaultDateFormat -> ${defaultDateFormat.format(convertCal2.time)}")

        val epochMinus = convertEpoch2-selectedEpoch
        val offsetMillis: Int = (offset * 60 * 60 * 1000).toInt()
        val log2 = "[offset: ${offset}Hr] == $offsetMillis ms\n" +
                " ---> ${convertDateFormat2}\n" +
                " ---> epoch: $selectedEpoch\n" +
                " ---> epochMinus : $epochMinus\n" +
                " -> ${defaultDateFormat.format(convertCal2.time)}"

        val strBuilder = StringBuilder()
        strBuilder.append("선택값: \n")
        strBuilder.append("$log\n")
        strBuilder.append("\n\n  -----------------------------------  \n\n")
        strBuilder.append("변환 후: \n\n")
        strBuilder.append("$log2")

        binding.textView.text = strBuilder.toString()
    }
}