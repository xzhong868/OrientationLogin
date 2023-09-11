package com.example.orientationlogin

import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.nfc.tech.NfcF
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.example.orientationlogin.databinding.ActivityMainBinding
import com.romellfudi.fudinfc.gear.NfcAct
import com.romellfudi.fudinfc.gear.interfaces.OpCallback
import com.romellfudi.fudinfc.gear.interfaces.TaskCallback
import com.romellfudi.fudinfc.util.async.WriteCallbackNfc
import com.romellfudi.fudinfc.util.interfaces.NfcReadUtility
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.http.parametersOf
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.slf4j.MDC.put
import java.math.BigInteger


@RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class MainActivity : NfcAct(), KoinComponent {

//    private lateinit var prefManager : SharedPreferenceManager
//    private lateinit var binding: ActivityMainBinding

    private val mProgressDialog: ProgressDialog by inject {
        org.koin.core.parameter.parametersOf(
            this@MainActivity
        )
    }

    private val mNfcReadUtility: NfcReadUtility by inject()

    private val mTaskCallback: TaskCallback by inject()


    var mOpCallback: OpCallback? = null

    val client = getclient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val sharePreference = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        val sess = sharePreference.getString("SESSION", "").toString()

//        init()
        if (sess != "") {
            movePage()
        }
        val loginButton = findViewById<Button>(R.id.loginButton)
//        loginButton.setOnClickListener {
//            val editor = sharePreference.edit()
//            editor.putString("SESSION", "test")
//            editor.apply()
////            prefManager.saveClient(client)
//            login()
//        }
        val signupButton = findViewById<Button>(R.id.signupButton)
        signupButton.setOnClickListener {
            signup()
        }
    }

    public override fun onNewIntent(paramIntent: Intent) {
        super.onNewIntent(paramIntent)
        val dataMac = getMAC(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag)
        login(dataMac)
    }

//    public override fun onNewIntent(paramIntent: Intent) {
//        super.onNewIntent(paramIntent)
//        if (mProgressDialog.isShowing) {
//            mOpCallback?.let { WriteCallbackNfc(mTaskCallback, it).executeWriteOperation() }
//            mOpCallback = null
//        } else {
//            val dataFull =
//                "my mac: " + getMAC(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as? Tag)
//            mNfcReadUtility.readFromTagWithMap(paramIntent)?.values
//                ?.fold(dataFull) { full, st -> full + "\n${st}" }
//                .also { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
//        }
//    }



//    private fun init() {
//        prefManager = SharedPreferenceManager(this)
//    }

    private fun login(pw: String) {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        lifecycleScope.launch {
            kotlin.runCatching {
                client.gotrue.loginWith(Email) {
                    email = emailEditText.text.toString()
                    password = pw
//                    password = passwordEditText.text.toString()
                }
            }.onFailure {
                if (emailEditText.text.toString() == "" || passwordEditText.text.toString() == "") {
                    Toast.makeText(
                        this@MainActivity,
                        "Fill in all the fields",
                        Toast.LENGTH_LONG
                    ).show()
//                    binding.emailEditText.setText(sess.toString())
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Incorrect email or password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.onSuccess {
                movePage()
            }
        }
    }

    private fun signup() {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        lifecycleScope.launch {
            kotlin.runCatching {
                client.gotrue.signUpWith(Email) {
                    email = emailEditText.text.toString()
                    password = passwordEditText.text.toString()
                }
            }.onFailure {
                if (emailEditText.text.toString() == "" || passwordEditText.text.toString() == "") {
                    Toast.makeText(
                        this@MainActivity,
                        "Fill in all the fields",
                        Toast.LENGTH_LONG
                    ).show()
//                    binding.emailEditText.setText(sess.toString())
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Already exists",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.onSuccess {
                Toast.makeText(this@MainActivity, "Successfully signed up", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun movePage() {
        val intent = Intent(this, NewActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun getclient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://nabbsmcfsskdwjncycnk.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5hYmJzbWNmc3NrZHdqbmN5Y25rIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTM5MDM3ODksImV4cCI6MjAwOTQ3OTc4OX0.dRVk2u91mLhSMaA1s0FSyIFwnxe2Y3TPdZZ4Shc9mAY"
        ) {
            install(Postgrest)
            install(GoTrue)
        }
    }

    private fun getMAC(tag: Tag?): String =
        Regex("(.{2})").replace(
            String.format(
                "%0" + ((tag?.id?.size ?: 0) * 2).toString() + "X",
                BigInteger(1, tag?.id ?: byteArrayOf())
            ), "$1:"
        ).dropLast(1)
}