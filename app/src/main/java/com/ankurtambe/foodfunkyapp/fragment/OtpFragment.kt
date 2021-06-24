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

class OtpFragment(val c: Context, val m: String) : Fragment() {

    lateinit var etOtp: EditText
    lateinit var etNPass: EditText
    lateinit var etConPass: EditText
    lateinit var btnSub: Button
    lateinit var prog: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_otp, container, false)

        etOtp = view.findViewById(R.id.otp_otp)
        etNPass = view.findViewById(R.id.otp_pass)
        etConPass = view.findViewById(R.id.otp_con_pass)
        btnSub = view.findViewById(R.id.otp_subbtn)
        prog = view.findViewById(R.id.otp_prog)

        btnSub.setOnClickListener(View.OnClickListener {
            if (etNPass.text.toString().length < 6 && etConPass.text.toString().length < 6) {
                etConPass.error = "Min. 6 char."
                etNPass.error = "Min. 6 char."
            } else {
                if (etOtp.text.isBlank()) {
                    etOtp.error = "Missing!"
                } else {
                    if (etNPass.text.isBlank()) {
                        etNPass.error = "Missing!"
                    } else {
                        if (etConPass.text.isBlank()) {
                            etConPass.error = "Missing!"
                        } else {
                            if ((etNPass.text.toString()
                                        == etConPass.text.toString())
                            ) {
                                val otpText = etOtp.text.toString()
                                val pwd = etNPass.text.toString()

                                prog.visibility = View.VISIBLE


                                val queue = Volley.newRequestQueue(activity as Context)
                                val url = "http://13.235.250.119/v2/reset_password/fetch_result/"
                                val jsonParams =
                                    JSONObject().put("mobile_number", m).put("password", pwd)
                                        .put("otp", otpText)
                                if (ConnectionManager().checkConnectivity(activity as Context)) {

                                    val jsonObjectRequest = object : JsonObjectRequest(
                                        Method.POST,
                                        url,
                                        jsonParams,
                                        Response.Listener {

                                            try {
                                                val res = it.getJSONObject("data")
                                                val success = res.getBoolean("success")
                                                if (success) {
                                                    val trial = res.getString("successMessage")
                                                    if (trial.compareTo(
                                                            "Password has successfully changed.",
                                                            false
                                                        ) == 0
                                                    ) {
                                                        Toast.makeText(
                                                            c,
                                                            "Password has successfully changed.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        passwordChanged()
                                                    } else {

                                                        prog.visibility = View.INVISIBLE

                                                        Toast.makeText(
                                                            c,
                                                            "Incorrect OTP! Please try again.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                } else {

                                                    prog.visibility = View.INVISIBLE

                                                    Toast.makeText(
                                                        c,
                                                        "Error occurred! Please try again.",
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

                                        },
                                        Response.ErrorListener {

                                            prog.visibility = View.INVISIBLE

                                            Toast.makeText(
                                                c,
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

                            } else {

                                prog.visibility = View.INVISIBLE

                                etConPass.error = "Passwords don't match"

                            }
                        }
                    }
                }

            }
        })

        return view
    }

    fun passwordChanged() {
        val transaction = fragmentManager?.beginTransaction()

        transaction?.replace(
            R.id.start_frame,
            LoginFragment(c)
        )

        transaction?.commit()
    }


    override fun onResume() {

        prog.visibility = View.INVISIBLE

        if (!ConnectionManager().checkConnectivity(activity as Context)) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
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