package com.dicoding.mystoryapp.view.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityRegisterBinding
import com.dicoding.mystoryapp.util.Status
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.dicoding.mystoryapp.view.welcome.WelcomeActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        registerViewModel = getViewModel(this)
        setContentView(registerBinding.root)

        setAction()
    }

    private fun setAction() {

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = getString(R.string.register_actionbar)

        registerBinding.registerEmailEdit.emailMessage.observe(this) {
            registerBinding.registerEmailLayout.error = it
        }

        registerBinding.registerPassEdit.passMessage.observe(this) {
            registerBinding.registerPassLayout.error = it
        }

        registerViewModel.response.observe(this) { status ->
            when(status) {
                is Status.Failure -> showDialog(status.throwable ?: resources.getString(R.string.register_fail))
                is Status.Loading -> showLoading(status.state)
                is Status.Success -> showDialog(status.data.message ?: resources.getString(R.string.register_success))
            }
        }

        registerBinding.registerButton.setOnClickListener {

            if (registerBinding.registerNameEdit.text.isNullOrEmpty()) registerBinding.registerNameLayout.error = getString(R.string.must_filled)
            if (registerBinding.registerEmailEdit.text.isNullOrEmpty()) registerBinding.registerEmailLayout.error = getString(R.string.must_filled)
            if (registerBinding.registerPassEdit.text.isNullOrEmpty()) registerBinding.registerPassLayout.error = getString(R.string.must_filled)

            if (registerBinding.registerEmailLayout.error != null || registerBinding.registerPassLayout.error != null) {
                Toast.makeText(this, getString(R.string.insert_valid_data), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerViewModel.userRegister(
                registerBinding.registerNameEdit.text.toString(),
                registerBinding.registerEmailEdit.text.toString(),
                registerBinding.registerPassEdit.text.toString()
            )
        }
    }

    private fun getViewModel(context: Context): RegisterViewModel {
        val factory = ViewModelFactory.getInstance(context)
        return ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        with(registerBinding) {
            registerLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
            registerProgressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
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