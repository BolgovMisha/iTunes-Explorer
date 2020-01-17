package apps.mishka.testapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import apps.mishka.testapp.data.model.Album

@Dao
interface AlbumDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(album: Album)

    @Query("SELECT * FROM album")
    fun getAll(): LiveData<List<Album>>
}