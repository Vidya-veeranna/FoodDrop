package com.vidya.fooddrop.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.vidya.fooddrop.*
import com.vidya.fooddrop.fragment.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinationLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = "0"
    private var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val convertView =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.drawer_header, null)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("used_id", "0")
        sharedPreferences.edit().putString("users_id", userId).apply()
        setContentView(R.layout.activity_main)

        val userName: TextView = convertView.findViewById(R.id.txtpersonName)
        val userMobileNumber: TextView = convertView.findViewById(R.id.txtpersonPhno)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinationLayout = findViewById(R.id.coordinationLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigation)

        userName.text = sharedPreferences.getString("user_name", "Vidya")
        userMobileNumber.text = "+91-${sharedPreferences.getString("mobile_number", "6663332222")}"

        navigationView.addHeaderView(convertView)

        setUpToolbar()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isChecked = true
            it.isCheckable = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    openHome()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            MyProfileFragment()
                        )
                        .commit()

                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favouriteRestaurants -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouriteRestaurantsFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Favourite restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            OrderHistoryFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FAQsFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Frequently Asked Question"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Log Out")
                    dialog.setMessage("Do you want to log out")
                    dialog.setPositiveButton("Yes") { _, _ ->
                        startActivity(Intent(this@MainActivity, LogInActivity::class.java))
                        ActivityCompat.finishAffinity(this@MainActivity)
                        sharedPreferences.edit().clear().apply()
                    }
                    dialog.setNegativeButton("No") { _, _ ->

                    }
                    dialog.create()
                    dialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Tool Bar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openHome() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = HomeFragment()

        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()

        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.home)
        drawerLayout.closeDrawers()
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }
}
