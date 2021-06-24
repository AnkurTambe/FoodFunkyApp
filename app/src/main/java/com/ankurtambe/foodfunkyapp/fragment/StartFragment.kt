package com.ankurtambe.foodfunkyapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ankurtambe.foodfunkyapp.R


class StartFragment(val c: Context) : Fragment() {

    lateinit var lgnBtn: Button
    lateinit var sgnBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_start, container, false)

        lgnBtn = v.findViewById(R.id.login_btn)
        sgnBtn = v.findViewById(R.id.signup_btn)

        lgnBtn.setOnClickListener {
            val fragment = LoginFragment(c)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.start_frame, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        sgnBtn.setOnClickListener {
            val fragment = SignupFragment(c)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.start_frame, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return v
    }
}