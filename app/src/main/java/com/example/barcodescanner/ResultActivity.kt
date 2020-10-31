package com.example.barcodescanner

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val Barcode = intent.getStringExtra("result_code");
        result_database.setText(Barcode.toString()).toString()
        menu_btn.setOnClickListener {
            val i = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(i)
        }
        btn_addAc.setOnClickListener {
            val i = Intent(this@ResultActivity, AddPlace::class.java)
            startActivity(i)
        }
    }
}