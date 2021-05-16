package com.vidya.fooddrop.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.vidya.fooddrop.R
import com.vidya.fooddrop.adapter.HomeRecyclerAdapter
import com.vidya.fooddrop.database.RestaurantDatabase
import com.vidya.fooddrop.database.RestaurantEntity
import com.vidya.fooddrop.model.Restaurant

/**
 * A simple [Fragment] subclass.
 */
class FavouriteRestaurantsFragment : Fragment() {
    private lateinit var rlFavourites: RelativeLayout
    private lateinit var recyclerFavourites: RecyclerView
    private lateinit var HomeRecyclerAdapter: HomeRecyclerAdapter
    private var restaurantList = arrayListOf<Restaurant>()
    private lateinit var rlNoFav: RelativeLayout
    private lateinit var rlLoading: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

        rlFavourites = view.findViewById(R.id.rlFavourites)
        recyclerFavourites = view.findViewById(R.id.recyclerFavourites)
        rlNoFav = view.findViewById(R.id.rlNoFavourites)
        rlLoading = view.findViewById(R.id.rlLoading)

        rlLoading.visibility = View.VISIBLE
        setUpRecycler(view)
        return view
    }

    private fun setUpRecycler(view: View) {
        recyclerFavourites = view.findViewById(R.id.recyclerFavourites)

        val backgroundList = FavouritesAsync(activity as Context).execute().get()
        if (backgroundList.isEmpty()) {
            rlLoading.visibility = View.GONE
            rlFavourites.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        } else {
            rlFavourites.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
            rlNoFav.visibility = View.GONE

            println("favList $backgroundList")

            for (i in backgroundList) {
                restaurantList.add(
                    Restaurant(
                        i.id.toString(),
                        i.resName,
                        i.resRating,
                        i.costForOne,
                        i.resImage
                    )
                )
            }

            HomeRecyclerAdapter = HomeRecyclerAdapter(restaurantList, activity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerFavourites.layoutManager = mLayoutManager
            recyclerFavourites.itemAnimator = DefaultItemAnimator()
            recyclerFavourites.adapter = HomeRecyclerAdapter
            recyclerFavourites.setHasFixedSize(true)
        }

    }

    class FavouritesAsync(context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {

        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            println("resFav ${db.restaurantDao().getAllRestaurant()}")
            return db.restaurantDao().getAllRestaurant()
        }

    }

}
