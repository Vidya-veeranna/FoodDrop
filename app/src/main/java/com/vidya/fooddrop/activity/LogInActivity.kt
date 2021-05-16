package com.vidya.fooddrop.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vidya.fooddrop.R
import com.vidya.fooddrop.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import androidx.appcompat.widget.Toolbar

class LogInActivity : AppCompatActivity() {

    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtForgotPassword: TextView
    private lateinit var txtRegister: TextView
    private lateinit var btnLogIn: Button
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_log_in)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)
        btnLogIn = findViewById(R.id.btnLogIn)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Log In"

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this@LogInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        title = "Log In"


        btnLogIn.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this@LogInActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("password", etPassword.text.toString())
                val jsonObject = object :
                    JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val response = data.getJSONObject("data")
                                val userId = response.getString("user_id")
                                Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
                                sharedPreferences.edit()
                                    .putString("used_id", response.getString("user_id")).apply()
                                sharedPreferences.edit()
                                    .putString("user_name", response.getString("name")).apply()
                                sharedPreferences.edit()
                                    .putString("email", response.getString("email")).apply()
                                sharedPreferences.edit()
                                    .putString("mobile_number", response.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("address", response.getString("address")).apply()
                                val intent = Intent(this@LogInActivity, MainActivity::class.java)
                                savePreferences()
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Some error Occuried",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this,
                                "Some catch error Occuried",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }, Response.ErrorListener { }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Context-type"] = "application/json"
                        headers["token"] = "b57bea4cb596a1"
                        return headers
                    }
                }
                queue.add(jsonObject)
            } else {
                val dialog = AlertDialog.Builder(this@LogInActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@LogInActivity)
                }
                dialog.create()
                dialog.show()
            }

        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(
                this@LogInActivity,
                ForgotPasswordActivity::class.java
            )
            startActivity(intent)
        }

        txtRegister.setOnClickListener {
            val intent = Intent(this@LogInActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun savePreferences() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

