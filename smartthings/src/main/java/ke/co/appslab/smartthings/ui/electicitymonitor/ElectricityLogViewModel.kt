package ke.co.appslab.smartthings.ui.electicitymonitor

import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.repositories.ElectricityLogRepo

class ElectricityLogViewModel : ViewModel() {
    private val electricityLogRepo = ElectricityLogRepo()

    fun monitorElectricity(){
        electricityLogRepo.monitorElectricity()
    }

}