package com.vidya.fooddrop.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vidya.fooddrop.R
import com.vidya.fooddrop.adapter.OrderAdapter
import com.vidya.fooddrop.model.OrderDetails
import com.vidya.fooddrop.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class OrderHistoryFragment : Fragment() {
    private lateinit var recyclerOrder: RecyclerView
    var orderHistoryList = ArrayList<OrderDetails>()
    private lateinit var recyclerAdapter: OrderAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var noOrder: RelativeLayout
    private lateinit var llOrder: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var progressLayout: RelativeLayout
    private var userId = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        sharedPreferences = (activity as Context).getSharedPreferences(
            getString(R.string.preference_name),
            Context.MODE_PRIVATE
        )
        recyclerOrder = view.findViewById(R.id.recyclerOrder)
        noOrder = view.findViewById(R.id.rlNoOrder)
        llOrder = view.findViewById(R.id.llOrder)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar.visibility = VISIBLE
        layoutManager = LinearLayoutManager(activity)
        userId = sharedPreferences.getString("used_id", "0")

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            progressLayout.visibility = View.GONE

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    val data1 = it.getJSONObject("data")
                    val success = data1.getBoolean("success")
                    try {
                        if (success) {

                            val data = data1.getJSONArray("data")

                            if (data.length() == 0) {
                                llOrder.visibility = GONE
                                noOrder.visibility = VISIBLE
                            } else {
                                for (i in 0 until data.length()) {
                                    val orderJsonObject = data.getJSONObject(i)
                                    val orderObject = OrderDetails(
                                        orderJsonObject.getString("order_id"),
                                        orderJsonObject.getString("restaurant_name"),
                                        orderJsonObject.getString("total_cost"),
                                        orderJsonObject.getString("order_placed_at"),
                                        orderJsonObject.getJSONArray("food_items")
                                    )
                                    orderHistoryList.add(orderObject)

                                    if (orderHistoryList.isEmpty()) {
                                        llOrder.visibility = GONE
                                        noOrder.visibility = VISIBLE
                                    } else {
                                        llOrder.visibility = VISIBLE
                                        noOrder.visibility = GONE

                                        if (activity != null) {
                                            recyclerAdapter =
                                                OrderAdapter(activity as Context, orderHistoryList)
                                            recyclerOrder.adapter = recyclerAdapter
                                            recyclerOrder.layoutManager = layoutManager
                                        } else {
                                            queue.cancelAll(this::class.java.simpleName)
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error Occuried",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

}
