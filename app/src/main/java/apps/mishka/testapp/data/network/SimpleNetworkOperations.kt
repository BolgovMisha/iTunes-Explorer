package apps.mishka.testapp.data.network

import android.util.Log
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.data.model.Track
import retrofit2.Retrofit
import java.util.*
import javax.inject.Inject

class SimpleNetworkOperations @Inject constructor(retrofit: Retrofit) : NetworkOperations {
    private val COUNTRY_CODE = "us"
    private val CONTENT_TYPE= "music"
    private val ENTITY_TYPE_ALBUM= "album"
    private val ENTITY_TYPE_SONG= "song"

    private val iTunesApi = retrofit.create(ITunesApi::class.java)

    override fun findAlbums(query: String): List<Album>? {
        val response = iTunesApi.getAlbums(query, COUNTRY_CODE, CONTENT_TYPE, ENTITY_TYPE_ALBUM).execute()
        return response.body()?.results
    }

    override fun getAlbumTracks(albumId: Int): List<Track>? {
        val response = iTunesApi.getTracks(albumId.toString(), ENTITY_TYPE_SONG).execute()
        return response.body()?.results
    }
}