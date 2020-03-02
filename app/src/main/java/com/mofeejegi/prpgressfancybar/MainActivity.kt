package com.mofeejegi.prpgressfancybar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress.setOnClickListener{
            val score = Random().nextInt(601) + 300f
            progress.progress = score
            progress.animateProgress(300f)
            Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show()


            digitText1.setValue(score.toString().substring(0, 1).toInt(), true, false)
            digitText2.setValue(score.toString().substring(1, 2).toInt(), true, true)
            digitText3.setValue(score.toString().substring(2, 3).toInt(), true, false)
//            digitText1.setValue(9, true, false)
//            digitText2.setValue(9, true, true)
//            digitText3.setValue(9, true, false)
        }
    }
}
