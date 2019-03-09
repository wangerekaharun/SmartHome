package ke.co.appslab.smarthome.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.datastates.WeatherDataState
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by lazy { ViewModelProviders.of(this).get(HomeViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getWeatherData()
        observeLiveData()
    }

    private fun observeLiveData() {
        homeViewModel.getWeatherDataResponse().nonNull().observe(this) {
            setUpViews(it)
        }
    }

    private fun setUpViews(it: WeatherDataState) {
        val weather = it.weather
        weather?.let {
            temperatureText.text = getString(R.string.temperature, it[0].temperature)
            pressureText.text = getString(R.string.pressure, it[0].pressure)
        }
    }

    private fun getWeatherData() {
        homeViewModel.getWeatherData()
    }
}