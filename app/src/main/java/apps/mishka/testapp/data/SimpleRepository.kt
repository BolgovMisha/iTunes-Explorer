package apps.mishka.testapp.data

import androidx.lifecycle.LiveData
import apps.mishka.testapp.data.database.AlbumDao
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.data.model.Track
import apps.mishka.testapp.data.network.NetworkOperations
import javax.inject.Inject

class SimpleRepository @Inject constructor(
    private val networkOperations: NetworkOperations,
    private val albumDao: AlbumDao
) : Repository {

    override val searchHistory: LiveData<List<Album>>
        get() = albumDao.getAll()

    override suspend fun findAlbums(query: String): List<Album>? {
        return networkOperations.findAlbums(query)
    }

    override suspend fun getTracks(album: Album): List<Track>? {
        return networkOperations.getAlbumTracks(album.collectionId).onlyTracks()
    }

//    Returns only items with proper wrapper type
    private fun List<Track>?.onlyTracks(): List<Track>? {
        val TRACK_WRAPPER_TYPE= "track"
        return this?.filter { it.type == TRACK_WRAPPER_TYPE }
    }

    override suspend fun saveAlbumToSearchHistory(album: Album) {
        albumDao.insert(album)
    }
}
