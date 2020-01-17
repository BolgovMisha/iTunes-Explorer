package apps.mishka.testapp.ui.search

import androidx.lifecycle.*
import apps.mishka.testapp.data.Repository
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.ui.common.Event
import apps.mishka.testapp.ui.common.OnItemClickListener
import apps.mishka.testapp.ui.common.toEvent
import apps.mishka.testapp.util.ConnectionStateUtil
import kotlinx.coroutines.*

class SearchViewModel(private val repository: Repository, private val connectionStateUtil: ConnectionStateUtil) : ViewModel(), OnItemClickListener<Album>,
    SearchEditText.OnBackPressedListener {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val _results = MutableLiveData<List<Album>>()
    val results = _results as LiveData<List<Album>>

    private val _navigateToAlbumDetails = MutableLiveData<Event<Album>>()
    val navigateToAlbumDetails = _navigateToAlbumDetails as LiveData<Event<Album>>

    private val _isSearchFocused = MutableLiveData<Boolean>()
    val isSearchFocused = _isSearchFocused as LiveData<Boolean>

    private val _removeFocusFromSearchView = MutableLiveData<Event<Unit>>()
    val removeFocusFromSearchView = _removeFocusFromSearchView as LiveData<Event<Unit>>

    val searchHistory = repository.searchHistory

    private val _hideSearchResults = MutableLiveData<Event<Unit>>()
    val hideSearchResults = _hideSearchResults as LiveData<Event<Unit>>

    private val _showSearchResults = MutableLiveData<Event<Unit>>()
    val showSearchResults = _showSearchResults as LiveData<Event<Unit>>

    private val _isLoadingVisible = MutableLiveData<Boolean>()
    val isLoadingVisible = _isLoadingVisible as LiveData<Boolean>

    private val _clearSearchQuery = MutableLiveData<Event<Unit>>()
    val clearSearchQuery = _clearSearchQuery as LiveData<Event<Unit>>

    private val _isNoHistoryStubVisible = MediatorLiveData<Boolean>()
    val isNoHistoryStubVisible = _isNoHistoryStubVisible as LiveData<Boolean>

    private val _showNoInternetConnectionNotification = MutableLiveData<Event<Unit>>()
    val showNoInternetConnectionNotification = _showNoInternetConnectionNotification as LiveData<Event<Unit>>

    init {
        _isNoHistoryStubVisible.addSource(searchHistory) {
            _isNoHistoryStubVisible.value = it.isEmpty()
        }
    }

    fun onSearchQueryChanged(query: String) {
        if(showNoInternetConnectionIfNeed())
            return

        if (query.isEmpty()) {
            _hideSearchResults.value = Event(Unit)
            return
        }

        scope.launch {
            val albums = repository.findAlbums(query)
            if (albums?.isNotEmpty() == true) {
                _results.postValue(albums)
                _showSearchResults.postValue(Event(Unit))
            } else {
                _hideSearchResults.postValue(Event(Unit))
            }
        }
    }

    private var lastOnItemClickTimeMillis = System.currentTimeMillis()
    val MIN_TIME_BETWEEN_TWO_CLICKS = 500L

    override fun onItemClick(item: Album) {

        if(isItemDoubleClicked())
            return

        if(showNoInternetConnectionIfNeed())
            return

        _isLoadingVisible.value = true
        _clearSearchQuery.value = Event(Unit)
        _removeFocusFromSearchView.value = Event(Unit)
        scope.launch {
            val tracks = repository.getTracks(item)
            item.tracks = tracks
            repository.saveAlbumToSearchHistory(item)
            _navigateToAlbumDetails.postValue(item.toEvent())
            _isLoadingVisible.postValue(false)
        }
    }

    private fun isItemDoubleClicked(): Boolean {
        val previousOnItemClickMillis = lastOnItemClickTimeMillis
        val currentClickTimeMillis = System.currentTimeMillis()

        val result = currentClickTimeMillis - previousOnItemClickMillis < MIN_TIME_BETWEEN_TWO_CLICKS
        lastOnItemClickTimeMillis = currentClickTimeMillis

        return result
    }

    fun onSearchViewClick(hasFocus: Boolean) {
        if(hasFocus && showNoInternetConnectionIfNeed())
            return

        if (hasFocus)
            _removeFocusFromSearchView.value = Event(Unit)
    }

    fun onSearchViewFocusChanged(hasFocus: Boolean) {
        if(hasFocus && showNoInternetConnectionIfNeed())
            return

        _isSearchFocused.value = hasFocus
    }

    private fun showNoInternetConnectionIfNeed(): Boolean{
        if (!connectionStateUtil.isOnline()){
            _removeFocusFromSearchView.value = Event(Unit)
            _showNoInternetConnectionNotification.value = Event(Unit)
            return true
        }

        return false
    }

    fun onShadowClick() {
        _removeFocusFromSearchView.value = Event(Unit)
    }

    override fun onBackPressed() {
        _removeFocusFromSearchView.value = Event(Unit)
    }

    fun onResume() {
        _isSearchFocused.value = false
    }

    override fun onCleared() {
        scope.coroutineContext.cancel()
        super.onCleared()
    }

    class Factory(private val repository: Repository, private val connectionStateUtil: ConnectionStateUtil) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(repository, connectionStateUtil) as T
        }
    }


    companion object {
        val TAG = SearchViewModel::class.java.simpleName
    }
}