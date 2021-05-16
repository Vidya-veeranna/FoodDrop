package com.vidya.fooddrop.activity

import android.widget.RelativeLayout
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vidya.fooddrop.R
import org.json.JSONObject
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import com.vidya.fooddrop.adapter.CartAdapter
import com.vidya.fooddrop.adapter.FoodRecyclerAdapter
import com.vidya.fooddrop.database.OrderEntity
import com.vidya.fooddrop.database.RestaurantDatabase
import com.vidya.fooddrop.model.FoodItem
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONArray

class CartActivity : AppCompatActivity() {

    private lateinit var cartAdapter: CartAdapter
    private val orderList = arrayListOf<FoodItem>()
    private lateinit var progressLayout: RelativeLayout
    private lateinit var recyclerCart: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var txtResName: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rlCart: RelativeLayout
    private lateinit var btnPlaceOrder: Button
    private var restaurantId: String? = "100"
    private var resName: String? = "Pind Tadka"
    private var userId: String? = "0"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
        recyclerCart = findViewById(R.id.recyclerFood)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        rlCart = findViewById(R.id.rlCart)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbar)
        txtResName = findViewById(R.id.txtResName)

        userId = sharedPreferences.getString("cart_user_id", "0")
        restaurantId = sharedPreferences.getString("res-id", "10")
        resName = sharedPreferences.getString("res-name", "Pind Tadka")
        txtResName.text = resName
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressLayout.visibility = View.GONE

        setUpCartList()
        placeOrder()
    }


    private fun setUpCartList() {

        val dbList = DBAsync(this@CartActivity).execute().get()
        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<FoodItem>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            Toast.makeText(this@CartActivity, "Empty", Toast.LENGTH_SHORT).show()
            rlCart.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            progressLayout.visibility = View.VISIBLE
        } else {
            Toast.makeText(this@CartActivity, " not Empty", Toast.LENGTH_SHORT).show()
            rlCart.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            progressLayout.visibility = View.GONE
        }

        cartAdapter = CartAdapter(orderList, this@CartActivity)
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerFood.layoutManager = mLayoutManager
        recyclerFood.itemAnimator = DefaultItemAnimator()
        recyclerFood.adapter = cartAdapter
    }


    private fun placeOrder() {
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].costForOne.toInt()
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            sendServerRequest()
        }
    }

    private fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)
        val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            userId
        )

        jsonParams.put("restaurant_id", restaurantId)
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].costForOne.toInt()
        }

        jsonParams.put("total_cost", sum.toString())
        val foodArray = JSONArray()
        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].foodId)
            foodArray.put(i, foodId)
        }
        jsonParams.put("food", foodArray)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    Toast.makeText(this@CartActivity, "Success $success", Toast.LENGTH_SHORT)
                        .show()
                    if (success) {
                        ClearCart(this@CartActivity, restaurantId.toString()).execute()
                        FoodRecyclerAdapter.isCartEmpty = true

                        val dialog = Dialog(
                            this@CartActivity,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                        dialog.setContentView(R.layout.order_placed)
                        dialog.show()
                        dialog.setCancelable(false)
                        val btnOk = dialog.findViewById<Button>(R.id.btnOK)
                        btnOk.setOnClickListener {
                            dialog.dismiss()
                            ClearCart(this@CartActivity, restaurantId.toString()).execute()
                            startActivity(Intent(this@CartActivity, MainActivity::class.java))
                            ActivityCompat.finishAffinity(this@CartActivity)
                        }
                    } else {
                        rlCart.visibility = View.VISIBLE
                        Toast.makeText(this@CartActivity, "Some Error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }

                } catch (e: Exception) {
                    rlCart.visibility = View.VISIBLE
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                rlCart.visibility = View.VISIBLE
                Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "b57bea4cb596a1"
                    return headers
                }
            }

        queue.add(jsonObjectRequest)

    }

    class DBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }
    }

    class ClearCart(val context: Context, private val restaurantId: String) :
        AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            db.orderDao().deleteOrders(restaurantId.toInt())
            db.close()
            return true
        }

    }

    override fun onBackPressed() {
        ClearCart(this@CartActivity, restaurantId.toString()).execute()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        ClearCart(this@CartActivity, restaurantId.toString()).execute()
        val intent = Intent(this@CartActivity, DescriptionActivity::class.java)
        startActivity(intent)
        onBackPressed()
        return true
    }

}
