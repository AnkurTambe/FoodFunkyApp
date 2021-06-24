package com.ankurtambe.foodfunkyapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.fragment.StartFragment


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("user_logged_in", false)) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            openFrag(StartFragment(this))
        }



    }


    private fun openFrag(YourFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.start_frame, YourFragment)
        transaction.commit()
    }


    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.start_frame)) {
            !is StartFragment -> openFrag(StartFragment(this))

            else -> super.onBackPressed()
        }
    }
}