package com.vidya.fooddrop.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.vidya.fooddrop.R
import com.vidya.fooddrop.activity.DescriptionActivity
import com.vidya.fooddrop.database.RestaurantDatabase
import com.vidya.fooddrop.database.RestaurantEntity
import com.vidya.fooddrop.model.Restaurant

class HomeRecyclerAdapter(
    private var itemList: ArrayList<Restaurant>,
    val context: Context
) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtResName.text = restaurant.ResName
        holder.txtResRating.text = restaurant.ResRating

        val costForOne = "${restaurant.costForOne}/person"
        holder.txtResPrice.text = costForOne
        Picasso.get().load(restaurant.ResImage).error(R.mipmap.ic_launcher).into(holder.imgRes)

        holder.llContext.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("id", restaurant.ResId)
            intent.putExtra("res_name", restaurant.ResName)
            context.startActivity(intent)

        }

        val restaurantEntity = RestaurantEntity(
            restaurant.ResId.toInt(),
            holder.txtResName.text.toString(),
            holder.txtResRating.text.toString(),
            restaurant.costForOne,
            holder.imgRes.toString()
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()

        val isfav = checkFav.get()

        if (isfav) {
            holder.imgFav.setImageResource(R.drawable.ic_rating_fav)
        } else {
            holder.imgFav.setImageResource(R.drawable.ic_rating)
        }

        holder.imgFav.setOnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant added to Favourites", Toast.LENGTH_SHORT)
                        .show()
                    holder.imgFav.setImageResource(R.drawable.ic_rating_fav)
                } else {
                    Toast.makeText(context, "Some error occuried!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgFav.setImageResource(R.drawable.ic_rating)
                } else {
                    Toast.makeText(context, "Some error occuried!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResName: TextView = view.findViewById(R.id.txtResName)
        val txtResPrice: TextView = view.findViewById(R.id.txtResPrice)
        val txtResRating: TextView = view.findViewById(R.id.txtResRating)
        val imgRes: ImageView = view.findViewById(R.id.imgResImage)
        val llContext: LinearLayout = view.findViewById(R.id.llContext)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg p0: Void?): Boolean {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
                .build()
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

}