package ke.co.appslab.smartthings.ui.internetmonitor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.models.InternetMonitorLog
import ke.co.appslab.smartthings.utils.SharedPref.IS_CONNECTED
import ke.co.appslab.smartthings.utils.SharedPref.PREF_NAME
import ke.co.appslab.smartthings.utils.getDurationFormatted
import ke.co.appslab.smartthings.utils.nonNull
import ke.co.appslab.smartthings.utils.observe
import kotlinx.android.synthetic.main.activity_internet_monitor.*


class InternetMonitorActivity : AppCompatActivity(), DroidListener {
    private var isInternetOn = true
    private val internetLogViewModel: InternetLogViewModel by lazy {
        ViewModelProviders.of(this).get(InternetLogViewModel::class.java)
    }
    lateinit var droidNet: DroidNet
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internet_monitor)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon =
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_keyboard_backspace_black_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))

        droidNet = DroidNet.getInstance()
        droidNet.addInternetConnectivityListener(this)

        monitorInternet()
        fetchInternetLogs()
        observeLiveData()
    }

    private fun monitorInternet() {
        internetLogViewModel.monitorInternet()
    }

    private fun observeLiveData() {
        internetLogViewModel.getInternetInfoResponse().nonNull().observe(this) {
            handleInternetInfoResponse(it)
        }
        internetLogViewModel.getInternetMonitorLogsResponse().nonNull().observe(this) {
            handleInternetLogsResponse(it)
        }
    }

    private fun fetchInternetLogs() {
        showDialog()
        internetLogViewModel.loadInternetInfo()
        internetLogViewModel.fetchInternetMonitorLogs()
    }

    private fun handleInternetLogsResponse(it: InternetMonitorLog) {
        hideDialog()
        when (it.timestampOff) {
            null -> timeElapsedTextView.text = getString(R.string.internet_has_been_on_for,
                it.timeStampOn?.let { timeStampOn -> getDurationFormatted(timeStampOn, applicationContext) })
            else -> {
                timeElapsedTextView.text = getString(R.string.internet_has_been_off_for,
                    it.timestampOff?.let { timeStampOff -> getDurationFormatted(timeStampOff, applicationContext) })
            }
        }

    }

    private fun handleInternetInfoResponse(it: Boolean) {
        hideDialog()
        when {
            it -> {
                isInternetOn = true
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorLightsOnBackground
                    )
                )
                internetStatusImg.setImageResource(R.drawable.lights_on_house)
                internetConnectionStatusImg.setImageResource(R.drawable.ic_wifi)
                overallStatusText.text = getString(R.string.internet_is_on)
                window.statusBarColor =
                    ContextCompat.getColor(applicationContext, R.color.colorLightsOnBackgroundDarker)
                toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorLightsOnBackground
                    )
                )

            }
            else -> {
                isInternetOn = false
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorLightsOffBackground
                    )
                )
                internetStatusImg.setImageResource(R.drawable.lights_off_house)
                internetConnectionStatusImg.setImageResource(R.drawable.ic_no_wifi)
                overallStatusText.text = getString(R.string.internet_is_off)
                window.statusBarColor =
                    ContextCompat.getColor(applicationContext, R.color.colorLightsOffBackgroundDarker)

                toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorLightsOffBackground
                    )
                )
            }
        }

    }

    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
        internetStatusImg.visibility = View.GONE
        overallStatusText.visibility = View.GONE
        timeElapsedTextView.visibility = View.GONE

    }

    private fun hideDialog() {
        progressBar.visibility = View.GONE
        internetStatusImg.visibility = View.VISIBLE
        overallStatusText.visibility = View.VISIBLE
        timeElapsedTextView.visibility = View.VISIBLE
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        when {
            isConnected -> {
                Log.d("InternetMonitorNetwork", "true")
            }
            else -> {
                sharedPreferences.edit().putInt(IS_CONNECTED, 1).apply()
                Log.d("InternetMonitorNetwork", "false")

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("InternetMonitorNetwork", "onDestroy")
        droidNet.removeInternetConnectivityChangeListener(this)
    }
}
