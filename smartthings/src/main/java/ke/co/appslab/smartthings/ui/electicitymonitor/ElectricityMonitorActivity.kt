package ke.co.appslab.smartthings.ui.electicitymonitor

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
import ke.co.appslab.smartthings.models.ElectricityMonitorLog
import ke.co.appslab.smartthings.utils.SharedPref.IS_CONNECTED
import ke.co.appslab.smartthings.utils.SharedPref.PREF_NAME
import ke.co.appslab.smartthings.utils.getDurationFormatted
import ke.co.appslab.smartthings.utils.nonNull
import ke.co.appslab.smartthings.utils.observe
import kotlinx.android.synthetic.main.activity_electricity_monitor.*


class ElectricityMonitorActivity : AppCompatActivity(), DroidListener {
    private var isPowerOn = true
    private val electricityLogViewModel: ElectricityLogViewModel by lazy {
        ViewModelProviders.of(this).get(ElectricityLogViewModel::class.java)
    }
    lateinit var droidNet: DroidNet
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electricity_monitor)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon =
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_keyboard_backspace_black_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))

        droidNet = DroidNet.getInstance()
        droidNet.addInternetConnectivityListener(this)

        monitorElectricity()
        fetchElectricityLogs()
        observeLiveData()
    }

    private fun monitorElectricity() {
        electricityLogViewModel.monitorElectricity()
    }

    private fun observeLiveData() {
        electricityLogViewModel.getPowerInfoResponse().nonNull().observe(this) {
            handlePowerInfoResponse(it)
        }
        electricityLogViewModel.getElectricityMonitorLogsResponse().nonNull().observe(this) {
            handleElectricityLogsResponse(it)
        }
    }

    private fun fetchElectricityLogs() {
        showDialog()
        electricityLogViewModel.loadPowerInfo()
        electricityLogViewModel.fetchElectricityMonitorLogs()
    }

    private fun handleElectricityLogsResponse(it: ElectricityMonitorLog) {
        hideDialog()
        when (it.timestampOff) {
            null -> timeElapsedTextView.text = getString(R.string.power_has_been_on_for,
                it.timeStampOn?.let { timeStampOn -> getDurationFormatted(timeStampOn, applicationContext) })
            else -> {
                timeElapsedTextView.text = getString(R.string.power_has_been_off_for,
                    it.timestampOff?.let { timeStampOff -> getDurationFormatted(timeStampOff, applicationContext) })
            }
        }

    }

    private fun handlePowerInfoResponse(it: Boolean) {
        hideDialog()
        when {
            it -> {
                isPowerOn = true
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorLightsOnBackground
                    )
                )
                electricityStatusImg.setImageResource(R.drawable.lights_on_house)
                overallStatusText.text = getString(R.string.power_is_on)
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
                isPowerOn = false
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorLightsOffBackground
                    )
                )
                electricityStatusImg.setImageResource(R.drawable.lights_off_house)
                overallStatusText.text = getString(R.string.power_is_off)
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
        electricityStatusImg.visibility = View.GONE
        overallStatusText.visibility = View.GONE
        timeElapsedTextView.visibility = View.GONE

    }

    private fun hideDialog() {
        progressBar.visibility = View.GONE
        electricityStatusImg.visibility = View.VISIBLE
        overallStatusText.visibility = View.VISIBLE
        timeElapsedTextView.visibility = View.VISIBLE
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        when {
            isConnected -> {
                sharedPreferences.edit().putBoolean(IS_CONNECTED, true).apply()
                Log.d("ElectricityMonitorNetwork", "true")
            }
            else -> {
                sharedPreferences.edit().putBoolean(IS_CONNECTED, false).apply()
                Log.d("ElectricityMonitorNetwork", "false")

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        droidNet.removeInternetConnectivityChangeListener(this)
    }
}
