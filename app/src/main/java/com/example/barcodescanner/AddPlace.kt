package com.example.barcodescanner

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.activity_main.*

class AddPlace : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        title = "KotlinApp"
        btn_add.setOnClickListener {
            val i = Intent(this@AddPlace, MainActivity::class.java)
            startActivity(i)
        }
        scan_btn.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            val textView: TextView = findViewById(R.id.Result_view) as TextView
            if(result != null){
                if(result.contents == null){
                    Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show()
                }else{
                    textView.setText(result.contents).toString()
                }
            }

        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}