package com.vidya.fooddrop.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Restaurant")
data class RestaurantEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "res_name") val resName : String,
    @ColumnInfo(name = "res_rating") val resRating : String,
    @ColumnInfo(name = "cost_for_one") val costForOne : String,
    @ColumnInfo(name = "res_image") val resImage : String
)