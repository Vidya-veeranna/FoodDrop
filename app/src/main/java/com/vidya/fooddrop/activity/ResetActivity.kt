package com.vidya.fooddrop.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vidya.fooddrop.R
import com.vidya.fooddrop.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetActivity : AppCompatActivity() {
    private lateinit var etOTP: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSubmit: Button
    lateinit var toolbar: Toolbar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSubmit.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                val mobileNumber = sharedPreferences.getString("mobile_number", "9998886666")
                val queue = Volley.newRequestQueue(this@ResetActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", etNewPassword.text.toString())
                jsonParams.put("otp", etOTP.text.toString())
                if (etNewPassword.text.toString() == etConfirmPassword.text.toString()) {
                    val jsonObject = object : JsonObjectRequest(
                        Method.POST, url, jsonParams,
                        Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    Toast.makeText(
                                        this,
                                        "Successfully Changed Password",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val intent =
                                        Intent(this@ResetActivity, LogInActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Some success error Occuried",
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
                        },
                        Response.ErrorListener { }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Context-type"] = "application/json"
                            headers["token"] = "b57bea4cb596a1"
                            return headers
                        }
                    }
                    queue.add(jsonObject)
                } else {
                    Toast.makeText(this@ResetActivity, "Password not matching", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val dialog = AlertDialog.Builder(this@ResetActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@ResetActivity)
                }
                dialog.create()
                dialog.show()
            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this@ResetActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
        onBackPressed()
        return true
    }
}
