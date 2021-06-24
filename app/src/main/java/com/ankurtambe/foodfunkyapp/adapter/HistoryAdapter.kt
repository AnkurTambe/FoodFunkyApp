package com.ankurtambe.foodfunkyapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.model.ModelHistory


class HistoryAdapter(context: Context, var orderList: ArrayList<ModelHistory>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    var context = context

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantName: TextView = view.findViewById(R.id.textViewResturantName)
        val orderData: TextView = view.findViewById(R.id.textViewDate)
        val recyclerHisItem: RecyclerView = view.findViewById(R.id.recyclerViewItemsOrdered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_rv, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val order = orderList[position]
        holder.restaurantName.text = order.restaurantName
        holder.orderData.text = order.orderDate


        var recyclerAdapter = HisItemAdapter(context, order.itemList)
        val layoutManager = LinearLayoutManager(context)

        holder.recyclerHisItem.adapter = recyclerAdapter
        holder.recyclerHisItem.layoutManager = layoutManager
    }
}