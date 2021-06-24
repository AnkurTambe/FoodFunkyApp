package com.ankurtambe.foodfunkyapp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.adapter.HomeAdapter
import com.ankurtambe.foodfunkyapp.database.RestaurantDatabase
import com.ankurtambe.foodfunkyapp.database.RestaurantEntity
import com.ankurtambe.foodfunkyapp.model.ModelRestaurant
import com.ankurtambe.foodfunkyapp.util.ConnectionManager
import org.json.JSONException

class FavouriteFragment(val c: Context) : Fragment() {


    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: GridLayoutManager
    lateinit var prog: ImageView
    lateinit var favouriteAdapter: HomeAdapter//using the same adapter here as it has the same functionality

    public var restaurantInfoList = arrayListOf<ModelRestaurant>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)//set the layout manager

        recyclerView =
            view.findViewById(R.id.recyclerViewFavouriteRestaurant)//recycler view from Dashboard fragment

        prog = view.findViewById(R.id.fav_prog)

        return view
    }

    private fun fetchData() {

        prog.visibility = View.VISIBLE

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            try {

                val queue = Volley.newRequestQueue(activity as Context)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        println("Response(Fav) is $it")

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            prog.visibility = View.INVISIBLE

                            restaurantInfoList.clear()//old listener of jsonObjectRequest are still listening therefore clear is used


                            val data = responseJsonObjectData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)

                                val restaurantEntity = RestaurantEntity(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name")
                                )

                                if (DBAsynTask(c, restaurantEntity, 1).execute()
                                        .get()
                                )//if restaurant present add
                                {

                                    val restaurantObject = ModelRestaurant(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("image_url")
                                    )

                                    restaurantInfoList.add(restaurantObject)

                                    //progressBar.visibility = View.GONE

                                    favouriteAdapter = HomeAdapter(
                                        activity as Context,
                                        restaurantInfoList
                                    )//set the adapter with the data

                                    recyclerView.adapter =
                                        favouriteAdapter//bind the  recyclerView to the adapter

                                    recyclerView.layoutManager =
                                        layoutManager //bind the  recyclerView to the layoutManager

                                    recyclerView.setHasFixedSize(true)


                                }
                            }
                            if (restaurantInfoList.size == 0) {//no items found
                                Toast.makeText(
                                    activity as Context,
                                    "Nothing is added to Favourites",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } else {
                            prog.visibility = View.INVISIBLE

                            Toast.makeText(
                                activity as Context,
                                "Error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        prog.visibility = View.INVISIBLE
                        println("Error(Fav) is $it")

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

        } else {
            prog.visibility = View.INVISIBLE

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

    }


    class DBAsynTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            /*
            * Mode 1->check if restaurant is in favourites
            * Mode 2->Save the restaurant into DB as favourites
            * Mode 3-> Remove the favourite restaurant*/


            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return restaurant != null
                }
                else -> return false

            }

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
