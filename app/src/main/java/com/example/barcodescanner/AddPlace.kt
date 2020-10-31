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
import kotlinx.android.synthetic.main.activity_result.*

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
            val code_product = Result_view.text.toString()
            if(Calorie.isBlank() || Fat.isBlank() || Saturated.isBlank() || Carb.isBlank() || Sugar.isBlank() || Protein.isBlank() || Sodium.isBlank()){
                Toast.makeText(this,"Nie uzupełniłeś jednego z pól. Jeśli nie znasz wartości wpisz 0.", Toast.LENGTH_LONG).show()
            }else if(code_product.isBlank()){
                Toast.makeText(this,"Nie można wprowadzić produktu do bazy bez kodu kreskowego.", Toast.LENGTH_LONG).show()
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
}