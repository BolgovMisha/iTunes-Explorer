package apps.mishka.testapp.data.network

import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.data.model.Track

interface NetworkOperations {
    fun findAlbums(query: String): List<Album>?
    fun getAlbumTracks(albumId: Int): List<Track>?
}