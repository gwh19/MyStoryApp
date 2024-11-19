package com.dicoding.mystoryapp.response

import com.google.gson.annotations.SerializedName

data class StoryDetailResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: ListStoryItem? = null
)
