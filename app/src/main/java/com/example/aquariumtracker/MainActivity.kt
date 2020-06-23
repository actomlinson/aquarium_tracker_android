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
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph, findViewById(R.id.drawer_layout))

        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
        findViewById<Toolbar>(R.id.toolbar).setupWithNavController(navController, appBarConfiguration)

        }

//        val queue = Volley.newRequestQueue(this)
//        val url = "***"
//        val textView = findViewById<TextView>(R.id.textview_main)
//        val stringRequest = StringRequest(
//                Request.Method.GET,
//                url,
//                Response.Listener<String> { response ->
//                    // Display the first 500 characters of the response string.
//                    textView.text = "Response is: ${response}" //.substring(0, 500)}"
//                    Log.i("response listener", "error")
//                },
//                Response.ErrorListener { error ->
//                    textView.text = error.toString() //"That didn't work!"
//                    Log.i("error listener", error.toString())
//                })
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//        Log.i("main", "added to queue")

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
}