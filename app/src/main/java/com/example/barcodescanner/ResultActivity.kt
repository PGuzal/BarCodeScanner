package com.example.barcodescanner

import android.R.attr.data
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_result.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val Barcode = intent.getStringExtra("result_code");
        val url = "http://10.0.2.2:5000/get"
        val json = "{\"code\": \"$Barcode\"}"
        getJSON(url, json)
        menu_btn.setOnClickListener {
            val i = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(i)
        }
        btn_addAc.setOnClickListener {
            val i = Intent(this@ResultActivity, AddPlace::class.java)
            startActivity(i)
        }
    }

    fun getJSON(url: String, json: String) {
        val thread = Thread(Runnable {
            val client = OkHttpClient()
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(url)
                .post(json.toRequestBody(mediaType))
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val data = response?.body?.string()
                val gson = Gson()
                val product = gson.fromJson(data, Product::class.java)
                var results_text = ""
                if(product.code.equals("brak")){
                    results_text = "Nie znaleziono kodu w bazie"
                }else {
                    results_text = "Wartość odżywcza (na 100g/ml):\n\nKod: "+product.code+"\nWartość energetyczna: "+product.calorie+" kcal\nTłuszcz: "+product.fat+" g\nw tym kwasy tłuszczowe nasycone: "+product.saturated+" g\nWęglowodany: "+product.carb+" g\nw tym cukry: "+product.sugar+" g\nBiałko: "+product.protein+" g\nSól: "+product.sodium+"g"
                }
                result_database.setText(results_text).toString()
            }
        })
        thread.start()
    }
}

