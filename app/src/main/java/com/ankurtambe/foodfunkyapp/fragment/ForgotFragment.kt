package com.ankurtambe.foodfunkyapp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotFragment(val c: Context) : Fragment() {

    lateinit var etMobno: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button
    lateinit var prog: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_forgot, container, false)

        etMobno = view.findViewById(R.id.forg_phno)
        etEmail = view.findViewById(R.id.forg_email)
        btnNext = view.findViewById(R.id.forg_nexbtn)
        prog = view.findViewById(R.id.for_prog)

        btnNext.setOnClickListener {

            if (etMobno.text.isBlank()) {
                etMobno.error = "Missing!"
            } else {
                if (etEmail.text.isBlank()) {
                    etEmail.error = "Missing!"
                } else {

                    prog.visibility = View.VISIBLE

                    val mobileNumber = etMobno.text.toString()
                    val email = etEmail.text.toString()

                    val queue = Volley.newRequestQueue(activity as Context)

                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                    val jsonParams =
                        JSONObject().put("mobile_number", mobileNumber).put("email", email)


                    if (ConnectionManager().checkConnectivity(activity as Context)) {

                        val jsonObjectRequest =
                            object :
                                JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                                    try {
                                        val response = it.getJSONObject("data")
                                        val success = response.getBoolean("success")
                                        if (success) {
                                            val trial = response.getBoolean("first_try")
                                            if (trial) {
                                                Toast.makeText(
                                                    c,
                                                    "OTP sent!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                openOtpFragment()
                                            } else {
                                                Toast.makeText(
                                                    c,
                                                    "OTP sent!",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                                openOtpFragment()
                                            }
                                        } else {
                                            prog.visibility = View.INVISIBLE

                                            Toast.makeText(
                                                c,
                                                "User not registered! Please try again.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: JSONException) {
                                        prog.visibility = View.INVISIBLE

                                        Toast.makeText(
                                            c,
                                            "Some Error occurred!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }, Response.ErrorListener {
                                    prog.visibility = View.INVISIBLE

                                    Toast.makeText(c, "Error occurred!", Toast.LENGTH_SHORT)
                                        .show()
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = getString(R.string.token)
                                    return headers
                                }
                            }
                        queue.add(jsonObjectRequest)

                    } else {
                        prog.visibility = View.INVISIBLE

                        val alterDialog =
                            androidx.appcompat.app.AlertDialog.Builder(activity as Context)

                        alterDialog.setTitle("Error!")
                        alterDialog.setMessage("No Internet Connection!")
                        alterDialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                            startActivity(settingsIntent)

                        }

                        alterDialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(activity as Activity)
                        }
                        alterDialog.create()
                        alterDialog.show()
                    }
                }
            }
        }

        return view
    }

    fun openOtpFragment() {
        val transaction = fragmentManager?.beginTransaction()

        transaction?.replace(
            R.id.start_frame,
            OtpFragment(c, etMobno.text.toString())
        )

        transaction?.commit()
    }


    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(activity as Context)) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("Error!")
            alterDialog.setMessage("No Internet Connection")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }
}


