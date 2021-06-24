package com.ankurtambe.foodfunkyapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.ankurtambe.foodfunkyapp.R

class OrderActivity : AppCompatActivity() {

    private lateinit var btnOkay: Button
    private lateinit var orderPlaced: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        orderPlaced = findViewById(R.id.orderSuccessfullyPlaced)
        btnOkay = findViewById(R.id.buttonOkay)

        btnOkay.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, DashboardActivity::class.java)

            startActivity(intent)

            finishAffinity()//finish all the activities
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)

        startActivity(intent)

        finishAffinity()
    }

}
