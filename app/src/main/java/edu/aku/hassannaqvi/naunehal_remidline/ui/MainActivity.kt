package edu.aku.hassannaqvi.naunehal_remidline.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import edu.aku.hassannaqvi.naunehal_remidline.BuildConfig
import edu.aku.hassannaqvi.naunehal_remidline.CONSTANTS
import edu.aku.hassannaqvi.naunehal_remidline.R
import edu.aku.hassannaqvi.naunehal_remidline.base.repository.GeneralRepository
import edu.aku.hassannaqvi.naunehal_remidline.base.repository.ResponseStatus
import edu.aku.hassannaqvi.naunehal_remidline.base.viewmodel.MainViewModel
import edu.aku.hassannaqvi.naunehal_remidline.core.MainApp
import edu.aku.hassannaqvi.naunehal_remidline.database.AndroidDatabaseManager
import edu.aku.hassannaqvi.naunehal_remidline.database.DatabaseHelper
import edu.aku.hassannaqvi.naunehal_remidline.databinding.ActivityMainBinding
import edu.aku.hassannaqvi.naunehal_remidline.ui.list_activity.FormsReportCluster
import edu.aku.hassannaqvi.naunehal_remidline.ui.list_activity.FormsReportDate
import edu.aku.hassannaqvi.naunehal_remidline.ui.login_activity.LoginActivity
import edu.aku.hassannaqvi.naunehal_remidline.ui.sections.IdentificationSectionActivity
import edu.aku.hassannaqvi.naunehal_remidline.ui.sections.Section01HHActivity
import edu.aku.hassannaqvi.naunehal_remidline.ui.sections.Section02CBActivity
import edu.aku.hassannaqvi.naunehal_remidline.utils.extension.gotoActivity
import edu.aku.hassannaqvi.naunehal_remidline.utils.extension.gotoActivityWithNoHistory
import edu.aku.hassannaqvi.naunehal_remidline.utils.extension.obtainViewModel
import edu.aku.hassannaqvi.naunehal_remidline.utils.isNetworkConnected
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var bi: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    private var exit = false
    private var sysdateToday = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date())
    private var district = mutableListOf("....")
    private var districtCode = mutableListOf<String>()
    private lateinit var districtAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bi = DataBindingUtil.setContentView(this, R.layout.activity_main)
        bi.callback = this
        setSupportActionBar(bi.toolbar)
        if (MainApp.admin) bi.adminSection.visibility = View.VISIBLE
        viewModel =
            obtainViewModel(MainViewModel::class.java, GeneralRepository(DatabaseHelper(this)))


        /*
        * Setting Adapters
        * */
        districtAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, district)
        bi.districts.adapter = districtAdapter

        /*
        * Calling viewmodel district data function
        * Fetch district result response
        * */
        viewModel.districtResponse.observe(this) {
            it?.let {
                when (it.status) {
                    ResponseStatus.SUCCESS -> {
                        lifecycleScope.launch {
                            district.clear()
                            districtCode.clear()
                            district.add("....")
                            it.data?.forEach { item ->
                                //
                                if (item.districtCode == MainApp.user.dist_id) {
                                    district.add(item.districtName)
                                    districtCode.add(item.districtCode)
                                } else if (MainApp.admin || MainApp.user.userName == "test1234") {
                                    district.add(item.districtName)
                                    districtCode.add(item.districtCode)
                                }
                            }
                            districtAdapter.notifyDataSetChanged()

                            bi.btnDownloadDistrict.visibility = View.VISIBLE
                        }
                    }
                    ResponseStatus.ERROR -> {
                        bi.btnDownloadDistrict.visibility = View.VISIBLE
                        Log.d("district: ", "false")
                    }
                    ResponseStatus.LOADING -> {
                        lifecycleScope.launch {
                            bi.btnDownloadDistrict.visibility = View.GONE
                            delay(2000)
                        }
                    }
                }
            }
        }


        /*
        * Get Today's form from DB
        * If it's null then return 0 otherwise return count
        * Show loading while data is fetching
        * */
        viewModel.todayForms.observe(this) {
            when (it.status) {
                ResponseStatus.SUCCESS -> {
                    Log.d("Today's form count:", it.data.toString())
                    bi.statisticLayout.tf.text = it.data.toString()
                }
                ResponseStatus.ERROR -> {
                }
                ResponseStatus.LOADING -> {
                    lifecycleScope.launch { delay(1000) }
                }
            }
        }

        /*
        * Get Form status from DB
        * If it's null then return 0 otherwise return count
        * Show loading while data is fetching
        * */
        viewModel.formsStatus.observe(this) {
            when (it.status) {
                ResponseStatus.SUCCESS -> {
                    it.data?.let { item ->
                        Log.d("Complete count:", item.closedForms.toString())
                        Log.d("In-complete count:", item.openedForms.toString())
                        bi.statisticLayout.cf.text = String.format("%02d", item.closedForms)
                        bi.statisticLayout.icf.text = String.format("%02d", item.openedForms)
                    }

                }
                ResponseStatus.ERROR -> {
                    animateFadeOut()
                    Log.d("Status", "error")
                }
                ResponseStatus.LOADING -> {
                    lifecycleScope.launch { delay(1000) }
                }
            }
        }

        /*
        * Get Upload & Download status of form from DB
        * If it's null then return 0 otherwise return count
        * Show loading while data is fetching
        * */
        viewModel.uploadForms.observe(this) {
            when (it.status) {
                ResponseStatus.SUCCESS -> {
                    it.data?.let { item ->
                        Log.d("Synced count:", item.closedForms.toString())
                        Log.d("Un-Synced count:", item.openedForms.toString())
                        bi.statisticLayout.sf.text = item.closedForms.toString()
                        bi.statisticLayout.usf.text = item.openedForms.toString()
                    }
                    animateFadeOut()
                }
                ResponseStatus.ERROR -> {
                    animateFadeOut()
                    Log.d("Sync", "error")
                }
                ResponseStatus.LOADING -> {
                    lifecycleScope.launch { delay(2000) }
                }
            }
        }

        setupSkips()
    }

    fun showDebugDBAddressLogToast(context: Context?) {
        if (BuildConfig.DEBUG) {
            try {
                val debugDB = Class.forName("com.amitshekhar.DebugDB")
                val getAddressLog = debugDB.getMethod("getAddressLog")
                val value = getAddressLog.invoke(null)
                Toast.makeText(context, value as String, Toast.LENGTH_LONG).show()
            } catch (ignore: Exception) {
            }
        }
    }

    private fun setupSkips() {
        bi.districts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    bi.btnDownloadDistrict.isEnabled = false
                    return
                }
                bi.btnDownloadDistrict.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /*
    * Back press button that will route to login activity after pressing -
    * back button two times
    * */
    override fun onBackPressed() {
        if (exit) {
            gotoActivityWithNoHistory(LoginActivity::class.java)
        } else {
            Toast.makeText(
                this, "Press back again to exit",
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler(Looper.getMainLooper()).postDelayed({ exit = false }, 3000)
        }
    }

    /*
    * Inflate menu on current activity
    * */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /*
    * Menu items selection
    * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_data_sync -> {
                if (isNetworkConnected(this)) {
                    gotoActivity(SyncActivity::class.java)

                } else
                    Toast.makeText(this, "Network connection not available!", Toast.LENGTH_SHORT)
                        .show()

                return true
            }
            R.id.formsReportDate -> {
                gotoActivity(FormsReportDate::class.java)
                return true
            }

            R.id.formsReportCluster -> {
                gotoActivity(FormsReportCluster::class.java)
                return true
            }
            R.id.action_database -> {
                gotoActivity(AndroidDatabaseManager::class.java)
                return true
            }
            R.id.debugDB -> {
                showDebugDBAddressLogToast(applicationContext)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /*
    * Route to specific activity according to selection
    * For uploading/downloading data, the network needs to work
    * */
    fun openSpecificActivity(v: View) {
        when (v.id) {
            R.id.formA -> {
                gotoActivity(Section01HHActivity::class.java)
            }
            R.id.btn01 -> {
                gotoActivity(Section01HHActivity::class.java)
            }
            R.id.btn02 -> {
                gotoActivity(Section02CBActivity::class.java)
            }
            R.id.editForm -> {
                gotoActivity(IdentificationSectionActivity::class.java)
            }
            R.id.databaseBtn -> startActivity(Intent(this, AndroidDatabaseManager::class.java))
            R.id.btn_download_district -> {
                if (isNetworkConnected(this)) {
                    startActivity(
                        Intent(this@MainActivity, SyncActivity::class.java)
                            .putExtra(CONSTANTS.SYNC_LOGIN, true)
                            .putExtra(
                                CONSTANTS.SYNC_DISTRICTID_LOGIN,
                                districtCode[bi.districts.selectedItemPosition - 1]
                            )
                    )
                } else
                    Toast.makeText(this, "Network connection not available!", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        animateFadeIn()
        viewModel.getDistrictFromDB()
//        viewModel.getTodayForms(sysdateToday)
//        viewModel.getUploadFormsStatus()
//        viewModel.getFormsStatus(sysdateToday)
        viewModel.getFormsStatusUploadStatus(sysdateToday)
    }

    /*
    * Stop animation on statistic Layout
    * */
    private fun animateFadeOut() {
        val shortAnimationDuration = 2000
        /*
        * Animate the content view to 100% opacity, and clear any animation
        * listener set on the view.
        * */
        bi.statisticLayout.syncLinearLayout.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(null)
        bi.statisticLayout.statusLinearLayout.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(null)

        /* 
        * Animate the loading view to 0% opacity. After the animation ends, 
        * set its visibility to GONE as an optimization step (it won't participate 
        * in layout passes, etc.)
        * */
        bi.statisticLayout.loading.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    bi.statisticLayout.loading.visibility = View.GONE
                }
            })
    }

    /*
    * Start animation on statistic Layout
    * */
    private fun animateFadeIn() {
        bi.statisticLayout.syncLinearLayout.alpha = 0f
        bi.statisticLayout.statusLinearLayout.alpha = 0f
        bi.statisticLayout.loading.alpha = 1f
        bi.statisticLayout.loading.visibility = View.VISIBLE
    }


}