package dev.gs.mytoolbox


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dev.gs.mytoolbox.databinding.LayoutNavigationDrawerActivityBinding
import dev.gs.mytoolbox.di.utils.SharedPrefManger


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "MainActivity"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var layoutNavigationDrawerActivityBinding: LayoutNavigationDrawerActivityBinding
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragment_nav_host_content_navigation_drawer) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutNavigationDrawerActivityBinding =
            LayoutNavigationDrawerActivityBinding.inflate(layoutInflater)
        setContentView(layoutNavigationDrawerActivityBinding.root)
        setSupportActionBar(layoutNavigationDrawerActivityBinding.appBarNavigationDrawer.toolbar)

        val navigationView = layoutNavigationDrawerActivityBinding.navView

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_tasks -> {
                    Log.d(javaClass.simpleName, "Nav view item click - NavTasks")
                    navController.navigate(R.id.nav_tasks)
                }

                R.id.nav_my_toolbox -> {
                    Log.d(javaClass.simpleName, "Nav View item click - Nav My ToolBox")
                    navController.navigate(R.id.nav_my_toolbox)
                }

                else -> {
                    Log.d(javaClass.simpleName, "Something is wrong")
                }
            }
            layoutNavigationDrawerActivityBinding.layoutNavigationDrawerActivity.close()
            true
        }

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        //FirebaseAuth.getInstance().currentUser?.let {
        SharedPrefManger.getPreference(SharedPrefManger.IS_SIGN_IN, false).takeIf { it }?.let {
            navGraph.setStartDestination(R.id.nav_my_toolbox)
        } ?: navGraph.setStartDestination(R.id.nav_login)
        navController.graph = navGraph

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_login, R.id.nav_tasks, R.id.nav_my_toolbox),
            layoutNavigationDrawerActivityBinding.layoutNavigationDrawerActivity
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_setting -> {
                Toast.makeText(this, "TODO: please implement me", Toast.LENGTH_LONG).show()
                Log.w(javaClass.simpleName, "Not Implemented yet")
                true
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                SharedPrefManger.putPreference(SharedPrefManger.IS_SIGN_IN, false)
                //TODO: update when new features are used
                navController.navigate(R.id.action_to_LoginFragment)
                true
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
        return if (navController.currentDestination?.id == R.id.nav_login) {
            true // Disable click for login screen
        } else {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG, "onTrimMemory memory level is: $level")
    }


}