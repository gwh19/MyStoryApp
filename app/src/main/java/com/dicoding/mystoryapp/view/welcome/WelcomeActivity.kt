package com.dicoding.mystoryapp.view.welcome

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityWelcomeBinding
import com.dicoding.mystoryapp.view.login.LoginActivity
import com.dicoding.mystoryapp.view.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var welcomeBinding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        welcomeBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(welcomeBinding.root)

        setAction()
    }

    private fun setAction() {

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        welcomeBinding.welcomeLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        welcomeBinding.welcomeRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_welcome, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.welcome_language -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        return super.onOptionsItemSelected(item)
    }
}