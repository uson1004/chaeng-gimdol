package com.yuseob.chaenggimdol.location

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExitDetectionPolicyTest {
    private val policy = ExitDetectionPolicy(
        minimumDurationMillis = 10 * 60 * 1_000L,
        exitRadiusMeters = 100f,
        maximumAccuracyMeters = 50f,
    )

    @Test
    fun doesNotTriggerBeforeMinimumDuration() {
        assertFalse(
            policy.shouldNotify(
                elapsedMillis = 599_999,
                distanceMeters = 150f,
                accuracyMeters = 10f,
            ),
        )
    }

    @Test
    fun doesNotTriggerInsideRadius() {
        assertFalse(
            policy.shouldNotify(
                elapsedMillis = 600_000,
                distanceMeters = 99f,
                accuracyMeters = 10f,
            ),
        )
    }

    @Test
    fun doesNotTriggerWithPoorAccuracy() {
        assertFalse(
            policy.shouldNotify(
                elapsedMillis = 600_000,
                distanceMeters = 150f,
                accuracyMeters = 51f,
            ),
        )
    }

    @Test
    fun triggersWhenAllConditionsHold() {
        assertTrue(
            policy.shouldNotify(
                elapsedMillis = 600_000,
                distanceMeters = 101f,
                accuracyMeters = 20f,
            ),
        )
    }
}
