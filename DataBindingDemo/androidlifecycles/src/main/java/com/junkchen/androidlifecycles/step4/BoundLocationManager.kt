package com.junkchen.androidlifecycles.step4

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log

class BoundLocationManager {

    companion object {
        private val TAG = BoundLocationManager::class.java.canonicalName

        fun bindLocationListenerIn(lifecycleOwner: LifecycleOwner,
                                   listener: LocationListener,
                                   context: Context) {
            BoundLocationListener(lifecycleOwner, listener, context)
        }
    }

    @SuppressLint("MissingPermission")
    private class BoundLocationListener(lifecycleOwner: LifecycleOwner,
                                        private val listener: LocationListener,
                                        private val context: Context) : LifecycleObserver {
        private var locationManager: LocationManager? = null

        init {
            // Add lifecycle observer
            lifecycleOwner.lifecycle.addObserver(this)
        }

        // Call this on resume
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun addLocationListener() {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager?.apply {
                requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener)
                Log.i(TAG, "Listener added")

                // Force an update with the last location, if available.
                getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                    listener.onLocationChanged(it)
                }
            }
        }

        // Call this on pause
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun removeLocationListener() {
            locationManager?.apply {
                removeUpdates(listener)
                Log.i(TAG, "Listener removed")
            }
            locationManager = null
//            if (locationManager != null) {
//                locationManager?.removeUpdates(listener)
//                locationManager = null
//                Log.i(TAG, "Listener removed")
//            }
        }
    }
}