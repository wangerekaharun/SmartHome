package ke.co.appslab.smarthome.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.datastates.WeatherDataState
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by lazy { ViewModelProviders.of(this).get(HomeViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val temperature = view.temperatureText
        val pressure = view.pressureText
        getWeatherData()
        observeLiveData(temperature, pressure)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun observeLiveData(temperature: TextView, pressure: TextView) {
        homeViewModel.getWeatherDataResponse().observe(this, Observer { weatherState ->
            weatherState?.let { setUpViews(it, temperature, pressure) }

        })
    }

    private fun setUpViews(
        it: WeatherDataState,
        temperature: TextView,
        pressure: TextView
    ) {
        val weather = it.weather
        weather?.let {

            temperature.text = getString(R.string.temperature, it[0].temperature)
            pressure.text = getString(R.string.pressure, it[0].pressure)
        }


    }

    private fun getWeatherData() {
        homeViewModel.getWeatherData()
    }
}