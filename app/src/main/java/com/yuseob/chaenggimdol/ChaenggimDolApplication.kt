package com.yuseob.chaenggimdol

import android.app.Application
import com.yuseob.chaenggimdol.notification.createNotificationChannels

class ChaenggimDolApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels(this)
        container = AppContainer.create(this)
    }
}
