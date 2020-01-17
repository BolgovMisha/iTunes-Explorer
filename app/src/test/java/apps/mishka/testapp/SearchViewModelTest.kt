package apps.mishka.testapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import apps.mishka.mindsandbox.TrackViewHolder
import apps.mishka.testapp.data.Repository
import apps.mishka.testapp.data.SimpleRepository
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.data.model.Track
import apps.mishka.testapp.ui.search.SearchViewModel
import apps.mishka.testapp.util.ConnectionStateUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest : BaseTest() {

    lateinit var viewModel: SearchViewModel
    lateinit var repository: Repository
    lateinit var connectionStateUtil: ConnectionStateUtil

    @Before
    fun init() {
        repository = mockk()
        every { repository.searchHistory } returns MutableLiveData()
        connectionStateUtil = mockk()
        viewModel = SearchViewModel(repository, connectionStateUtil)
    }

    val VALID_SEARCH_QUERY = "Some query"
    val EMPTY_SEARCH_QUERY = ""

    @Test
    fun `search request should be aborted if internet connection absent`() {
        every { connectionStateUtil.isOnline() } returns false
        every { runBlocking { repository.findAlbums(any()) } } returns listOf()

        viewModel.onSearchQueryChanged(VALID_SEARCH_QUERY)
        waitForAsyncCodeExecuted()

        verify(inverse = true) { runBlocking { repository.findAlbums(any()) } }
    }

    @Test
    fun `search request should be aborted if query is empty`() {
        every { connectionStateUtil.isOnline() } returns true
        every { runBlocking { repository.findAlbums(any()) } } returns listOf()

        viewModel.onSearchQueryChanged(EMPTY_SEARCH_QUERY)
        waitForAsyncCodeExecuted()

        verify(inverse = true) { runBlocking { repository.findAlbums(any()) } }
    }

    @Test
    fun `on successful search request should show results if it's not empty`() {
        every { connectionStateUtil.isOnline() } returns true
        every { runBlocking { repository.findAlbums(any()) } } returns listOf(mockk())

        viewModel.onSearchQueryChanged(VALID_SEARCH_QUERY)
        waitForAsyncCodeExecuted()

        assertNotNull(viewModel.results.value)
        assertNotNull(viewModel.showSearchResults.value)
    }

    @Test
    fun `on successful search request should hide results if it's empty`() {
        every { connectionStateUtil.isOnline() } returns true
        every { runBlocking { repository.findAlbums(any()) } } returns listOf()

        viewModel.onSearchQueryChanged(VALID_SEARCH_QUERY)
        waitForAsyncCodeExecuted()

        assertNotNull(viewModel.hideSearchResults.value)
    }

    @Test
    fun `on item double click should be aborted`() {
        every { connectionStateUtil.isOnline() } returns true
        val album = mockk<Album>()
        every{album.tracks = any()} returns Unit
        every { runBlocking { repository.getTracks(any()) } } returns listOf()
        every { runBlocking { repository.saveAlbumToSearchHistory(any()) } } returns Unit
        waitTimeBeforeClickOnItem()


        viewModel.onItemClick(album)
        viewModel.onItemClick(album)

        verify(exactly = 1) { runBlocking { repository.getTracks(any()) } }
    }

    @Test
    fun `on item click should be aborted if internet connection absents`() {
        every { connectionStateUtil.isOnline() } returns false
        every { runBlocking { repository.getTracks(any()) } } returns listOf()
        waitTimeBeforeClickOnItem()

        viewModel.onItemClick(mockk())

        waitForAsyncCodeExecuted()
        verify(inverse = true) { runBlocking { repository.getTracks(any()) } }
    }

    @Test
    fun `on item click should save tracks in search history`() {
        every { connectionStateUtil.isOnline() } returns true
        val fetchedTracks = listOf<Track>(mockk(), mockk())
        val album = mockk<Album>()
        every{album.tracks = any()} returns Unit
        every { runBlocking { repository.getTracks(any()) } } returns fetchedTracks
        every { runBlocking { repository.saveAlbumToSearchHistory(any()) } } returns Unit
        waitTimeBeforeClickOnItem()

        viewModel.onItemClick(album)

        waitForAsyncCodeExecuted()
        verify(exactly = 1) { runBlocking { repository.saveAlbumToSearchHistory(any()) } }
    }

    @Test
    fun `on item click should navigate to album details`() {
        every { connectionStateUtil.isOnline() } returns true
        val fetchedTracks = listOf<Track>(mockk(), mockk())
        val album = mockk<Album>()
        every{album.tracks = any()} returns Unit
        every { runBlocking { repository.getTracks(any()) } } returns fetchedTracks
        every { runBlocking { repository.saveAlbumToSearchHistory(any()) } } returns Unit
        waitTimeBeforeClickOnItem()

        viewModel.onItemClick(album)

        waitForAsyncCodeExecuted()
        assertNotNull(viewModel.navigateToAlbumDetails)
    }

    @Test
    fun `on search view click should do nothing if focus lost`(){
        every { connectionStateUtil.isOnline() } returns true

        viewModel.onSearchViewClick(false)

        assertNull(viewModel.removeFocusFromSearchView.value)
    }

    @Test
    fun `on search view click should remove focus if focused`(){
        every { connectionStateUtil.isOnline() } returns true

        viewModel.onSearchViewClick(true)

        assertNotNull(viewModel.removeFocusFromSearchView.value)
    }


    @Test
    fun `on search view focus changed should be aborted if internet connection absents`(){
        every { connectionStateUtil.isOnline() } returns false

        viewModel.onSearchViewClick(true)

        assertNull(viewModel.isSearchFocused.value)
    }

    @Test
    fun `on search view focus changed should set live data`(){
        every { connectionStateUtil.isOnline() } returns true

        viewModel.onSearchViewFocusChanged(true)

        assert(viewModel.isSearchFocused.value == true)
    }

    @Test
    fun `on shadow click should remove focus`(){
        viewModel.onShadowClick()

        assertNotNull(viewModel.removeFocusFromSearchView)
    }

    @Test
    fun `on back pressed should remove focus`(){
        viewModel.onBackPressed()

        assertNotNull(viewModel.removeFocusFromSearchView)
    }


    @Test
    fun `on resume should remove focus`(){
        viewModel.onResume()

        assertNotNull(viewModel.removeFocusFromSearchView)
    }

    private fun waitTimeBeforeClickOnItem() = runBlocking { delay(viewModel.MIN_TIME_BETWEEN_TWO_CLICKS) }
}