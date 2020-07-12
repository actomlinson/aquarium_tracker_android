package com.example.aquariumtracker

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        // having the set instead of navController.graph sets every page in the
        // set as a 'home' meaning no back buttons.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_aquarium_list, R.id.nav_gallery), drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        navView.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(toolbar)

        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0,0)
        drawerToggle.syncState()
    }

    override fun onBackPressed() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val drawerToggle = ActionBarDrawerToggle(this, drawer, toolbar, 0,0)

        val fragmentManager = supportFragmentManager
        Log.i("back", toolbar.navigationContentDescription.toString())
        Log.i("fragments", supportFragmentManager.fragments.toString())
        super.onBackPressed()
//        when {
//            toolbar.navigationContentDescription.toString() == "Navigate up" -> {
//                super.onBackPressed()
//            }
//            drawer.isDrawerOpen(GravityCompat.START) -> {
//                drawer.closeDrawer(GravityCompat.START)
//            }
//            else -> {
//                drawer.openDrawer(GravityCompat.START)
//            }
//        }
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

}