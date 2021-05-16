package com.vidya.fooddrop.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vidya.fooddrop.R
import com.vidya.fooddrop.R.*
import com.vidya.fooddrop.adapter.HomeRecyclerAdapter
import com.vidya.fooddrop.model.Restaurant
import com.vidya.fooddrop.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


class HomeFragment : Fragment() {
    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    val resList = arrayListOf<Restaurant>()
    private var checkedItem: Int = -1

    private var ratingComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
        if (restaurant1.ResRating.compareTo(restaurant2.ResRating, true) == 0) {
            restaurant1.ResRating.compareTo(restaurant2.ResRating, true)
        } else {
            restaurant1.ResRating.compareTo(restaurant2.ResRating, true)
        }
    }
    private var costLTHComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
        if (restaurant1.costForOne.compareTo(restaurant2.costForOne, true) == 0) {
            restaurant1.costForOne.compareTo(restaurant2.costForOne, true)
        } else {
            restaurant1.costForOne.compareTo(restaurant2.costForOne, true)
        }

    }
    private var costHTLComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
        if (restaurant1.costForOne.compareTo(restaurant2.costForOne, true) == 0) {
            restaurant1.costForOne.compareTo(restaurant2.costForOne, true)
        } else {
            restaurant1.costForOne.compareTo(restaurant2.costForOne, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)
        setHasOptionsMenu(true)

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    val data1 = it.getJSONObject("data")
                    val success = data1.getBoolean("success")
                    try {
                        if (success) {
                            progressLayout.visibility = View.GONE

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val resJsonObject = data.getJSONObject(i)
                                val resObject = Restaurant(
                                    resJsonObject.getString("id"),
                                    resJsonObject.getString("name"),
                                    resJsonObject.getString("rating"),
                                    resJsonObject.getString("cost_for_one"),
                                    resJsonObject.getString("image_url")
                                )
                                println("Image ${resJsonObject.getString("image_url")}")
                                resList.add(resObject)
                                println("resList $resList")
                                recyclerAdapter = HomeRecyclerAdapter(resList, activity as Context)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.actionSort -> showDialog(context as Context)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(context: Context) {

        val builder: AlertDialog.Builder? = AlertDialog.Builder(context)
        builder?.setTitle("Sort By?")
        builder?.setCancelable(false)
        builder?.setSingleChoiceItems(
            array.filters,
            checkedItem
        ) { _, isChecked ->
            checkedItem = isChecked
        }
        builder?.setPositiveButton("Ok") { _, _ ->
            when (checkedItem) {
                0 -> {
                    Collections.sort(resList, costLTHComparator)
                }
                1 -> {
                    Collections.sort(resList, costHTLComparator)
                    resList.reverse()
                }
                2 -> {
                    Collections.sort(resList, ratingComparator)
                    resList.reverse()
                }
            }
            recyclerAdapter.notifyDataSetChanged()
        }
        builder?.setNegativeButton("Cancel", null)
        builder?.create()
        builder?.show()
    }

}

