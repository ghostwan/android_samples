package com.ghostwan.sampleaidlservice

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TestService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return ServiceBinder()
    }

    class ServiceBinder : ITestService.Stub() {
        override fun getInfo(callback: Callback) {
            if ((Math.random() <= 0.5)) {
                callback.onSuccess("Everything works!")
            } else {
                callback.onError(1, "Something happened!")
            }
        }
    }
}
