package com.ankurtambe.foodfunkyapp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.adapter.HistoryAdapter
import com.ankurtambe.foodfunkyapp.model.ModelHisItem
import com.ankurtambe.foodfunkyapp.model.ModelHistory
import com.ankurtambe.foodfunkyapp.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class HistoryFragment(val c: Context) : Fragment() {

    lateinit var recyclerHistory: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    var orderList = ArrayList<ModelHistory>()
    lateinit var recyclerAdapter: HistoryAdapter
    lateinit var prog: ImageView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_history, container, false)


        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )!!
        recyclerHistory = view.findViewById(R.id.recyclerHistory)
        layoutManager = LinearLayoutManager(activity)

        prog = view.findViewById(R.id.his_prog)

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            fetchData()
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("Error!")
            alterDialog.setMessage("No Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()
        }


        return view
    }

    private fun fetchData() {
        prog.visibility = View.VISIBLE

        try {

            val queue = Volley.newRequestQueue(activity as Context)

            val url =
                "http://13.235.250.119/v2/orders/fetch_result/" + sharedPreferences.getString(
                    "user_id",
                    "0"
                )

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {


                    val res = it.getJSONObject("data")
                    val success = res.getBoolean("success")

                    if (success) {
                        prog.visibility = View.INVISIBLE

                        val data = res.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            var itemList = arrayListOf<ModelHisItem>()
                            val orderJsonObject = data.getJSONObject(i)
                            val temp = orderJsonObject.getJSONArray("food_items")
                            for (j in 0 until temp.length()) {
                                val tempItem = temp.getJSONObject(j)
                                val itemObject = ModelHisItem(
                                    tempItem.getString("name"),
                                    tempItem.getString("cost")
                                )
                                itemList.add(itemObject)
                            }
                            val orderObject = ModelHistory(
                                orderJsonObject.getString("restaurant_name"),
                                orderJsonObject.getString("order_placed_at"),
                                itemList
                            )
                            orderList.add(orderObject)
                        }
                        println("Response is")
                        println(orderList)
                        recyclerAdapter = HistoryAdapter(activity as Context, orderList)

                        recyclerHistory.adapter = recyclerAdapter
                        recyclerHistory.layoutManager = layoutManager

                        if (orderList.size == 0) {//no items found
                            Toast.makeText(
                                activity as Context,
                                "Nothing has been Ordered!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    } else {
                        prog.visibility = View.INVISIBLE
                        Toast.makeText(
                            c,
                            "Error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    prog.visibility = View.INVISIBLE

                    Toast.makeText(
                        activity as Context,
                        "Error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"
                    headers["token"] = getString(R.string.token)

                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        } catch (e: JSONException) {
            prog.visibility = View.INVISIBLE
            Toast.makeText(
                activity as Context,
                "Some Error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onResume() {
        prog.visibility = View.INVISIBLE

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            fetchData()//if internet is available fetch data
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("Error!")
            alterDialog.setMessage("No Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }

}