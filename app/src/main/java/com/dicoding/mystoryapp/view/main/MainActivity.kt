package com.dicoding.mystoryapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.mystoryapp.paging.LoadingStateAdapter
import com.dicoding.mystoryapp.util.Status
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.dicoding.mystoryapp.view.add.AddActivity
import com.dicoding.mystoryapp.view.maps.MapsActivity
import com.dicoding.mystoryapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: StoryAdapter
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mainViewModel = getViewModel(this)
        setContentView(mainBinding.root)

        setAction(savedInstanceState)
    }

    private fun setAction(savedInstanceState: Bundle?) {
        mainBinding.mainStoriesrv.layoutManager = LinearLayoutManager(this)
        adapter = StoryAdapter()
        mainBinding.mainStoriesrv.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        if (savedInstanceState === null) {
            mainViewModel.getSession()
        }

        mainViewModel.story.observe(this) { status ->
            when(status) {
                is Status.Failure -> {
                    showDialog(getString(R.string.main_fail))
                }
                is Status.Loading -> showLoading(status.state)
                is Status.Success -> {
                    status.data.observe(this) {
                        adapter.submitData(lifecycle, it)
                    }
                }
            }
        }

        mainViewModel.token.observe(this) { token ->
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
                return@observe
            }
            mainViewModel.getStory()
        }

        mainBinding.mainAddbutton.setOnClickListener {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent(this, AddActivity::class.java))
            finish()
        }
    }

    private fun getViewModel(context: Context): MainViewModel {
        val factory = ViewModelFactory.getInstance(context)
        return ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { _, _ ->
                val intent = Intent(context, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        mainBinding.mainProgressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_logout -> mainViewModel.logout()
            R.id.main_language -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.main_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}