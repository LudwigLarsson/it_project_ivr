package com.ludwiglarsson.antiplanner


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.antiplanner.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.getItemId
import com.ludwiglarsson.antiplanner.data.alarm.DeadlineManager
import com.ludwiglarsson.antiplanner.fragments.EditFragment
import com.ludwiglarsson.antiplanner.fragments.EventsFragment
import com.ludwiglarsson.antiplanner.fragments.MainFragment
import com.ludwiglarsson.antiplanner.fragments.NewFragment
import com.ludwiglarsson.antiplanner.fragments.UserFragment
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    @Inject
    lateinit var deadlineManager: DeadlineManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (this.applicationContext as App).appComponent.getMainActivityComponentFactory().create().inject(this)
        setContentView(R.layout.activity_main)

//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, MainFragment())
            .addToBackStack(null)
            .commit()
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.mainFragment -> {
                        item.isChecked = true
                        supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container_view, MainFragment())
                        .addToBackStack(null)
                        .commit()}
                    R.id.newFragment -> {
                        item.isChecked = true
                        supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container_view, NewFragment())
                        .addToBackStack(null)
                        .commit()}
                    R.id.eventsFragment -> {
                        item.isChecked = true
                        supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container_view, EventsFragment())
                        .addToBackStack(null)
                        .commit()}
                    R.id.userFragment -> {
                        item.isChecked = true
                        supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container_view, UserFragment())
                        .addToBackStack(null)
                        .commit()}
                }
                false
            })
        //navView.setupWithNavController(navController)
        handleAlarmIntent(intent)
        checkPermissions()
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleAlarmIntent(intent)
    }

    private fun checkPermissions() {
        val nonGrantedPermissions = ArrayList<String>()
        for (permission in deadlineManager.getRequiredPermissions()) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                nonGrantedPermissions.add(permission)
            }
        }
    }

    private fun handleAlarmIntent(intent: Intent?) {
        val itemId = intent?.getItemId()
        if (itemId != null) {
            val editTodo = EditFragment.createNewInstance(itemId)
            navController.navigate(R.id.editFragment);
        } else {
        }
    }

}