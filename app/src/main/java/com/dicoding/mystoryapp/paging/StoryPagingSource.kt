package com.dicoding.mystoryapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.retrofit.ApiConfig

class StoryPagingSource (private val token: String): PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = ApiConfig.getApiService().getStory("Bearer $token", position, params.loadSize)
            LoadResult.Page(
                data = responseData.listStory as List<ListStoryItem>,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}