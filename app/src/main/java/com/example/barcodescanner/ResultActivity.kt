package com.example.barcodescanner

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
        val url = "http://10.0.2.2:8000/receive" //ustalenie odnośnika mającego obsłużyć żądanie
        val json = "{\"code\": \"$Barcode\"}" //tworzenie treści wiadomości do wysłania
        receiveJSON(url, json)
        menu_btn.setOnClickListener {
            val i = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(i)
        }
        btn_addAc.setOnClickListener {
            val i = Intent(this@ResultActivity, AddPlace::class.java)
            if(result_database.text.toString()==" Nie znaleziono kodu w bazie")  {
                startActivity(i)
            }
            else if (result_database.text.toString()==" Proszę poczekać na połączenie z bazą") {
                Toast.makeText(this,"Nie udało się sprawdzić obecności kodu z uwagi na brak połączenia.", Toast.LENGTH_LONG).show()
                startActivity(i)
            }
            else {
                Toast.makeText(this,"Dane są już w bazie, nie można ich wprowadzić ponownie.", Toast.LENGTH_LONG).show()
            }
        }
    }

    //przesłanie kodu do uzyskania informacji oraz obsługa odpowiedzi
    fun receiveJSON(url: String, json: String) {
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val request = Request.Builder() //tworzenie wiadomości do wysłania
            .url(url)
            .post(json.toRequestBody(mediaType))
            .build()
        client.newCall(request).enqueue(object: Callback { //próba przesłania danych
            override fun onFailure(call: Call, e: IOException) {
                showToast("Nie udało się nawiązać połączenia z serwerem.")
            }
            //obsługa odpowiedzi - wyświetlenie odebranych informacji
            override fun onResponse(call: Call, response: Response) {
                val data = response?.body?.string()
                val product = Gson().fromJson(data, Product::class.java)
                var results_text = ""
                if(product.code.equals("brak")){
                    results_text = " Nie znaleziono kodu w bazie"
                }else if(product.code.equals("brakdb")){
                    showToast("Nie udało się nawiązać połączenia z bazą danych.")
                }else {
                    results_text = " Wartość odżywcza (na 100 g/ml):\n\n Kod: "+product.code+"\n\n Nazwa produktu: "+product.name+"\n\n Wartość energetyczna: "+product.calorie+" kcal\n Tłuszcz: "+product.fat+" g\n w tym kwasy tłuszczowe nasycone: "+product.saturated+" g\n Węglowodany: "+product.carb+" g\n w tym cukry: "+product.sugar+" g\n Białko: "+product.protein+" g\n Sól: "+product.sodium+"g"
                }
                result_database.setText(results_text).toString()
            }
        })
    }

    //tworzenie komunikatów
    fun showToast(toast: String?) {
        runOnUiThread {
            Toast.makeText(
                this@ResultActivity,
                toast,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

