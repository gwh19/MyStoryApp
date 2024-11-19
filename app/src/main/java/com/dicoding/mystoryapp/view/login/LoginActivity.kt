package com.dicoding.mystoryapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.mystoryapp.util.Status
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.dicoding.mystoryapp.view.main.MainActivity
import com.dicoding.mystoryapp.view.welcome.WelcomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        loginViewModel = getViewModel(this)
        setContentView(loginBinding.root)

        setAction()
    }

    private fun setAction() {

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = getString(R.string.login_actionbar)

        loginBinding.loginEmailEdit.emailMessage.observe(this) {
            loginBinding.loginEmailLayout.error = it
        }

        loginBinding.loginPassEdit.passMessage.observe(this) {
            loginBinding.loginPassLayout.error = it
        }

        loginViewModel.user.observe(this) { status ->
            when (status) {
                is Status.Loading -> showLoading(status.state)
                is Status.Failure -> showDialog(status.throwable ?: resources.getString(R.string.login_fail))
                is Status.Success -> {
                    loginViewModel.saveSession(status.data.loginResult?.token.toString())
                }
            }
        }

        loginViewModel.token.observe(this) {
            if (!it.isNullOrEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        loginBinding.loginButton.setOnClickListener {

            if (loginBinding.loginEmailEdit.text.isNullOrEmpty()) loginBinding.loginEmailLayout.error = getString(R.string.must_filled)
            if (loginBinding.loginPassEdit.text.isNullOrEmpty()) loginBinding.loginPassLayout.error = getString(R.string.must_filled)

            if (loginBinding.loginEmailLayout.error != null || loginBinding.loginPassLayout.error != null) {
                Toast.makeText(this, getString(R.string.insert_valid_data), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginViewModel.userLogin(
                loginBinding.loginEmailEdit.text.toString(),
                loginBinding.loginPassEdit.text.toString()
            )
        }
    }

    private fun getViewModel(context: Context): LoginViewModel {
        val factory = ViewModelFactory.getInstance(context)
        return ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        with(loginBinding) {
            loginLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
            loginProgressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK") {_, _ ->
                val intent = Intent(context, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
}