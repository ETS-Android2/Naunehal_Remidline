package edu.aku.hassannaqvi.naunehal_remidline.ui.sections

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.validatorcrawler.aliazaz.Clear
import com.validatorcrawler.aliazaz.Validator
import edu.aku.hassannaqvi.naunehal_remidline.CONSTANTS
import edu.aku.hassannaqvi.naunehal_remidline.R
import edu.aku.hassannaqvi.naunehal_remidline.contracts.ChildContract
import edu.aku.hassannaqvi.naunehal_remidline.core.MainApp
import edu.aku.hassannaqvi.naunehal_remidline.databinding.ActivitySection03csBinding
import edu.aku.hassannaqvi.naunehal_remidline.models.Child
import edu.aku.hassannaqvi.naunehal_remidline.models.ChildCard
import edu.aku.hassannaqvi.naunehal_remidline.models.ChildInformation
import edu.aku.hassannaqvi.naunehal_remidline.utils.EndSectionActivity
import edu.aku.hassannaqvi.naunehal_remidline.utils.contextEndActivity
import edu.aku.hassannaqvi.naunehal_remidline.utils.convertStringToUpperCase
import edu.aku.hassannaqvi.naunehal_remidline.utils.extension.gotoActivity
import edu.aku.hassannaqvi.naunehal_remidline.utils.shortStringLength
import java.text.SimpleDateFormat
import java.util.*

class Section03CSActivity : AppCompatActivity(), EndSectionActivity {
    lateinit var bi: ActivitySection03csBinding
    private val info: ChildInformation by lazy {
        intent.getSerializableExtra(CONSTANTS.CHILD_DATA_UNDER5) as ChildInformation
    }

    companion object {
        lateinit var selectedChildInfo: ChildInformation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_section_03cs)
        bi.mainCard.childCard = ChildCard(
            info.cb02.convertStringToUpperCase().shortStringLength(),
            String.format("Mother: %s", info.cb07.convertStringToUpperCase().shortStringLength()),
            info.cb03.toInt()
        )
        MainApp.child = Child(
            info.cb01,
            info.cb02,
            info.cb07,
            info.uid
        )
        bi.child = MainApp.child
        setSupportActionBar(bi.toolbar)
        selectedChildInfo = info
        setupSkips()

