package com.example.locationmanagerdemo.location

import android.location.Location

interface LocationTracker {
    interface LocationUpdateListener {
        fun onUpdate(oldLoc: Location?, oldTime: Long, newLoc: Location?, newTime: Long)
    }

    fun start()
    fun start(update: LocationUpdateListener?)
    fun stop()
    fun hasLocation(): Boolean
    fun hasPossiblyStaleLocation(): Boolean
    val location: Location?
    val possiblyStaleLocation: Location?
}