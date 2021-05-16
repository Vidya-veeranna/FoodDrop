package com.vidya.fooddrop.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.vidya.fooddrop.R
import com.vidya.fooddrop.adapter.FoodRecyclerAdapter
import com.vidya.fooddrop.database.OrderEntity
import com.vidya.fooddrop.database.RestaurantDatabase
import com.vidya.fooddrop.util.ConnectionManager
import org.json.JSONException
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vidya.fooddrop.model.FoodItem

class DescriptionActivity : AppCompatActivity() {
    private lateinit var recyclerFood: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    private lateinit var addToCart: Button
    private lateinit var descriptionAdapter: FoodRecyclerAdapter
    private val foodList = arrayListOf<FoodItem>()
    private val orderList = arrayListOf<FoodItem>()
    private var restaurantId: String? = "100"
    private var resName: String? = "Pind Tadka"
    private var userid: String? = "0"
    private lateinit var toolBar: Toolbar
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        addToCart = findViewById(R.id.btnAddToCart)
        toolBar = findViewById(R.id.toolbar)
        progressLayout.visibility = View.VISIBLE
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("users_id", "0")
        layoutManager = LinearLayoutManager(this@DescriptionActivity)
        ClearCart(this@DescriptionActivity, restaurantId.toString()).execute()
        resName = intent.getStringExtra("res_name")
        setSupportActionBar(toolBar)
        supportActionBar?.title = resName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        restaurantId = intent.getStringExtra("id")
        addToCart.setOnClickListener {
            sharedPreferences.edit().putString("cart_user_id", userid).apply()
            sharedPreferences.edit().putString("res-id", restaurantId).apply()
            sharedPreferences.edit().putString("res-name", resName).apply()
            setUpCart()
        }
        setUpRestaurantMenu()
    }

    private fun setUpRestaurantMenu() {
        recyclerFood = findViewById(R.id.recyclerFood)
        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val queue = Volley.newRequestQueue(this@DescriptionActivity)

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    println("response is $it")
                    try {
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = FoodItem(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one")
                                )
                                foodList.add(foodObject)
                                descriptionAdapter = FoodRecyclerAdapter(
                                    this@DescriptionActivity,
                                    foodList,
                                    object : FoodRecyclerAdapter.OnItemClickListener {
                                        override fun onAddItemClick(food: FoodItem) {
                                            orderList.add(food)
                                            if (orderList.size > 0) {
                                                addToCart.visibility = View.VISIBLE
                                                FoodRecyclerAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(food: FoodItem) {
                                            orderList.remove(food)
                                            if (orderList.isEmpty()) {
                                                addToCart.visibility = View.GONE
                                                FoodRecyclerAdapter.isCartEmpty = true
                                            }
                                        }
                                    })
                                val mLayoutManager = LinearLayoutManager(this@DescriptionActivity)
                                recyclerFood.layoutManager = mLayoutManager
                                recyclerFood.itemAnimator = DefaultItemAnimator()
                                recyclerFood.adapter = descriptionAdapter
                            }
                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some error Occuried",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some catch error Occuried",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }, Response.ErrorListener {

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Context-type"] = "application/json"
                        headers["token"] = "b57bea4cb596a1"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun setUpCart() {

        val gson = Gson()

        val foodItems = gson.toJson(orderList)

        val async =
            ItemsOfCart(this@DescriptionActivity, restaurantId.toString(), foodItems, 1).execute()
        val result = async.get()
        println("result $result")
        if (result) {
            val data = Bundle()
            data.putString("resId", restaurantId)
            data.putString("resName", resName)
            val intent = Intent(this@DescriptionActivity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText((this@DescriptionActivity), "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    class ItemsOfCart(
        context: Context,
        private val restaurantId: String,
        private val foodItems: String,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertFood(OrderEntity(restaurantId.toInt(), foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteFood(OrderEntity(restaurantId.toInt(), foodItems))
                    db.close()
                    return true
                }
                3 -> {
                    db.orderDao().deleteOrders(restaurantId.toInt())
                    db.close()
                    return true
                }
            }

            return false
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
        showDialogBox()
    }

    private fun showDialogBox() {
        val dialog = AlertDialog.Builder(this@DescriptionActivity)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Do you want to log outGoing back will reset cart items.Do you still want to proceed?")
        dialog.setPositiveButton("Yes") { _, _ ->
            ClearCart(this@DescriptionActivity, restaurantId.toString()).execute()
            val intent = Intent(this@DescriptionActivity, MainActivity::class.java)
            startActivity(intent)
        }
        dialog.setNegativeButton("No") { _, _ ->

        }
        dialog.create()
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        showDialogBox()
        onBackPressed()
        return true
    }
}
