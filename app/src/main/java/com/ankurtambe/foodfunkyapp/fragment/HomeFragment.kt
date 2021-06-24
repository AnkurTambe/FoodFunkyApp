package com.ankurtambe.foodfunkyapp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.adapter.HomeAdapter
import com.ankurtambe.foodfunkyapp.model.ModelRestaurant
import com.ankurtambe.foodfunkyapp.util.ConnectionManager
import kotlinx.android.synthetic.main.radiogroup_sort.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment(val c: Context) : Fragment() {


    lateinit var recyclerView: RecyclerView

    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var dashboardAdapter: HomeAdapter
    lateinit var editTextSearch: EditText
    lateinit var radioButtonView: View
    lateinit var unableSearch: RelativeLayout
    lateinit var prog: ImageView


    var restaurantInfoList = arrayListOf<ModelRestaurant>()


    var ratingComparator = Comparator<ModelRestaurant> { rest1, rest2 ->

        if (rest1.restaurantRating.compareTo(rest2.restaurantRating, true) == 0) {
            rest1.restaurantName.compareTo(rest2.restaurantName, true)
        } else {
            rest1.restaurantRating.compareTo(rest2.restaurantRating, true)
        }

    }

    var costComparator = Comparator<ModelRestaurant> { rest1, rest2 ->

        rest1.cost_for_one.compareTo(rest2.cost_for_one, true)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

        recyclerView =
            view.findViewById(R.id.recyclerViewDashboard)//recycler view from Dashboard fragment

        editTextSearch = view.findViewById(R.id.home_search)


        unableSearch = view.findViewById(R.id.home_unable_search)

        prog = view.findViewById(R.id.home_prog)



        fun filterFun(strTyped: String) {//to filter the recycler view depending on what is typed
            val filteredList = arrayListOf<ModelRestaurant>()

            for (item in restaurantInfoList) {
                if (item.restaurantName.toLowerCase()
                        .contains(strTyped.toLowerCase())
                ) {//to ignore case and if contained add to new list

                    filteredList.add(item)

                }
            }

            if (filteredList.size == 0) {
                unableSearch.visibility = View.VISIBLE
            } else {
                unableSearch.visibility = View.INVISIBLE
            }

            dashboardAdapter.filterList(filteredList)

        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            //as the user types the search filter is applied
            override fun afterTextChanged(strTyped: Editable?) {
                filterFun(strTyped.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )


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
                        println("Response(Home) is $it")

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            prog.visibility = View.INVISIBLE

                            val data = responseJsonObjectData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = ModelRestaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)

                                dashboardAdapter = HomeAdapter(
                                    activity as Context,
                                    restaurantInfoList
                                )//set the adapter with the data

                                recyclerView.adapter =
                                    dashboardAdapter//bind the  recyclerView to the adapter

                                recyclerView.layoutManager =
                                    gridLayoutManager //bind the  recyclerView to the layoutManager

                                recyclerView.setHasFixedSize(true)
                            }

                        }
                    },
                    Response.ErrorListener {
                        prog.visibility = View.INVISIBLE

                        println("Error(Home)$it")
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.sort -> {
                radioButtonView = View.inflate(
                    c,
                    R.layout.radiogroup_sort,
                    null
                )//radiobutton view for sorting display
                androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButtonView.sort_high_to_low.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            restaurantInfoList.reverse()
                            dashboardAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.sort_low_to_high.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            dashboardAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.sort_rating.isChecked) {
                            Collections.sort(restaurantInfoList, ratingComparator)
                            restaurantInfoList.reverse()
                            dashboardAdapter.notifyDataSetChanged()//updates the adapter
                        }
                    }
                    .setNegativeButton("CANCEL") { text, listener ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {//once setting is opened to turn internet we again check for connection

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            if (restaurantInfoList.isEmpty())//if no data is loaded previously load new data
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