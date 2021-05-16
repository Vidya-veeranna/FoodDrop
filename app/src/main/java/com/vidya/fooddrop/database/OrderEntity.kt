package com.vidya.fooddrop.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray

@Entity(tableName = "Order")
data class OrderEntity (
    @PrimaryKey(autoGenerate = true) val resid: Int,
    @ColumnInfo(name = "food_items") val foodItems : String
)