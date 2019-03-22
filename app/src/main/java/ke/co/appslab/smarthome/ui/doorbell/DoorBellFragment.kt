package ke.co.appslab.smarthome.ui.doorbell

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.models.DoorbellEntry
import kotlinx.android.synthetic.main.fragment_door_bell.*
import org.jetbrains.anko.toast

class DoorBellFragment : Fragment() {
    private lateinit var doorbellViewModel: DoorbellViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_door_bell, container, false)
    }

    private fun observeLiveData() {
        doorbellViewModel.getDoorbellEntriesResponse().observe(this, Observer {
            when {
                it.entriesList != null -> {
                    swipeRefreshLayout.isRefreshing = false
                    initView(it.entriesList)
                }
                it.responseString != null -> {
                    swipeRefreshLayout.isRefreshing = false
                    context?.toast(it.responseString)
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        doorbellViewModel = ViewModelProviders.of(this).get(DoorbellViewModel::class.java)
        doorbellViewModel.getDoorBellEntries()
        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setOnRefreshListener { doorbellViewModel.getDoorBellEntries() }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        observeLiveData()
    }

    private fun initView(entriesList: List<DoorbellEntry>) {
        when {
            entriesList.isEmpty() -> {
                cameraFeedRv.visibility = View.GONE
                emptyViewLinear.visibility = View.VISIBLE
            }
            else -> {
                cameraFeedRv.visibility = View.VISIBLE
                emptyViewLinear.visibility = View.GONE
                cameraFeedRv.layoutManager = LinearLayoutManager(activity!!)
                cameraFeedRv.adapter = DoorbellAdapter(entriesList) {
                    val alertDialogBuilder = Dialog(activity!!)
                    alertDialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    alertDialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertDialogBuilder.setContentView(R.layout.doorbell_actions_dialog)
                    alertDialogBuilder.show()

                }
            }
        }
    }

}