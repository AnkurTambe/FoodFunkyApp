package com.ankurtambe.foodfunkyapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.fragment.*
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var headerTxt1: TextView
    lateinit var headerTxt2: TextView
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItemSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        sharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        coordinatorLayout = findViewById(R.id.coordinator_layout)
        toolbar = findViewById(R.id.dash_tb)
        frameLayout = findViewById(R.id.dash_frame)
        navigationView = findViewById(R.id.dash_nv)
        drawerLayout = findViewById(R.id.dash_drawer_layout)

        val headerView = navigationView.getHeaderView(0)

        headerTxt1 = headerView.findViewById(R.id.header_tv1)
        headerTxt2 = headerView.findViewById(R.id.header_tv2)

        navigationView.menu.getItem(0).isCheckable = true
        navigationView.menu.getItem(0).isChecked = true

        setToolBar()
        toolbar.navigationIcon?.setColorFilter(
            resources.getColor(R.color.creme),
            PorterDuff.Mode.SRC_ATOP
        )

        headerTxt1.text = sharedPreferences.getString("name", "Person")
        val s = "+91-" + sharedPreferences.getString("mobile_number", "9999999999")

        headerTxt2.text = s

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItemSelected != null) {
                previousMenuItemSelected?.isChecked = false
            }

            previousMenuItemSelected = it

            it.isCheckable = true
            it.isChecked = true


            when (it.itemId) {
                R.id.homee -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.my_profile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.dash_frame,
                            ProfileFragment(this)
                        )
                        .commit()

                    supportActionBar?.title =
                        "My Profile"

                    drawerLayout.closeDrawers()
                }
                R.id.favourite_restaurants -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.dash_frame,
                            FavouriteFragment(this)
                        )
                        .commit()

                    supportActionBar?.title =
                        "Favourite Restaurants"

                    drawerLayout.closeDrawers()

                }
                R.id.order_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.dash_frame,
                            HistoryFragment(this)
                        )
                        .commit()

                    supportActionBar?.title =
                        "Order History"

                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.dash_frame,
                            FaqsFragment()
                        )
                        .commit()

                    supportActionBar?.title =
                        "Frequently Asked Questions"

                    drawerLayout.closeDrawers()

                }
                R.id.abt_app -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.dash_frame,
                            AboutFragment()
                        )
                        .commit()

                    supportActionBar?.title =
                        "App Description"

                    drawerLayout.closeDrawers()

                }
                R.id.logout -> {

                    drawerLayout.closeDrawers()

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)

                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Are you sure you want to log out?")
                    alterDialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().putBoolean("user_logged_in", false).apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    alterDialog.setNegativeButton("No") { text, listener ->

                    }
                    alterDialog.create()
                    alterDialog.show()

                }
            }

            return@setNavigationItemSelectedListener true
        }

        openHome()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {

        when (supportFragmentManager.findFragmentById(R.id.dash_frame)) {
            !is HomeFragment -> {
                navigationView.menu.getItem(0).isChecked = true
                openHome()
            }
            else -> super.onBackPressed()
        }
    }

    private fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }


    private fun openHome() {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(
            R.id.dash_frame,
            HomeFragment(this)
        )

        transaction.commit()

        supportActionBar?.title =
            "All Restaurants"

        navigationView.setCheckedItem(R.id.homee)
    }

    override fun onResume() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        super.onResume()
    }


}
