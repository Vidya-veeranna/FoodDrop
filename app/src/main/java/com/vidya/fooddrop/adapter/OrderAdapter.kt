package com.vidya.fooddrop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vidya.fooddrop.R
import com.vidya.fooddrop.model.FoodItem
import com.vidya.fooddrop.model.OrderDetails
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderAdapter(val context: Context, private val orderHistoryList: ArrayList<OrderDetails>) :
    RecyclerView.Adapter<OrderAdapter.OrderHistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_order_single_row, parent, false)
        return OrderHistoryViewHolder(view)

    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orderHistoryObject = orderHistoryList[position]
        holder.txtResName.text = orderHistoryObject.resName
        holder.txtDate.text = formatDate(orderHistoryObject.orderDate)
        setUpRecycler(holder.recyclerOrder, orderHistoryObject)
    }

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResName: TextView = view.findViewById(R.id.txtResNameOrder)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerOrder: RecyclerView = view.findViewById(R.id.recyclerOrder)
    }

    private fun setUpRecycler(recyclerOrder: RecyclerView, orderHistoryList: OrderDetails) {
        val foodItems = ArrayList<FoodItem>()
        for (i in 0 until orderHistoryList.foodItem.length()) {
            val foodjson = orderHistoryList.foodItem.getJSONObject(i)
            foodItems.add(
                FoodItem(
                    foodjson.getString("food_item_id"),
                    foodjson.getString("name"),
                    foodjson.getString("cost")
                )
            )
        }
        val cartItemAdapter = CartAdapter(foodItems, context)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerOrder.layoutManager = mLayoutManager
        recyclerOrder.adapter = cartItemAdapter
    }

    private fun formatDate(dateString: String): String {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}