package com.ankurtambe.foodfunkyapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.model.ModelHisItem

class HisItemAdapter(val context: Context, var itemList: ArrayList<ModelHisItem>) :
    RecyclerView.Adapter<HisItemAdapter.OrderItemViewHolder>() {
    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.textViewOrderItem)
        val dishPrice: TextView = view.findViewById(R.id.textViewOrderItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart_rv, parent, false)

        return OrderItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val dish = itemList[position]
        holder.dishName.text = dish.itemName
        val temp = "₹" + dish.itemPrice
        holder.dishPrice.text = temp
    }
}