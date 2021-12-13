package edu.aku.hassannaqvi.naunehal_remidline.ui.sections

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.edittextpicker.aliazaz.EditTextPicker
import com.validatorcrawler.aliazaz.Clear
import com.validatorcrawler.aliazaz.Validator
import edu.aku.hassannaqvi.naunehal_remidline.R
import edu.aku.hassannaqvi.naunehal_remidline.contracts.ChildInformationContract
import edu.aku.hassannaqvi.naunehal_remidline.core.MainApp
import edu.aku.hassannaqvi.naunehal_remidline.databinding.ActivitySection02cbBinding
import edu.aku.hassannaqvi.naunehal_remidline.utils.datecollection.AgeModel
import edu.aku.hassannaqvi.naunehal_remidline.utils.datecollection.DateRepository.Companion.getCalculatedAge
import edu.aku.hassannaqvi.naunehal_remidline.utils.openWarningDialog
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Section02CBActivity : AppCompatActivity() {
    lateinit var bi: ActivitySection02cbBinding
    var dtFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_section_02cb)
        bi.cinfo = MainApp.childInformation
        setSupportActionBar(bi.toolbar)

        // TODO: After itemClick on childlist fetchChildByUID() from TABLE_FAMILY and update contents MainApp.Family before entering this activity.
        if (MainApp.form.hh14 == "1") bi.cb0601.isEnabled = false
        else bi.cb0602.isEnabled = false
        setupSkips()

        /*
         * Setup Listeners
         * */
        val txtListener = arrayOf<EditText>(bi.cb04dd, bi.cb04mm)
        for (txtItem in txtListener) {
            txtItem.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    bi.cb0501.text = null
                    bi.cb0502.text = null
                    bi.cb04yy.text = null
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    private fun setupSkips() {
        bi.cb06.setOnCheckedChangeListener { radioGroup: RadioGroup?, i: Int ->
            when (i) {
                bi.cb0601.id -> {
                    Clear.clearAllFields(bi.fldGrpCVcb07, false)
                    Clear.clearAllFields(bi.fldGrpCVcb08, false)
                    Clear.clearAllFields(bi.fldGrpCVcb09, false)
                    Clear.clearAllFields(bi.fldGrpCVcb10, false)
                    Clear.clearAllFields(bi.fldGrpCVcb11, false)
                    bi.fldGrpCVcb11.visibility = View.GONE
                    MainApp.childInformation.cb07 = MainApp.form.hh12
                    MainApp.childInformation.cb08 = MainApp.form.hh13
                    MainApp.childInformation.cb09 = MainApp.form.hh16
                    MainApp.childInformation.cb10 = MainApp.form.hh17


                    Clear.clearAllFields(bi.fldGrpCVcb12, true)
                    Clear.clearAllFields(bi.fldGrpCVcb13, true)
                    Clear.clearAllFields(bi.fldGrpCVcb14, true)
                    bi.cb1413.isEnabled = false

                    MainApp.childInformation.cb11 = "1"
                }
                bi.cb0602.id -> {
                    Clear.clearAllFields(bi.fldGrpCVcb12, false)
                    Clear.clearAllFields(bi.fldGrpCVcb13, false)
                    Clear.clearAllFields(bi.fldGrpCVcb14, false)

                    MainApp.childInformation.cb12 = MainApp.form.hh12
                    MainApp.childInformation.cb13 = MainApp.form.hh16
                    MainApp.childInformation.cb14 = MainApp.form.hh17

                    Clear.clearAllFields(bi.fldGrpCVcb07, true)
                    Clear.clearAllFields(bi.fldGrpCVcb08, true)
                    Clear.clearAllFields(bi.fldGrpCVcb09, true)
                    Clear.clearAllFields(bi.fldGrpCVcb10, true)
                    Clear.clearAllFields(bi.fldGrpCVcb11, true)

                    bi.fldGrpCVcb11.visibility = View.VISIBLE
                }
                else -> {
                    Clear.clearAllFields(bi.fldGrpCVcb12, true)
                    Clear.clearAllFields(bi.fldGrpCVcb13, true)
                    Clear.clearAllFields(bi.fldGrpCVcb14, true)
                    Clear.clearAllFields(bi.fldGrpCVcb07, true)
                    Clear.clearAllFields(bi.fldGrpCVcb08, true)
                    Clear.clearAllFields(bi.fldGrpCVcb09, true)
                    Clear.clearAllFields(bi.fldGrpCVcb10, true)
                    Clear.clearAllFields(bi.fldGrpCVcb11, true)
                    bi.cb1413.isEnabled = false
                    bi.fldGrpCVcb11.visibility = View.VISIBLE
                }
            }
        }

        txtWatch(bi.cb09)
        txtWatch(bi.cb13)

    }

    private fun txtWatch(edx: EditTextPicker) {
        edx.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edx.text.toString().isNotEmpty()) {
                    if (Integer.parseInt(edx.text.toString()) == 22)
                        edx.rangedefaultvalue = 22f
                    if (Integer.parseInt(edx.text.toString()) == 55)
                        edx.rangedefaultvalue = 55f
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun BtnContinue(view: View) {
        if (!formValidation()) return
        saveDraft()
        if (updateDB()) {
            finish()
        }
    }

    private fun updateDB(): Boolean {
        val db = MainApp.appInfo.dbHelper
        if (!MainApp.childInformation.isEditFlag) {
            val updcount = db.addChildInformation(MainApp.childInformation)
            return if (updcount > 0) {
                MainApp.childInformation.id = updcount.toString()
                MainApp.childInformation.uid =
                    MainApp.childInformation.deviceId + MainApp.childInformation.id
                var count = db.updatesChildInformationColumn(
                    ChildInformationContract.ChildInfoTable.COLUMN_UID,
                    MainApp.childInformation.uid
                )
                if (count > 0) count = db.updatesChildInformationColumn(
                    ChildInformationContract.ChildInfoTable.COLUMN_SCB,
                    MainApp.childInformation.sCBtoString()
                )
                if (count > 0) true else {
                    Toast.makeText(
                        this, "Sorry. You can't go further.\n" +
                                " Please contact IT Team (Failed to update DB)", Toast.LENGTH_SHORT
                    ).show()
                    false
                }
            } else {
                Toast.makeText(
                    this,
                    "Sorry. You can't go further.\n Please contact IT Team (Failed to update DB)",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
        } else {
            val updcount = db.updatesChildInformationColumn(
                ChildInformationContract.ChildInfoTable.COLUMN_SCB,
                MainApp.childInformation.sCBtoString()
            )
            return if (updcount > 0) true else {
                Toast.makeText(
                    this, "Sorry. You can't go further.\n" +
                            " Please contact IT Team (Failed to update DB)", Toast.LENGTH_SHORT
                ).show()
                false
            }
        }
    }

    private fun formValidation(): Boolean {
        if (!Validator.emptyCheckingContainer(this, bi.GrpName)) return false
        if (bi.cb0501.text.toString().toInt().plus(bi.cb0502.text.toString().toInt()) == 0)
            return Validator.emptyCustomTextBox(
                this,
                bi.cb0501,
                "Both year and month couldn't be zero!"
            )
        val totalMonths = bi.cb0501.text.toString().toInt() * 12 + bi.cb0502.text.toString().toInt()
        if (totalMonths > 59) {
            this.openWarningDialog(
                "Warning",
                "Add children having age of less then or equal to 59 Months"
            )
            return false
        }
        return true
    }

    fun BtnEnd(view: View) {
        finish()
    }


    private fun saveDraft() {
        if (!MainApp.childInformation.isEditFlag) {
            MainApp.childInformation.sysDate =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date().time)
            MainApp.childInformation.uuid = MainApp.form.uid
            MainApp.childInformation.userName = MainApp.user.userName
            MainApp.childInformation.dcode = MainApp.form.dcode
            MainApp.childInformation.ucode = MainApp.form.ucode
            MainApp.childInformation.cluster = MainApp.form.cluster
            MainApp.childInformation.hhno = MainApp.form.hhno
            MainApp.childInformation.deviceId = MainApp.appInfo.deviceID
            MainApp.childInformation.deviceTag = MainApp.appInfo.tagName
            MainApp.childInformation.appver = MainApp.appInfo.appVersion
        }

    }


    fun cb04yyOnTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        bi.cb0502.isEnabled = false
        bi.cb0502.text = null
        bi.cb0501.isEnabled = false
        bi.cb0501.text = null
        MainApp.childInformation.calculatedDOB = null
        if (TextUtils.isEmpty(bi.cb04dd.text) || TextUtils.isEmpty(bi.cb04mm.text) || TextUtils.isEmpty(
                bi.cb04yy.text
            )
        ) return
        if (!bi.cb04dd.isRangeTextValidate || !bi.cb04mm.isRangeTextValidate || !bi.cb04yy.isRangeTextValidate) return
        if (bi.cb04dd.text.toString() == "98" && bi.cb04mm.text.toString() == "98" && bi.cb04yy.text.toString() == "9998") {
            bi.cb0502.isEnabled = true
            bi.cb0501.isEnabled = true
            dtFlag = true
            return
        }
        val day = if (bi.cb04dd.text.toString() == "98") 15 else bi.cb04dd.text.toString().toInt()
        val month = bi.cb04mm.text.toString().toInt()
        val year = bi.cb04yy.text.toString().toInt()
        val age: AgeModel?
        age = if (MainApp.form.localDate != null) getCalculatedAge(
            MainApp.form.localDate,
            year,
            month,
            day
        ) else getCalculatedAge(year = year, month = month, day = day)
        if (age == null) {
            bi.cb04yy.error = "Invalid date!!"
            dtFlag = false
            return
        }
        dtFlag = true
        bi.cb0502.setText(age.month.toString())
        bi.cb0501.setText(age.year.toString())

        //Setting Date
        try {
            val instant = Instant.parse(
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(
                    SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(
                        "$day-$month-$year"
                    )
                ) + "T06:24:01Z"
            )
            MainApp.childInformation.calculatedDOB =
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}