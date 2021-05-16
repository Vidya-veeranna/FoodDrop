package com.vidya.fooddrop.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertFood(orderEntity: OrderEntity)

    @Delete
    fun deleteFood(orderEntity: OrderEntity)

    @Query("SELECT * FROM `order`")
    fun getAllOrders():List<OrderEntity>

    @Query("DELETE FROM `order` WHERE resid=:restaurantId")
    fun deleteOrders(restaurantId: Int)

}