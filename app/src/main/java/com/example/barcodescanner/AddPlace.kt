package com.example.barcodescanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_add_place.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class AddPlace : AppCompatActivity() {
    val result = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        title = "KotlinApp"
        btn_add.setOnClickListener {
            val Calorie = Calorie_entry.text
            val Fat = Fat_entry.text
            val Saturated = Saturated_entry.text
            val Carb = Carb_entry.text
            val Sugar = Sugar_entry.text
            val Protein = Protein_entry.text
            val Sodium = Sodium_entry.text
            val Barcode = Result_view.text.toString()
            if(Calorie.isBlank() || Fat.isBlank() || Saturated.isBlank() || Carb.isBlank() || Sugar.isBlank() || Protein.isBlank() || Sodium.isBlank()){
                Toast.makeText(this,"Nie uzupełniłeś jednego z pól. Jeśli nie znasz wartości wpisz 0.", Toast.LENGTH_LONG).show()
            }else if(Barcode.isBlank()){
                Toast.makeText(this,"Nie można wprowadzić produktu do bazy bez kodu kreskowego.", Toast.LENGTH_LONG).show()
            }else {
                val url = "http://10.0.2.2:8000/save"
                val json = "{\"code\": \"$Barcode\", \"calorie\": \"$Calorie\", \"fat\": \"$Fat\", \"saturated\": \"$Saturated\", \"carb\": \"$Carb\", \"sugar\": \"$Sugar\", \"protein\": \"$Protein\", \"sodium\": \"$Sodium\"}"
                saveJSON(url, json)
            }
        }
        scan_btn.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()
        }
        btn_menu.setOnClickListener {
            val i = Intent(this@AddPlace, MainActivity::class.java)
            startActivity(i)
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

    fun saveJSON(url: String, json: String) {
        var result = "FIRST"
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
                    val obj: JsonObject = Gson().fromJson(data, JsonObject::class.java)
                    result = obj["message"].asString
                    showToast(result);
                }
        })
        try {
            thread.start()
        } catch (e: Exception) {
            Toast.makeText(this,"Nie udało się nawiązać połączenia z bazą.", Toast.LENGTH_LONG).show()
        }
    }
    fun showToast(toast: String?) {
        runOnUiThread {
            Toast.makeText(
                this@AddPlace,
                toast,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}