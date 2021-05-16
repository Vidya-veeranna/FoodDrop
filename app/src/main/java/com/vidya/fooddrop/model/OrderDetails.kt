package com.vidya.fooddrop.model

import com.google.gson.JsonArray
import org.json.JSONArray

data class OrderDetails (
    var orderId : String,
    var resName : String,
    var totalCost : String,
    var orderDate : String,
    var foodItem : JSONArray
)