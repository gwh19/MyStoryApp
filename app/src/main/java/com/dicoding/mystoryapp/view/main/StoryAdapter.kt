package com.dicoding.mystoryapp.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.databinding.StoryListBinding
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.view.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class StoryViewHolder(private val binding: StoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.rvTitle.text = data.name
            binding.rvDesc.text = data.description
            binding.rvDate.text = data.createdAt
            Glide.with(itemView.context)
                .load(data.photoUrl ?: "https://i.stack.imgur.com/l60Hf.png")
                .into(binding.rvImage)
            itemView.setOnClickListener {
                val moveIntent = Intent(itemView.context, DetailActivity::class.java)
                moveIntent.putExtra(DetailActivity.EXTRA_ID, data.id)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.rvImage, "image"),
                        Pair(binding.rvTitle, "title"),
                        Pair(binding.rvDesc, "desc"),
                        Pair(binding.rvDate, "date")
                    )
                itemView.context.startActivity(moveIntent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}