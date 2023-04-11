package com.psu.accessapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.whenCreated
import androidx.lifecycle.withCreated
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.psu.accessapplication.databinding.ActivityMainBinding
import com.psu.accessapplication.extentions.createProgressDialog
import com.psu.accessapplication.tools.CoroutineWorker
import com.rainc.coroutinecore.extension.launch
import com.rainc.coroutinecore.extension.updateUI
import com.rainc.facerecognitionmodule.repository.PersonDataSource
import com.rainc.recognitionsource.RecognitionSourceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val repository: RecognitionSourceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)

        launch(Dispatchers.Default) {
            whenCreated {
                val progressDialog = createProgressDialog()
                updateUI { progressDialog.show() }
                withContext(Dispatchers.Default) {
                    repository.logIn()
                    repository.users.first()
                    updateUI { progressDialog.dismiss() }
                    repository.users.collect { users ->
                        PersonDataSource.upload(personModels = users.mapNotNull { it.getOrNull() })
                    }
                }
            }
        }

    }

    var onActivityResultCallback: ((Int, Int, Intent?) -> Unit)? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResultCallback?.invoke(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineWorker.cancelAllJobs()
    }
}
