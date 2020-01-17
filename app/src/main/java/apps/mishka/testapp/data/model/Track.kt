package apps.mishka.testapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Track(
    @SerializedName("wrapperType")
    @Expose
    val type: String,

    @SerializedName("trackName")
    @Expose
    val trackName: String,

    @SerializedName("trackNumber")
    @Expose
    val trackNumber: Int,

    @SerializedName("trackTimeMillis")
    @Expose
    val trackTimeMillis: Int
): Serializable