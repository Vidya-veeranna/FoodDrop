package com.vidya.fooddrop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vidya.fooddrop.R
import com.vidya.fooddrop.model.FoodItem

class CartAdapter(private val itemList: ArrayList<FoodItem>, val context: Context) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)

        return CartViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObject = itemList[position]
        holder.itemName.text = cartObject.foodName
        val cost = "Rs.${cartObject.costForOne}"
        holder.itemCost.text = cost
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.txtFoodNameCart)
        val itemCost: TextView = view.findViewById(R.id.txtPrice)
    }

}