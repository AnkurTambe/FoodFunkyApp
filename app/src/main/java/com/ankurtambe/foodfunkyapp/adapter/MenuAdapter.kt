package com.ankurtambe.foodfunkyapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.activity.CartActivity
import com.ankurtambe.foodfunkyapp.model.ModelMenu

class MenuAdapter(
    val context: Context,
    private val restaurantId: String,
    private val restaurantName: String,
    private val proceedToCartPassed: RelativeLayout,
    private val buttonProceedToCart: Button,
    private val restaurantMenu: ArrayList<ModelMenu>
) : RecyclerView.Adapter<MenuAdapter.ViewHolderRestaurantMenu>() {


    var itemSelectedCount: Int = 0
    lateinit var proceedToCart: RelativeLayout


    var itemsSelectedId = arrayListOf<String>()


    class ViewHolderRestaurantMenu(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSerialNumber: TextView = view.findViewById(R.id.textViewSerialNumber)
        val textViewItemName: TextView = view.findViewById(R.id.textViewItemName)
        val textViewItemPrice: TextView = view.findViewById(R.id.textViewItemPrice)
        val buttonAddToCart: Button = view.findViewById(R.id.buttonAddToCart)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_rv, parent, false)

        return ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
        restaurantMenu.size
        return restaurantMenu.size
    }

    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {
        val restaurantMenuItem = restaurantMenu[position]

        proceedToCart = proceedToCartPassed//button view passed from the RestaurantMenuActivity

        //click listener to the button view Passed from activity which has the button proceed to cart
        buttonProceedToCart.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, CartActivity::class.java)

            intent.putExtra(
                "restaurantId",
                restaurantId.toString()
            )// pass the restaurant id to the next activity

            intent.putExtra("restaurantName", restaurantName)

            intent.putExtra(
                "selectedItemsId",
                itemsSelectedId
            )//pass all the items selected by the user

            context.startActivity(intent)

        })


        holder.buttonAddToCart.setOnClickListener(View.OnClickListener {

            if (holder.buttonAddToCart.text.toString() == "Remove") {
                itemSelectedCount--//unselected

                itemsSelectedId.remove(holder.buttonAddToCart.tag.toString())

                holder.buttonAddToCart.text = "Add"


            } else {
                itemSelectedCount++//selected

                itemsSelectedId.add(holder.buttonAddToCart.getTag().toString())


                holder.buttonAddToCart.text = "Remove"

            }

            if (itemSelectedCount > 0) {
                proceedToCart.visibility = View.VISIBLE
            } else {
                proceedToCart.visibility = View.INVISIBLE
            }

        })

        holder.buttonAddToCart.tag =
            restaurantMenuItem.id + ""//save the item id in textViewName Tag ,will be used to add to cart
        holder.textViewSerialNumber.text = (position + 1).toString()//position starts from 0
        holder.textViewItemName.text = restaurantMenuItem.name
        holder.textViewItemPrice.text = "₹" + restaurantMenuItem.cost_for_one

    }

    fun getSelectedItemCount(): Int {
        return itemSelectedCount
    }

}