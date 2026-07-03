package com.yuseob.chaenggimdol.location

interface LocationSessionController {
    fun start(sessionId: Long)

    fun stop()
}
