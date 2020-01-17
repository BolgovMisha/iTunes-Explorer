package apps.mishka.testapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import apps.mishka.testapp.data.model.Album

@Database(entities = [Album::class], version = 1)
abstract class Database : RoomDatabase(){
    abstract fun getAlbumDao(): AlbumDao
    companion object {
        val name = "database"
    }
}