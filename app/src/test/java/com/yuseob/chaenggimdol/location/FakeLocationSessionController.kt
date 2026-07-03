package com.yuseob.chaenggimdol.location

class FakeLocationSessionController : LocationSessionController {
    val started = mutableListOf<Long>()
    var stopCount = 0

    override fun start(sessionId: Long) {
        started += sessionId
    }

    override fun stop() {
        stopCount += 1
    }
}
