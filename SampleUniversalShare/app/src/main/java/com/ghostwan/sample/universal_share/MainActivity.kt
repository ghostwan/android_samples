package com.ghostwan.sample.universal_share

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        seeBT.visibility = View.GONE

        if(intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_SEND_MULTIPLE) {
            handleSendAction()
        }
        else {
            typeTV.visibility = View.GONE
            mimeTypeTV.visibility = View.GONE
            contentTV.text = "Nothing shared"
        }
    }

    fun handleSendAction() {
        mimeTypeTV.visibility = View.VISIBLE
        mimeTypeTV.text = intent.type
        typeTV.visibility = View.VISIBLE

        with(intent.type){
            when {
                contains("text/plain") -> {
                    typeTV.text = "HTML"
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        contentTV.text = it
                    }
                }
                contains("text/html") -> {
                    typeTV.text = "HTML"
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        contentTV.text = it
                    }
                }
                contains("image/") -> {
                    typeTV.text = "Image"
                    handleMedia(intent)

                }
                contains("video/") -> {
                    typeTV.text = "Video"
                    handleMedia(intent)
                }
                contains("audio/") -> {
                    typeTV.text = "Audio"
                    handleMedia(intent)
                }
                else -> Unit
            }
        }
    }

    fun handleMedia(intent: Intent) {
        val uri = if(intent.action == Intent.ACTION_SEND) {
            (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                contentTV.text = it.toString()
                it
            }
        }
        else {
            intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
                var content = ""
                it.forEach { content += "$it \n" }
                contentTV.text = content
                it[0].toString().toUri()
            }
        }

        seeBT.visibility = View.VISIBLE
        seeBT.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

}
