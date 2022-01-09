package com.example.employeeapp2

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "MainActivity"

fun createFile(context: Context, folder: String, ext: String): File {
    val timeStamp: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    //getFilesDir() is for internal storage
    val filesDir: File? = context.getExternalFilesDir(folder)
    val newFile = File(filesDir, "$timeStamp.$ext")
    newFile.createNewFile()
    return newFile
}

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runWorker()

        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setIcon(R.mipmap.ic_launcher)

//        setSupportActionBar(toolbar)
//        supportActionBar?.title = ""

//        val navController = findNavController(R.id.nav_host_fragment)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun runWorker() {
        val work = OneTimeWorkRequest.Builder(EmployeeOfTheDayWorker::class.java).build()
        WorkManager.getInstance(this).enqueue(work)
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(this, "Landscape", Toast.LENGTH_SHORT).show()
//            Log.d(TAG, "hello")
//        }
//        else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "Portrait", Toast.LENGTH_SHORT).show()
//            Log.d(TAG, "hello2")
//        }
//
//    }
}