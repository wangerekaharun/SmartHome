package ke.co.appslab.smarthome.ui.doorbell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.datastates.DoorBellState
import ke.co.appslab.smarthome.models.DoorbellEntry
import ke.co.appslab.smarthome.utils.nonNull
import kotlinx.android.synthetic.main.fragment_door_bell.view.*
import org.jetbrains.anko.toast

class DoorBellFragment : Fragment() {
    private lateinit var doorbellViewModel: DoorbellViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_door_bell, container, false)

        doorbellViewModel = ViewModelProviders.of(this).get(DoorbellViewModel::class.java)
        doorbellViewModel.getDoorBellEntries()

        observeLiveData(view.cameraFeedRv)

        return view
    }

    private fun observeLiveData(cameraFeedRv: RecyclerView) {
        doorbellViewModel.getDoorbellEntriesResponse().observe(this, Observer {
            when {
                it.entriesList != null -> {
                    context?.toast(it.entriesList.size.toString())
                    initView(cameraFeedRv, it.entriesList)
                }
            }
        })
    }

    private fun initView(cameraFeedRv: RecyclerView, entriesList: List<DoorbellEntry>) {
        cameraFeedRv.layoutManager = LinearLayoutManager(activity!!)
        cameraFeedRv.adapter = DoorbellAdapter(entriesList) {

        }
    }

}