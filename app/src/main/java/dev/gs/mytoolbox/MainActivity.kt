package dev.gs.mytoolbox

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dev.gs.mytoolbox.databinding.LayoutNavigationDrawerActivityBinding
import dev.gs.mytoolbox.di.utils.SharedPrefManger


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "MainActivity"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var layoutNavigationDrawerActivityBinding: LayoutNavigationDrawerActivityBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        layoutNavigationDrawerActivityBinding =
            LayoutNavigationDrawerActivityBinding.inflate(layoutInflater)
        setContentView(layoutNavigationDrawerActivityBinding.root)
        setSupportActionBar(layoutNavigationDrawerActivityBinding.appBarNavigationDrawer.toolbar)

        drawerLayout = layoutNavigationDrawerActivityBinding.layoutNavigationDrawerActivity
        val navigationView = layoutNavigationDrawerActivityBinding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_nav_host_content_navigation_drawer) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val currentStartDestinationId = if (SharedPrefManger.getPreference(
                SharedPrefManger.IS_USER_LOGGED_IN,
                false
            )
        ) R.id.nav_tasks else R.id.nav_login
        navGraph.setStartDestination(currentStartDestinationId)
        navController.graph = navGraph


        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_login, R.id.nav_tasks, R.id.nav_my_toolbox),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_login -> lockDrawer()
                else -> unlockDrawer()
            }
        }


        /*        // Check if the user is logged in
                if (SharedPrefManger.getPreference(IS_USER_LOGGED_IN, false)) {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.layoutNavigationDrawerActivity,
                        FragmentMyToolBox.newInstance(),
                        "FragmentMyToolBox"
                    ).commit()

                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layoutNavigationDrawerActivity, FragmentLogin.newInstance(), "LoginFragment")
                        .commit()
                }*/
    }

    private fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_sign_out -> {
                SharedPrefManger.putPreference(SharedPrefManger.IS_USER_LOGGED_IN, false)
                val navController =
                    findNavController(R.id.fragment_nav_host_content_navigation_drawer)
                when (navController.currentDestination?.id) {
                    R.id.nav_tasks -> {
                        navController.navigate(R.id.action_FragmentTasks_to_LoginFragment)
                        true
                    }

                    R.id.nav_my_toolbox -> {
                        navController.navigate(R.id.action_FragmentMyToolBox_to_LoginFragment)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }

            R.id.nav_exit -> {
                finishAffinity()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController: NavController =
            findNavController(R.id.fragment_nav_host_content_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.i(TAG, "onTrimMemory memory level is: $level")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}