package apps.mishka.testapp.data

import androidx.lifecycle.LiveData
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.data.model.Track

interface Repository {
    val searchHistory: LiveData<List<Album>>
    suspend fun findAlbums(query: String): List<Album>?
    suspend fun getTracks(album: Album): List<Track>?
    suspend fun saveAlbumToSearchHistory(album: Album)
}