package com.ludwiglarsson.antiplanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.antiplanner.R
import com.example.antiplanner.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ludwiglarsson.antiplanner.fragments.MainFragmentDirections

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)

        /*binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.getItemId()) {
                R.id.add -> {
                    navController.navigate(MainFragmentDirections.actionMainFragmentToNewFragment())
                }
                R.id.events -> {
                    navController.navigate(MainFragmentDirections.actionMainFragmentToEventsFragment())
                }

                R.id.user -> {
                    navController.navigate(MainFragmentDirections.actionMainFragmentToUserFragment())
                }
            }
            false
        }*/
    }
}