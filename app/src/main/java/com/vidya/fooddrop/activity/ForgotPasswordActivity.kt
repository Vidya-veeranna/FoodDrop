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
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vidya.fooddrop.R
import com.vidya.fooddrop.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import androidx.appcompat.widget.Toolbar

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var etMobileNumber: EditText
    private lateinit var etEmailAddress: EditText
    private lateinit var btnConfirm: Button
    private lateinit var toolbar: Toolbar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
        etMobileNumber = findViewById(R.id.etMobileNumberForgotPasswod)
        etEmailAddress = findViewById(R.id.etEmailAddressForgotPassword)
        btnConfirm = findViewById(R.id.btnConfirm)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnConfirm.setOnClickListener {
            sharedPreferences.edit().putString("mobile_number", etMobileNumber.text.toString())
                .apply()
            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("email", etEmailAddress.text.toString())
                val jsonObject = object : JsonObjectRequest(
                    Method.POST, url, jsonParams,
                    Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val bundle = Bundle()
                                bundle.putString("mobile_number", etMobileNumber.text.toString())
                                val intent =
                                    Intent(this@ForgotPasswordActivity, ResetActivity::class.java)
                                intent.putExtras(bundle)
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
                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this@ForgotPasswordActivity, LogInActivity::class.java)
        startActivity(intent)
        onBackPressed()
        return true
    }
}
