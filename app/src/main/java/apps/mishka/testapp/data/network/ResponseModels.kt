package apps.mishka.testapp.data.network

import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.data.model.Track
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAlbumsResponse(
    @SerializedName("resultCount")
    @Expose
    val resultCount: Int,

    @SerializedName("results")
    @Expose
    val results: List<Album>)

class GetTracksResponse(
    @SerializedName("results")
    @Expose
    val results: List<Track>)