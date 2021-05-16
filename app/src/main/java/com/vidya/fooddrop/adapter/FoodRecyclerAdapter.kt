package com.vidya.fooddrop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vidya.fooddrop.R
import com.vidya.fooddrop.model.FoodItem

class FoodRecyclerAdapter(
    val context: Context,
    private val itemList: ArrayList<FoodItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FoodRecyclerAdapter.FoodViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_food_single_row, parent, false)

        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(food: FoodItem)
        fun onRemoveItemClick(food: FoodItem)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = itemList[position]
        val cost = "Rs.${food.costForOne}"

        holder.txtNumber.text = (position + 1).toString()
        holder.txtFoodName.text = food.foodName
        holder.txtFoodPrice.text = cost

        holder.add.setOnClickListener {
            holder.add.visibility = View.GONE
            holder.remove.visibility = View.VISIBLE
            listener.onAddItemClick(food)
        }

        holder.remove.setOnClickListener {
            holder.remove.visibility = View.GONE
            holder.add.visibility = View.VISIBLE
            listener.onRemoveItemClick(food)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNumber: TextView = view.findViewById(R.id.txtNumber)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val add: Button = view.findViewById(R.id.btnAdd)
        val remove: Button = view.findViewById(R.id.btnRemove)
    }

}