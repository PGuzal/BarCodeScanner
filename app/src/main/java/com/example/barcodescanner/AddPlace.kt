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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class AddPlace : AppCompatActivity() {
    val result = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        title = "Skaner"
        btn_add.setOnClickListener {
            val Name_P = Name_entry.text
            val Calorie = Calorie_entry.text
            val Fat = Fat_entry.text
            val Saturated = Saturated_entry.text
            val Carb = Carb_entry.text
            val Sugar = Sugar_entry.text
            val Protein = Protein_entry.text
            val Sodium = Sodium_entry.text
            val Barcode = Result_view.text.toString()
            if(Calorie.isBlank() || Fat.isBlank() || Saturated.isBlank() || Carb.isBlank() || Sugar.isBlank() || Protein.isBlank() || Sodium.isBlank() || Name_P.isBlank()){
                Toast.makeText(this,"Nie uzupełniłeś któregoś z pól. Jeśli nie znasz wartości wpisz 0.", Toast.LENGTH_LONG).show()
            }else if(Barcode.isBlank()){
                Toast.makeText(this,"Nie można wprowadzić produktu do bazy bez kodu kreskowego.", Toast.LENGTH_LONG).show()
            }else {
                val url = "http://10.0.2.2:8000/save" //ustalenie odnośnika mającego obsłużyć żądanie
                val json = "{\"code\": \"$Barcode\",\"name\": \"$Name_P\", \"calorie\": \"$Calorie\", \"fat\": \"$Fat\", \"saturated\": \"$Saturated\", \"carb\": \"$Carb\", \"sugar\": \"$Sugar\", \"protein\": \"$Protein\", \"sodium\": \"$Sodium\"}" //tworzenie treści wiadomości do wysłania
                Toast.makeText(this,"Prosze poczekać na odpowiedź z serwera.", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this,"Skanowanie nieudane.", Toast.LENGTH_LONG).show()
                }else{
                    textView.setText(result.contents).toString()
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //przesłanie danych do zapisu oraz obsługa odpowiedzi
    fun saveJSON(url: String, json: String) {
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val request = Request.Builder() //tworzenie wiadomości do wysłania
            .url(url)
            .post(json.toRequestBody(mediaType))
            .build()
    client.newCall(request).enqueue(object: Callback { //próba przesłania danych
        override fun onFailure(call: Call, e: IOException) {
            showToast("Nie udało się nawiązać połączenia z bazą.")
        }
        //obsługa odpowiedzi - wyświetlenie odebranej wiadomości
        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val data = response?.body?.string()
            val obj: JsonObject = Gson().fromJson(data, JsonObject::class.java)
            val result = obj["message"].asString
            showToast(result);
        }
    })
    }

    //tworzenie komunikatów
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