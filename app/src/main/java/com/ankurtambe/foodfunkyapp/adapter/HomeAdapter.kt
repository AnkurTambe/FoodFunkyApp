package com.ankurtambe.foodfunkyapp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.activity.MenuActivity
import com.ankurtambe.foodfunkyapp.database.RestaurantDatabase
import com.ankurtambe.foodfunkyapp.database.RestaurantEntity
import com.ankurtambe.foodfunkyapp.model.ModelRestaurant
import com.squareup.picasso.Picasso


class HomeAdapter(val context: Context, private var itemList: ArrayList<ModelRestaurant>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolderDashboard>() {

    class ViewHolderDashboard(view: View) : RecyclerView.ViewHolder(view) {


        val imageViewRestaurant: ImageView = view.findViewById(R.id.hrv_iv)
        val textViewRestaurantName: TextView = view.findViewById(R.id.hrv_name)
        val textViewPricePerPerson: TextView = view.findViewById(R.id.hrv_price)
        val textViewRating: TextView = view.findViewById(R.id.hrv_rating)
        val rlContent: RelativeLayout = view.findViewById(R.id.hrv_rl)
        val textViewFavourite: TextView = view.findViewById(R.id.hrv_fav)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDashboard {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_rv, parent, false)

        return ViewHolderDashboard(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolderDashboard, position: Int) {
        /*val text = arrayList.get(position)
        holder.itemView.text=text  // save the data to all the view in the dashboard recycler view single row*/

        val restaurant =
            itemList[position]//gets the item from the itemList sent in the constructor at the position
        //holder.textView.text=text//fill in the recieved data in the holder

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId,
            restaurant.restaurantName
        )

        holder.textViewFavourite.setOnClickListener(View.OnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val result = DBAsyncTask(context, restaurantEntity, 2).execute().get()

                if (result) {

                    Toast.makeText(context, "Added to favourites!", Toast.LENGTH_SHORT).show()

                    holder.textViewFavourite.tag = "liked"//new value
                    holder.textViewFavourite.background =
                        context.resources.getDrawable(R.drawable.ic_fav_fill)
                } else {

                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()

                }

            } else {

                val result = DBAsyncTask(context, restaurantEntity, 3).execute().get()

                if (result) {

                    Toast.makeText(context, "Removed from favourites!", Toast.LENGTH_SHORT).show()

                    holder.textViewFavourite.tag = "unliked"
                    holder.textViewFavourite.background =
                        context.resources.getDrawable(R.drawable.ic_fav_border)
                } else {

                    Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show()

                }

            }
        })


        holder.rlContent.setOnClickListener(View.OnClickListener {

            println(holder.textViewRestaurantName.tag.toString())

            val intent = Intent(context, MenuActivity::class.java)

            intent.putExtra("restaurantId", holder.textViewRestaurantName.tag.toString())

            intent.putExtra("restaurantName", holder.textViewRestaurantName.text.toString())


            context.startActivity(intent)


        })


        holder.textViewRestaurantName.setTag(restaurant.restaurantId + "")
        holder.textViewRestaurantName.text = restaurant.restaurantName
        holder.textViewPricePerPerson.text = "₹" + restaurant.cost_for_one + "/Person "
        holder.textViewRating.text = restaurant.restaurantRating
        //holder.imageViewBook.setBackgroundResource(book.bookImage)
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_spoon_knife)
            .into(holder.imageViewRestaurant);//if the image is not displayed properly we display default image in the error part


        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.textViewFavourite.tag = "liked"
            holder.textViewFavourite.background =
                context.resources.getDrawable(R.drawable.ic_fav_fill)

        } else {
            holder.textViewFavourite.tag = "unliked"
            holder.textViewFavourite.background =
                context.resources.getDrawable(R.drawable.ic_fav_border)
        }

    }

    fun filterList(filteredList: ArrayList<ModelRestaurant>) {//to update the recycler view depending on the search
        itemList = filteredList
        notifyDataSetChanged()
    }


    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        private val db =
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
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else -> return false

            }

        }


    }
}

