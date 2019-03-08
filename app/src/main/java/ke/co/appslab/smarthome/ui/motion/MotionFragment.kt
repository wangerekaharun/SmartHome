package ke.co.appslab.smarthome.ui.motion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.models.MotionImageLog
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_motion.*
import org.jetbrains.anko.toast

class MotionFragment : Fragment() {
    private val motionLogViewModel: MotionLogViewModel by lazy {
        ViewModelProviders.of(this).get(MotionLogViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_motion, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefreshLayout.isRefreshing = true
        motionLogViewModel.getMotionLogs()
        swipeRefreshLayout.setOnRefreshListener { motionLogViewModel.getMotionLogs() }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        observeLiveData()
    }

    private fun observeLiveData() {
        motionLogViewModel.getMotionLogResponse().nonNull().observe(this){
            when{
                it.motionLogList != null ->{
                    swipeRefreshLayout.isRefreshing = false
                    initView(it.motionLogList)
                }
                it.response != null ->{
                    swipeRefreshLayout.isRefreshing = false
                    context?.toast(it.response)
                }
            }
        }
    }

    private fun initView(motionLogList: List<MotionImageLog>) {
        when{
            motionLogList.isEmpty() ->{
                motionLogRv.visibility = View.GONE
                emptyViewLinear.visibility = View.VISIBLE
            }
            else ->{
                motionLogRv.visibility = View.VISIBLE
                emptyViewLinear.visibility = View.GONE
                motionLogRv.layoutManager = LinearLayoutManager(activity)
                motionLogRv.adapter = MotionAdapter(motionLogList){
                }
            }
        }

    }
}