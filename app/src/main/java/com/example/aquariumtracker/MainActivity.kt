package com.example.aquariumtracker

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.android.volley.RequestQueue
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var volleyRequestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        // having the set instead of navController.graph sets every page in the
        // set as a 'home' meaning no back buttons.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_aquarium, R.id.nav_gallery), findViewById(R.id.drawer_layout))
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        navView.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, appBarConfiguration)

    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT)
        } else {
            drawer.openDrawer(Gravity.LEFT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    public fun getVolleyQueue(): RequestQueue {
        return volleyRequestQueue
    }
}