package com.dicoding.mystoryapp.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityDetailBinding
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.util.Status
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.dicoding.mystoryapp.view.main.MainActivity
import com.dicoding.mystoryapp.view.welcome.WelcomeActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        detailViewModel = getViewModel(this)
        setContentView(detailBinding.root)

        setAction(savedInstanceState)
    }

    private fun setAction(savedInstanceState: Bundle?) {

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = "Detail"

        val id = intent.getStringExtra(EXTRA_ID) ?: return

        if (savedInstanceState === null) {
            detailViewModel.getSession()
        }

        detailViewModel.storyDetail.observe(this) { status ->
            when(status) {
                is Status.Loading -> showLoading(status.state)
                is Status.Failure -> {
                    if (!isFinishing) {
                        showDialog(status.throwable ?: resources.getString(R.string.detail_fail))
                    }
                }
                is Status.Success -> {
                    status.data.story?.let { setStoryDetail(it) }
                }
            }
        }

        detailViewModel.token.observe(this) { token ->
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
                return@observe
            }
            detailViewModel.getDetailStory(id)
        }
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

    private fun showLoading(isLoading: Boolean) {
        detailBinding.detailProgressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setStoryDetail(listStory: ListStoryItem) {
        with(detailBinding) {
            detailTitle.text = listStory.name
            detailCreated.text = listStory.createdAt
            detailDesc.text = listStory.description
        }
        Glide.with(this)
            .load(listStory.photoUrl ?: "https://i.stack.imgur.com/l60Hf.png")
            .into(detailBinding.detailImage)
        return
    }

    private fun getViewModel(context: Context): DetailViewModel {
        val factory = ViewModelFactory.getInstance(context)
        return ViewModelProvider(this, factory)[DetailViewModel::class.java]
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}