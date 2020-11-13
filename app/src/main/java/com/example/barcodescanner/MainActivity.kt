package com.example.barcodescanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_scan.setOnClickListener{
            val scanner = IntentIntegrator(this)

            scanner.initiateScan()
        }
        Add_btn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddPlace::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            val intent = Intent(this@MainActivity, ResultActivity::class.java)
            if(result != null){
                if(result.contents == null){
                    Toast.makeText(this,"Skanowanie nieudane.",Toast.LENGTH_LONG).show()
                }else{
                    intent.putExtra("result_code",result.contents)
                    startActivity(intent)
                }
            }

        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}