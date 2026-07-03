package com.yuseob.chaenggimdol.location

data class ExitDetectionPolicy(
    val minimumDurationMillis: Long,
    val exitRadiusMeters: Float,
    val maximumAccuracyMeters: Float,
) {
    fun shouldNotify(
        elapsedMillis: Long,
        distanceMeters: Float,
        accuracyMeters: Float,
    ): Boolean {
        return elapsedMillis >= minimumDurationMillis &&
            distanceMeters > exitRadiusMeters &&
            accuracyMeters <= maximumAccuracyMeters
    }
}
