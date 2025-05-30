package ch.heigvd.iict.dma.wifirtt

import android.net.wifi.rtt.RangingResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ch.heigvd.iict.dma.wifirtt.config.MapConfig
import ch.heigvd.iict.dma.wifirtt.config.MapConfigs
import ch.heigvd.iict.dma.wifirtt.models.RangedAccessPoint
import com.lemmingapex.trilateration.*
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer

/**
 * @author Guillaume Dunant, Haeffner Edwin, Junod Arthur
 */

class WifiRttViewModel : ViewModel() {

    // PERMISSIONS MANAGEMENT
    private val _wifiRttPermissionsGranted = MutableLiveData<Boolean>(null)
    val wifiRttPermissionsGranted : LiveData<Boolean> get() = _wifiRttPermissionsGranted

    fun wifiRttPermissionsGrantedUpdate(granted : Boolean) {
        _wifiRttPermissionsGranted.postValue(granted)
    }

    // WIFI RTT AVAILABILITY MANAGEMENT
    private val _wifiRttEnabled = MutableLiveData<Boolean>(null)
    val wifiRttEnabled : LiveData<Boolean> get() = _wifiRttEnabled

    fun wifiRttEnabledUpdate(enabled : Boolean) {
        _wifiRttEnabled.postValue(enabled)
    }

    // WIFI RTT MEASURES MANAGEMENT
    private val _rangedAccessPoints = MutableLiveData(emptyList<RangedAccessPoint>())
    val rangedAccessPoints : LiveData<List<RangedAccessPoint>> = _rangedAccessPoints.map { l -> l.toList().map { el -> el.copy() } }


    private val TIME_TO_STAY_ALIVE = 15000

    // CONFIGURATION MANAGEMENT
    private val _mapConfig = MutableLiveData(MapConfigs.levelB)
    val mapConfig : LiveData<MapConfig> get() = _mapConfig

    fun onNewRangingResults(newResults : List<RangingResult>) {
        val oldList = _rangedAccessPoints.value?.toMutableList()
        val toDeleteList : MutableList<RangedAccessPoint> = mutableListOf()

        if(oldList != null) {
            val currTime : Long = System.currentTimeMillis()
            for (ap in oldList) {
                val newRanging = newResults.find{it.macAddress.toString() == ap.bssid}
                if(newRanging != null) {
                    ap.update(newRanging)
                } else if ((currTime - ap.age) > TIME_TO_STAY_ALIVE) { //Check the age
                    toDeleteList.add(ap)
                }
            }

            //Delete the DEAD access points
            for(ap in toDeleteList)
                oldList.remove(ap)


            // Add all new access points
            for (res in newResults){
                if (oldList.none{it.bssid == res.macAddress.toString()}){
                    oldList.add(RangedAccessPoint.newInstance(res))
                }
            }
             _rangedAccessPoints.postValue(oldList)
        }
        // when the list is updated, we also want to update estimated location
        estimateLocation()
    }

    // WIFI RTT ACCESS POINT LOCATIONS

    private val _estimatedPosition = MutableLiveData<DoubleArray>(null)
    val estimatedPosition : LiveData<DoubleArray> get() = _estimatedPosition

    private val _estimatedDistances = MutableLiveData<MutableMap<String, Double>>(mutableMapOf())
    val estimatedDistances : LiveData<Map<String, Double>> = _estimatedDistances.map { m -> m.toMap() }

    private val _debug = MutableLiveData(false)
    val debug : LiveData<Boolean> get() = _debug

    fun debugMode(debug: Boolean) {
        _debug.postValue(debug)
    }

    private fun estimateLocation() {
        // the library https://github.com/lemmingapex/trilateration
        // will certainly helps you for the maths

        // you should post the coordinates [x, y, height] of the estimated position in _estimatedPosition
        // in the second experiment, you can hardcode the height as 0.0
        //_estimatedPosition.postValue(doubleArrayOf(2500.0, 8500.0, 0.0))

        //as well as the distances with each access point as a MutableMap<String, Double>

        // Get all the valid AP and take the first 4 after we sort them by distance (take the 4 closest)
        val chosenAP = _rangedAccessPoints.value
            ?.filter { _mapConfig.value?.accessPointKnownLocations?.keys?.contains(it.bssid) ?: false }
            ?.sortedBy{it.distanceMm}
            ?.take(4)!!

        // The position of each AP
        val positions = chosenAP.map { _mapConfig.value?.accessPointKnownLocations?.get(it.bssid)!! }
            .map { doubleArrayOf(it.xMm.toDouble(), it.yMm.toDouble(), it.heightMm.toDouble()) }
            .toTypedArray()

        val distances = chosenAP.map {it.distanceMm}.toDoubleArray()

        val estimatedDistances = chosenAP.map { Pair(it.bssid, it.distanceMm)}.toMap().toMutableMap()

        if(positions.size != distances.size || positions.size < 3){
            // Do nothing or
            //_estimatedPosition.postValue(doubleArrayOf(0.0, 0.0, 0.0))
        } else {
            try{
                val solver = NonLinearLeastSquaresSolver(TrilaterationFunction(positions, distances), LevenbergMarquardtOptimizer())
                val optimum : LeastSquaresOptimizer.Optimum = solver.solve()
                _estimatedPosition.postValue(optimum.point.toArray())
            } catch (ex: Exception){
                Log.e(TAG, "Error during trilateration", ex)
            }

        }
        _estimatedDistances.postValue(estimatedDistances)
    }

    companion object {
        private val TAG = WifiRttViewModel::class.simpleName
    }

}