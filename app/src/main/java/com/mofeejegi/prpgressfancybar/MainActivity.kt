package com.mofeejegi.prpgressfancybar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress.setOnClickListener{
            progress.animateProgress(300f)
            Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show()
        }
    }
}
