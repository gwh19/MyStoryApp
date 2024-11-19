package com.dicoding.mystoryapp

import com.dicoding.mystoryapp.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 .. 100) {
            val story = ListStoryItem(
                i.toString(),
                "https://i.stack.imgur.com/l60Hf.png",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}