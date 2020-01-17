package apps.mishka.testapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
class Album(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @SerializedName("collectionId")
    @Expose
    val collectionId: Int,

    @SerializedName("artistName")
    @Expose
    val artistName: String,

    @SerializedName("trackCount")
    @Expose
    val trackCount: Int,

    @SerializedName("collectionName")
    @Expose
    val name: String,

    @SerializedName("artworkUrl100")
    @Expose
    val coverUrl: String
): Serializable{
    @Ignore
    var tracks: List<Track>? = null

    companion object {
        val TAG: String = Album::class.java.simpleName
    }
}