        if (info.isMotherAvailable) {
            bi.fldGrpCVcs02a.visibility = View.GONE
            bi.fldGrpCVcs02b.visibility = View.GONE
        }
    }

    private fun setupSkips() {
        bi.cs02a.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            Clear.clearAllFields(bi.fldGrpCS02)
            bi.fldGrpCS02.visibility = View.VISIBLE
            if (i == bi.cs02a04.id) {
                bi.fldGrpCS02.visibility = View.GONE
                Clear.clearAllFields(bi.fldGrpCS02)
            }
        }

        bi.cs03.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            Clear.clearAllFields(bi.llcs03)
            bi.llcs03.visibility = View.VISIBLE
            if (i == bi.cs0302.id) {
                bi.llcs03.visibility = View.GONE
            }
        }

        bi.cs06.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            Clear.clearAllFields(bi.fldGrpCVcs07)
            Clear.clearAllFields(bi.fldGrpCVcs08)
            Clear.clearAllFields(bi.fldGrpCVcs08a)
            Clear.clearAllFields(bi.fldGrpCVcs08b)
            Clear.clearAllFields(bi.fldGrpCVcs09)
            Clear.clearAllFields(bi.fldGrpCVcs10)
            Clear.clearAllFields(bi.fldGrpCVcs11)
            bi.fldGrpCVcs07.visibility = View.VISIBLE
            bi.fldGrpCVcs08.visibility = View.VISIBLE
            bi.fldGrpCVcs08a.visibility = View.VISIBLE
            bi.fldGrpCVcs08b.visibility = View.VISIBLE
            bi.fldGrpCVcs09.visibility = View.VISIBLE
            bi.fldGrpCVcs10.visibility = View.VISIBLE
            bi.fldGrpCVcs11.visibility = View.VISIBLE
            if (i == bi.cs0602.id) {
                bi.fldGrpCVcs07.visibility = View.GONE
                bi.fldGrpCVcs08.visibility = View.GONE
                bi.fldGrpCVcs08a.visibility = View.GONE
                bi.fldGrpCVcs08b.visibility = View.GONE
            } else if (i == bi.cs0601.id) {
                bi.fldGrpCVcs10.visibility = View.GONE
                bi.fldGrpCVcs11.visibility = View.GONE
                bi.fldGrpCVcs09.visibility = View.GONE
            }
        }

        bi.cs08a.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            Clear.clearAllFields(bi.fldGrpCVcs08b)
            bi.fldGrpCVcs08b.visibility = View.VISIBLE
            if (i == bi.cs08ab.id) {
                bi.fldGrpCVcs08b.visibility = View.GONE
            }
        }

        bi.cs16.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            Clear.clearAllFields(bi.fldGrpCVcs17)
            Clear.clearAllFields(bi.fldGrpCVcs18)
            Clear.clearAllFields(bi.fldGrpCVcs18a)
            Clear.clearAllFields(bi.fldGrpCVcs18b)
            Clear.clearAllFields(bi.fldGrpCVcs19)
            if (i == bi.cs1601.id) {
                bi.fldGrpCVcs17.visibility = View.VISIBLE
                bi.fldGrpCVcs18.visibility = View.VISIBLE
                bi.fldGrpCVcs18a.visibility = View.VISIBLE
                bi.fldGrpCVcs18b.visibility = View.VISIBLE
                bi.fldGrpCVcs19.visibility = View.GONE
            } else if (i == bi.cs1602.id) {
                bi.fldGrpCVcs17.visibility = View.GONE
                bi.fldGrpCVcs18.visibility = View.GONE
                bi.fldGrpCVcs18a.visibility = View.GONE
                bi.fldGrpCVcs18b.visibility = View.GONE
                bi.fldGrpCVcs19.visibility = View.VISIBLE
            }
        }

        bi.cs18a.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            Clear.clearAllFields(bi.fldGrpCVcs18b)
            bi.fldGrpCVcs18b.visibility = View.VISIBLE
            if (i == bi.cs18ab.id) {
                bi.fldGrpCVcs18b.visibility = View.GONE
            }
        }

        bi.cs20.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            if (i == bi.cs2001.id) {
                bi.fldGrpCVcs21.visibility = View.VISIBLE
            } else {
                Clear.clearAllFields(bi.fldGrpCVcs21)
                bi.fldGrpCVcs21.visibility = View.GONE
            }
        }

        radioGroup(bi.cs12)
        radioGroup(bi.cs13)
        radioGroup(bi.cs14)

        /*
        * Skip for child age < 6 months
        * */
        if ((info.cb0501.toInt() * 12) + Integer.parseInt(info.cb0502) < 6) {
            bi.fldGrpCVcs20.visibility = View.GONE
            bi.fldGrpCVcs21.visibility = View.GONE
        }
    }

    private fun radioGroup(grp: RadioGroup) {
        grp.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            Clear.clearAllFields(bi.fldGrpCVcs15)
            Clear.clearAllFields(bi.fldGrpCVcs16)
            Clear.clearAllFields(bi.fldGrpCVcs17)
            Clear.clearAllFields(bi.fldGrpCVcs18)
            Clear.clearAllFields(bi.fldGrpCVcs18a)
            Clear.clearAllFields(bi.fldGrpCVcs18b)
            Clear.clearAllFields(bi.fldGrpCVcs19)
            Clear.clearAllFields(bi.fldGrpCVcs20)
            Clear.clearAllFields(bi.fldGrpCVcs21)
            bi.fldGrpCVcs15.visibility = View.VISIBLE
            bi.fldGrpCVcs16.visibility = View.VISIBLE
            bi.fldGrpCVcs17.visibility = View.VISIBLE
            bi.fldGrpCVcs18.visibility = View.VISIBLE
            bi.fldGrpCVcs18a.visibility = View.VISIBLE
            bi.fldGrpCVcs18b.visibility = View.VISIBLE
            bi.fldGrpCVcs19.visibility = View.VISIBLE
            bi.fldGrpCVcs20.visibility = View.VISIBLE
            bi.fldGrpCVcs21.visibility = View.VISIBLE
            if (bi.cs1202.isChecked
                && bi.cs1302.isChecked
                && bi.cs1402.isChecked
            ) {
                bi.fldGrpCVcs15.visibility = View.GONE
                bi.fldGrpCVcs16.visibility = View.GONE
                bi.fldGrpCVcs17.visibility = View.GONE
                bi.fldGrpCVcs18.visibility = View.GONE
                bi.fldGrpCVcs18a.visibility = View.GONE
                bi.fldGrpCVcs18b.visibility = View.GONE
                bi.fldGrpCVcs19.visibility = View.GONE
                bi.fldGrpCVcs20.visibility = View.GONE
                bi.fldGrpCVcs21.visibility = View.GONE
            } else if (bi.cs1402.isChecked) {
                bi.fldGrpCVcs15.visibility = View.GONE
            }
        }
    }

    fun BtnContinue(view: View) {
        if (!formValidation()) return
        saveDraft()
        MainApp.child.status = "1"
        if (updateDB()) {
            finish()
            if (info.isMotherAvailable) {
                if (info.isUnder35)
                    gotoActivity(Section04IMActivity::class.java)
                else if (info.isSelected == "1")
                    gotoActivity(Section05PDActivity::class.java)
            } else if (!bi.cs02a04.isChecked)
                if (info.isUnder35)
                    gotoActivity(Section04IMActivity::class.java)
                else if (info.isSelected == "1")
                    gotoActivity(Section07CVActivity::class.java)
        }
    }

    private fun updateDB(): Boolean {
        val db = MainApp.appInfo.dbHelper
        val updcount = db.addChild(MainApp.child)
        MainApp.child.id = updcount.toString()
        return if (updcount > 0) {
            MainApp.child.uid = MainApp.child.deviceId + MainApp.child.id
            var count =
                db.updatesChildColumn(ChildContract.ChildTable.COLUMN_UID, MainApp.child.uid)
            if (count > 0) count = db.updatesChildColumn(
                ChildContract.ChildTable.COLUMN_SCS,
                MainApp.child.s03CStoString()
            )
            if (count > 0) true else {
                Toast.makeText(this, "SORRY! Failed to update DB)", Toast.LENGTH_SHORT).show()
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
    }


    private fun saveDraft() {
        MainApp.child.sysDate =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(Date().time)
        MainApp.child.uuid = MainApp.form.uid
        MainApp.child.userName = MainApp.user.userName
        MainApp.child.dcode = MainApp.form.dcode
        MainApp.child.ucode = MainApp.form.ucode
        MainApp.child.cluster = MainApp.form.cluster
        MainApp.child.hhno = MainApp.form.hhno
        MainApp.child.deviceId = MainApp.appInfo.deviceID
        MainApp.child.deviceTag = MainApp.appInfo.tagName
        MainApp.child.appver = MainApp.appInfo.appVersion
        MainApp.child.serial = info.cb01
        MainApp.child.childname = info.cb02
        MainApp.child.mothername = info.cb07

        MainApp.child.cs01 = info.cb01
        MainApp.child.cs02 = info.cb02

    }


    private fun formValidation(): Boolean {
        return Validator.emptyCheckingContainer(this, bi.GrpName)
    }


    fun BtnEnd(view: View) {
        contextEndActivity(this)
    }


    override fun endSecActivity(flag: Boolean) {
        saveDraft()
        MainApp.child.status = "2"
        if (updateDB()) {
            finish()
        }
    }


    override fun onBackPressed() {
        Toast.makeText(this, "Back Press Not Allowed", Toast.LENGTH_SHORT).show()
    }

}