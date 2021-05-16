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

class RegisterActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etEmailAddressRegister: EditText
    private lateinit var etMobileNumber: EditText
    private lateinit var etDeliveryAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
        etName = findViewById(R.id.etName)
        etEmailAddressRegister = findViewById(R.id.etEmailAddressRegister)
        etMobileNumber = findViewById(R.id.etMobileNumberRegister)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPasswordRegister)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnRegister.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this@RegisterActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("name", etName.text.toString())
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("password", etPassword.text.toString())
                jsonParams.put("address", etDeliveryAddress.text.toString())
                jsonParams.put("email", etEmailAddressRegister.text.toString())
                if (etPassword.text.toString() == etConfirmPassword.text.toString()) {
                    val jsonObject = object : JsonObjectRequest(
                        Method.POST, url, jsonParams,
                        Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val response = data.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("used_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("email", response.getString("email")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "mobile_number",
                                            response.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("address", response.getString("address")).apply()
                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
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
                    Toast.makeText(this, "Password not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                val dialog = AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@RegisterActivity)
                }
                dialog.create()
                dialog.show()
            }

        }


        title = "Register"
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this@RegisterActivity, LogInActivity::class.java)
        startActivity(intent)
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@RegisterActivity, LogInActivity::class.java)
        startActivity(intent)
    }
}
