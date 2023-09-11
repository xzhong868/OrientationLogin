package com.example.orientationlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.orientationlogin.databinding.HomePageBinding

class NewActivity : AppCompatActivity() {

//    private lateinit var binding: HomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(R.layout.home_page)

        val sharePreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        val sess = sharePreference.getString("SESSION", "").toString()
        val textView2 = findViewById<TextView>(R.id.textView2)
        textView2.text = sess

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val editor = sharePreference.edit()
            editor.remove("SESSION")
            editor.apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}