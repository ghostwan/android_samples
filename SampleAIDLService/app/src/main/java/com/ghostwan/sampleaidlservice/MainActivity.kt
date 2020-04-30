package com.ghostwan.sampleaidlservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var testService: ITestService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            testService = ITestService.Stub.asInterface(service)
            testService?.getInfo(object: Callback.Stub() {
                override fun onSuccess(response: String?) {
                    textview.text = "Success: $response"
                }

                override fun onError(errorCode: Int, errorMessage: String?) {
                    textview.text = "Error: $errorMessage"
                }

            })
        }

        override fun onServiceDisconnected(name: ComponentName) {
            testService = null
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, TestService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}
