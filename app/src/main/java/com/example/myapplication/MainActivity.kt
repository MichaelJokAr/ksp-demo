package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jokar.ksp.spi.annoation.SPIService
import org.jokar.my.library.a.ICommonService

@SPIService(services = [Book::class], singleton = false, priority = 9)
class MainActivity : AppCompatActivity(),ICommonService {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clickTest()
    }

    val handler: Handler = Handler(Looper.getMainLooper())

    private fun clickTest() {
        findViewById<TextView>(R.id.textView1).setOnClickListener {
            Toast.makeText(this, "text1", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.textView2).setOnClickListener {
            Toast.makeText(this, "text3", Toast.LENGTH_SHORT).show()
        }
    }

}