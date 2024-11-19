package com.dicoding.mystoryapp.view.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityAddBinding
import com.dicoding.mystoryapp.util.Status
import com.dicoding.mystoryapp.util.Utils.Companion.getImageUri
import com.dicoding.mystoryapp.util.Utils.Companion.reduceFileImage
import com.dicoding.mystoryapp.util.Utils.Companion.uriToFile
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.dicoding.mystoryapp.view.main.MainActivity
import com.dicoding.mystoryapp.view.welcome.WelcomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddActivity : AppCompatActivity() {

    private lateinit var addBinding: ActivityAddBinding
    private lateinit var addViewModel: AddViewModel

    var currentImageUri: Uri? = null
    private var myLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBinding = ActivityAddBinding.inflate(layoutInflater)
        addViewModel = getViewModel(this)
        setContentView(addBinding.root)

        setAction(savedInstanceState)
    }

    private fun setAction(savedInstanceState: Bundle?) {

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = getString(R.string.add_actionbar)

        if (savedInstanceState === null) {
            addViewModel.getSession()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        addBinding.addGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        addBinding.addCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }

        addViewModel.token.observe(this) { token ->
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
                return@observe
            }
        }

        addBinding.addLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLocation()
                return@setOnCheckedChangeListener
            }
        }

        addViewModel.response.observe(this) { status ->
            when(status) {
                is Status.Failure -> showDialog(status.throwable ?: resources.getString(R.string.add_fail))
                is Status.Loading -> showLoading(status.state)
                is Status.Success -> showDialog(status.data.message ?: resources.getString(R.string.add_success))
            }
        }

        addBinding.addButton.setOnClickListener {

            if (currentImageUri == null) {
                Toast.makeText(this, getString(R.string.add_image_alert), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.i("AddActivity", "Lat : ${myLocation?.latitude} Lon : ${myLocation?.longitude}")

            currentImageUri?.let {
                val description = addBinding.addDescEdit.text.toString()
                val imageFile = uriToFile(it, this).reduceFileImage()
                if (description.isEmpty()) {
                    Toast.makeText(this, getString(R.string.add_desc_alert), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val lat = myLocation?.latitude
                val lon = myLocation?.longitude

                addViewModel.postStories(
                    MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        imageFile.asRequestBody("image/jpeg".toMediaType())
                    ),
                    description.toRequestBody("text/plain".toMediaType()),
                    lat,
                    lon
                )
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(addBinding) {
            addLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
            addProgressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            addBinding.addImage.setImageURI(it)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) showImage()
    }

    private fun getViewModel(context: Context): AddViewModel {
        val factory = ViewModelFactory.getInstance(context)
        return ViewModelProvider(this, factory)[AddViewModel::class.java]
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            when {
                permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLocation()
                }
                permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLocation()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    myLocation = location
                    Toast.makeText(this, "Location Found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                Toast.makeText(this, "Location Not Found", Toast.LENGTH_SHORT).show()
            }
            return
        }
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK") {_, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
}
