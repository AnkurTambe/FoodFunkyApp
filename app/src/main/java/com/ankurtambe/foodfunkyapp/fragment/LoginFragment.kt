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

class LoginFragment(val c: Context) : Fragment() {

    lateinit var etMobno: EditText
    lateinit var etPass: EditText
    lateinit var tvForPass: TextView
    lateinit var btnLogin: Button
    lateinit var prog: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        etMobno = view.findViewById(R.id.login_phno)
        etPass = view.findViewById(R.id.login_pass)
        tvForPass = view.findViewById(R.id.login_forgot)
        btnLogin = view.findViewById(R.id.login_lgnbtn)
        prog = view.findViewById(R.id.log_prog)


        tvForPass.setOnClickListener(View.OnClickListener {
            openForgotFragment()
        })


        btnLogin.setOnClickListener(View.OnClickListener {

            if (etMobno.text.isBlank()) {
                etMobno.error = "Missing!"
            } else {
                if (etPass.text.isBlank()) {
                    etPass.error = "Missing!"
                } else {
                    prog.visibility = View.VISIBLE

                    logBtnClicked()
                }
            }

        })
        return view
    }


    private fun openForgotFragment() {
        val transaction = fragmentManager?.beginTransaction()

        transaction?.replace(
            R.id.start_frame,
            ForgotFragment(c)
        )
        transaction?.commit()

    }


    private fun logBtnClicked() {
        val sharedPreferences = c.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )


        if (ConnectionManager().checkConnectivity(activity as Context)) {
            try {

                val loginUser = JSONObject()

                loginUser.put("mobile_number", etMobno.text)
                loginUser.put("password", etPass.text)


                val queue = Volley.newRequestQueue(activity as Context)

                val url = "http://13.235.250.119/v2/login/fetch_result"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    loginUser,
                    Response.Listener {
                        println("Response(login) is $it")

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")


                        if (success) {

                            val data = responseJsonObjectData.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("user_logged_in", true).apply()
                            sharedPreferences.edit()
                                .putString("user_id", data.getString("user_id")).apply()
                            sharedPreferences.edit().putString("name", data.getString("name"))
                                .apply()
                            sharedPreferences.edit().putString("email", data.getString("email"))
                                .apply()
                            sharedPreferences.edit()
                                .putString("mobile_number", data.getString("mobile_number")).apply()
                            sharedPreferences.edit()
                                .putString("address", data.getString("address")).apply()

                            Toast.makeText(
                                c,
                                "Logged in as " + data.getString("name"),
                                Toast.LENGTH_SHORT
                            ).show()

                            userSuccessfullyLoggedIn()

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

                        println("Error(login) is $it")
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

    fun userSuccessfullyLoggedIn() {
        val intent = Intent(activity as Context, DashboardActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(activity as Context)) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("Error!!")
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
