package apps.mishka.testapp.data.network

import apps.mishka.testapp.data.model.Album
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search")
    fun getAlbums(@Query("term") term: String,
                  @Query("country") country: String,
                  @Query("media") type: String,
                  @Query("entity") entityType: String ): Call<GetAlbumsResponse>

    @GET("lookup")
    fun getTracks(@Query("id") id: String,
                  @Query("entity") entity: String): Call<GetTracksResponse>

}