package edu.aku.hassannaqvi.naunehal_remidline.ui.sections

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.kennyc.view.MultiStateView
import com.leinardi.android.speeddial.SpeedDialActionItem
import edu.aku.hassannaqvi.naunehal_remidline.R
import edu.aku.hassannaqvi.naunehal_remidline.adapters.ChildListAdapter
import edu.aku.hassannaqvi.naunehal_remidline.base.repository.GeneralRepository
import edu.aku.hassannaqvi.naunehal_remidline.base.repository.ResponseStatus
import edu.aku.hassannaqvi.naunehal_remidline.base.viewmodel.ChildListViewModel
import edu.aku.hassannaqvi.naunehal_remidline.core.MainApp
import edu.aku.hassannaqvi.naunehal_remidline.database.DatabaseHelper
import edu.aku.hassannaqvi.naunehal_remidline.databinding.ActivityChildrenListBinding
import edu.aku.hassannaqvi.naunehal_remidline.models.ChildInformation
import edu.aku.hassannaqvi.naunehal_remidline.utils.WarningActivityInterface
import edu.aku.hassannaqvi.naunehal_remidline.utils.extension.gotoActivity
import edu.aku.hassannaqvi.naunehal_remidline.utils.extension.obtainViewModel
import edu.aku.hassannaqvi.naunehal_remidline.utils.openSectionEndingActivity
import edu.aku.hassannaqvi.naunehal_remidline.utils.openWarningActivity
import java.util.*

class ChildrenListActivity : AppCompatActivity(), WarningActivityInterface {

    lateinit var adapter: ChildListAdapter
    lateinit var bi: ActivityChildrenListBinding
    lateinit var viewModel: ChildListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_children_list)
        viewModel =
            obtainViewModel(ChildListViewModel::class.java, GeneralRepository(DatabaseHelper(this)))
        callingRecyclerView()

        /*
        * Nested Toolbar
        * */
        bi.toolbarLayout.title = "Children List (${MainApp.form.hhno})"
        bi.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.black))
        bi.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.blackOverlay))


        /*
        * Setting Floating button functionality
        * */
        val actionItems = mutableListOf<SpeedDialActionItem>(
            SpeedDialActionItem.Builder(R.id.fab_exit, R.drawable.ic_exit).setLabel("Force exit")
                .create(),
            SpeedDialActionItem.Builder(R.id.fab_finish, R.drawable.ic_finish)
                .setLabel("Next Section").create(),
            SpeedDialActionItem.Builder(R.id.fab_add, R.drawable.ic_add).setLabel("Add Children")
                .create()
        )
        bi.speedDial.addAllActionItems(actionItems)
        bi.speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_add -> {
                    MainApp.childInformation =
                        ChildInformation((adapter.childItems.size + 1).toString())
                    gotoActivity(Section02CBActivity::class.java)
                }
                R.id.fab_finish -> {
                    if (adapter.childItems.size == 0) {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Please add children's for proceeding to the next section",
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                        return@setOnActionSelectedListener false
                    }
                    /*if (adapter.childItems.find { it.isMotherAvailable } == null) {
                        Snackbar.make(findViewById(android.R.id.content), "Please add Mother available children for proceeding to the next section", Snackbar.LENGTH_LONG)
                                .show()
                        return@setOnActionSelectedListener false
                    }*/
                    childSelection()
                }
                R.id.fab_exit -> {
                    openSectionEndingActivity()
                    return@setOnActionSelectedListener true // false will close it without animation
                }
            }
            false
        }


        /*
        * Fetch child list
        * */
        bi.multiStateView.viewState = MultiStateView.ViewState.EMPTY
        viewModel.childResponse.observe(this, {
            it?.let {
                when (it.status) {
                    ResponseStatus.SUCCESS -> {
                        adapter.childItems = it.data as ArrayList<ChildInformation>
                        bi.multiStateView.viewState = MultiStateView.ViewState.CONTENT
                    }
                    ResponseStatus.ERROR -> {
                        bi.multiStateView.viewState = MultiStateView.ViewState.EMPTY
                    }
                    ResponseStatus.LOADING -> {
                        bi.multiStateView.viewState = MultiStateView.ViewState.LOADING
                    }
                }
            }
        })

        /*
        * Fetch childResponse
        * */
        viewModel.childUpdateResponse.observe(this, {
            when (it.status) {
                ResponseStatus.SUCCESS -> {
                    gotoActivity(SelectedChildrenListActivity::class.java)
                }
                ResponseStatus.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                ResponseStatus.LOADING -> {
                }
            }
        })

    }

    /*
    * Callback call after pressing Child item in recyclerview
    * */
    override fun callWarningActivity(id: Int, item: Any?) {
        val childItem = item as ChildInformation
        childItem.isEditFlag = true
        MainApp.childInformation = item
        gotoActivity(Section02CBActivity::class.java)
    }

    /*
    * Initialize recyclerView with onClickListener
    * */
    private fun callingRecyclerView() {
        adapter = ChildListAdapter(object : ChildListAdapter.OnItemClickListener {
            override fun onItemClick(item: ChildInformation, position: Int): Boolean {
                openWarningActivity(
                    title = "CONFIRMATION!",
                    message = "Are you sure, you want to edit ${item.cb02.toUpperCase(Locale.ENGLISH)} interview?",
                    item = item,
                    id = 1
                )
                return true
            }

            override fun onButtonItemClick(item: ChildInformation, flag: Boolean) {
                MainApp.childInformation =
                    ChildInformation((adapter.childItems.size + 1).toString(), flag, item)
                gotoActivity(Section02CBActivity::class.java)
            }
        })
        bi.childList.adapter = adapter
    }

    /*
    * Get childList on resume event
    * */
    override fun onResume() {
        super.onResume()

        viewModel.getChildDataFromDB(MainApp.form.cluster, MainApp.form.hhno, MainApp.form.uid)
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "You Can't go back", Toast.LENGTH_LONG).show()
    }

    /*
    * Setting child selection
    * */
    private fun childSelection() {
        val childList = adapter.childItems
        val selectedItems = childList.filter {
            it.cb11 == "1"
        }
        if (selectedItems.isNotEmpty()) {
            val selectedItem = selectedItems.sortedBy { it.totalMonths }[0]
            val item = selectedItem.totalMonths < 24
            viewModel.updateChildrenDataForSelectionDB(selectedItem, if (item) "2" else "1")
        } else {
            val selectedItem = childList.sortedBy { it.totalMonths }[0]
            viewModel.updateChildrenDataForSelectionDB(selectedItem, "1")
        }
    }
}