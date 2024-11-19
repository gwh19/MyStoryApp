package com.dicoding.mystoryapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.mystoryapp.DataDummy
import com.dicoding.mystoryapp.MainDispatcherRule
import com.dicoding.mystoryapp.getOrAwaitValue
import com.dicoding.mystoryapp.repository.StoryRepositoryInterface
import com.dicoding.mystoryapp.repository.TokenRepositoryInterface
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.util.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepositoryInterface

    @Mock
    private lateinit var tokenRepository: TokenRepositoryInterface

    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        Mockito.`when`(tokenRepository.getSession()).thenReturn("your_token")
        Mockito.`when`(storyRepository.getStory("your_token")).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(storyRepository, tokenRepository)
        mainViewModel.getStory()
        val actualStory: Status<LiveData<PagingData<ListStoryItem>>> = mainViewModel.story.getOrAwaitValue()

        if (actualStory is Status.Success) {
            val pagingData: PagingData<ListStoryItem> = actualStory.data.getOrAwaitValue()
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(pagingData)

            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(dummyStory.size, differ.snapshot().size)
            Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
        } else {
            Assert.fail("Not Success")
        }
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(tokenRepository.getSession()).thenReturn("your_token")
        Mockito.`when`(storyRepository.getStory("your_token")).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(storyRepository, tokenRepository)
        mainViewModel.getStory()
        val actualStory: Status<LiveData<PagingData<ListStoryItem>>> = mainViewModel.story.getOrAwaitValue()

        if (actualStory is Status.Success) {
            val pagingData: PagingData<ListStoryItem> = actualStory.data.getOrAwaitValue()
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(pagingData)

            Assert.assertEquals(0, differ.snapshot().size)
        } else {
            Assert.fail("Not Success")
        }
    }
}

class StoryPagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}