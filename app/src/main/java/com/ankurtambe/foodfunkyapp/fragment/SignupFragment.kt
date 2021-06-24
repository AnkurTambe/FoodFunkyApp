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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ankurtambe.foodfunkyapp.R
import com.ankurtambe.foodfunkyapp.activity.DashboardActivity
import com.ankurtambe.foodfunkyapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class SignupFragment(val c: Context) : Fragment() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobno: EditText
    lateinit var etAddress: EditText
    lateinit var etPass: EditText
    lateinit var etConpass: EditText
    lateinit var btnSignup: Button
    lateinit var prog: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_signup, container, false)

        etName = view.findViewById(R.id.signup_name)
        etEmail = view.findViewById(R.id.signup_email)
        etMobno = view.findViewById(R.id.signup_phno)
        etAddress = view.findViewById(R.id.signup_address)
        etPass = view.findViewById(R.id.signup_pass)
        etConpass = view.findViewById(R.id.signup_con_pass)
        btnSignup = view.findViewById(R.id.signup_sgn_btn)
        prog = view.findViewById(R.id.sign_prog)

        btnSignup.setOnClickListener(View.OnClickListener {
            regBtnClicked()
        })

        return view
    }

    fun registrySuccess() {
        openDashBoard()
    }

    private fun openDashBoard() {

        val intent = Intent(activity as Context, DashboardActivity::class.java)
        startActivity(intent)
        activity?.finish()

    }

    private fun regBtnClicked() {

        val sharedPreferences =
            c.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)


        sharedPreferences.edit().putBoolean("user_logged_in", false).apply()

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            if (errors()) {

                prog.visibility = View.VISIBLE


                try {
                    val registerUser = JSONObject()
                    registerUser.put("name", etName.text)
                    registerUser.put("mobile_number", etMobno.text)
                    registerUser.put("password", etPass.text)
                    registerUser.put("address", etAddress.text)
                    registerUser.put("email", etEmail.text)

                    val queue = Volley.newRequestQueue(activity as Context)

                    val url =
                        "http://13.235.250.119/v2/register/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        registerUser,
                        Response.Listener {
                            println("Response(register) is $it")

                            val responseJsonObjectData = it.getJSONObject("data")

                            val success = responseJsonObjectData.getBoolean("success")

                            if (success) {

                                val data = responseJsonObjectData.getJSONObject("data")

                                sharedPreferences.edit().putBoolean("user_logged_in", true)
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("user_id", data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name", data.getString("name"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("email", data.getString("email")).apply()
                                sharedPreferences.edit()
                                    .putString("mobile_number", data.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("address", data.getString("address")).apply()


                                Toast.makeText(
                                    c,
                                    "Signed up as " + data.getString("name"),
                                    Toast.LENGTH_SHORT
                                ).show()

                                registrySuccess()


                            } else {
                                prog.visibility = View.INVISIBLE

                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    c,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        },
                        Response.ErrorListener {
                            prog.visibility = View.INVISIBLE

                            println("Error(register) is $it")

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

                } catch (e: JSONException) {
                    prog.visibility = View.INVISIBLE

                    Toast.makeText(
                        c,
                        "Some Error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }

        } else {
            prog.visibility = View.INVISIBLE

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)

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

    private fun errors(): Boolean {
        var errorPassCount = 0
        prog.visibility = View.INVISIBLE

        if (etPass.text.toString().length < 6 && etConpass.text.toString().length < 6) {
            etConpass.error = "Min. 6 char."
            etPass.error = "Min. 6 char."
        } else {
            errorPassCount++
        }

        if (etName.text.isBlank()) {
            etName.error = "Missing!"
        } else {
            errorPassCount++
        }

        if (etMobno.text.isBlank()) {
            etMobno.error = "Missing!"
        } else {
            errorPassCount++
        }

        if (etEmail.text.isBlank()) {
            etEmail.error = "Missing!"
        } else {
            errorPassCount++
        }

        if (etAddress.text.isBlank()) {
            etAddress.error = "Missing!"
        } else {
            errorPassCount++
        }

        if (etConpass.text.isBlank()) {
            etConpass.error = "Missing!"
        } else {
            errorPassCount++
        }

        if (etPass.text.isBlank()) {
            etPass.error = "Missing!"
        } else {
            errorPassCount++
        }

        if (etPass.text.isNotBlank() && etConpass.text.isNotBlank()) {
            if (etPass.text.toString() == etConpass.text.toString()) {
                errorPassCount++
            } else {
                etConpass.error = "Passwords don't match"
            }
        }

        return errorPassCount == 8

    }


    override fun onResume() {
        if (!ConnectionManager().checkConnectivity(activity as Context)) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("Error!")
            alterDialog.setMessage("No Internet Connection!")
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