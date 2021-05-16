package com.vidya.fooddrop.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.vidya.fooddrop.R

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFragment : Fragment() {
    private lateinit var txtPersonName: TextView
    private lateinit var txtPersonNumber: TextView
    private lateinit var txtPersonEmail: TextView
    private lateinit var txtPersonAddress: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(
            getString(R.string.preference_name),
            Context.MODE_PRIVATE
        )
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        txtPersonName = view.findViewById(R.id.txtPersonName)
        txtPersonNumber = view.findViewById(R.id.txtPersonNumber)
        txtPersonEmail = view.findViewById(R.id.txtPersonEmailAdress)
        txtPersonAddress = view.findViewById(R.id.txtPersonDeliveryAddress)

        txtPersonName.text = sharedPreferences.getString("user_name", "Vidya")
        txtPersonNumber.text = sharedPreferences.getString("mobile_number", "9998886666")
        txtPersonEmail.text = sharedPreferences.getString("email", "vidyagowda@gmail.com")
        txtPersonAddress.text = sharedPreferences.getString("address", "Mysuru")

        return view
    }

}
