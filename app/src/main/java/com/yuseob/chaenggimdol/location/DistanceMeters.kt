package com.yuseob.chaenggimdol.location

import android.location.Location

fun distanceMeters(
    startLatitude: Double,
    startLongitude: Double,
    currentLatitude: Double,
    currentLongitude: Double,
): Float {
    val result = FloatArray(1)
    Location.distanceBetween(
        startLatitude,
        startLongitude,
        currentLatitude,
        currentLongitude,
        result,
    )
    return result[0]
}
