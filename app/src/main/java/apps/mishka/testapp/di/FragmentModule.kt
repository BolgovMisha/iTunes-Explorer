package apps.mishka.testapp.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import apps.mishka.testapp.BuildConfig
import apps.mishka.testapp.R
import apps.mishka.testapp.data.Repository
import apps.mishka.testapp.data.SimpleRepository
import apps.mishka.testapp.data.database.AlbumDao
import apps.mishka.testapp.data.database.Database
import apps.mishka.testapp.data.network.NetworkOperations
import apps.mishka.testapp.data.network.SimpleNetworkOperations
import apps.mishka.testapp.ui.search.SearchViewModel
import apps.mishka.testapp.util.ConnectionStateUtil
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class FragmentModule (private val fragment: Fragment, private val context: Context){
    @Provides
    fun provideSearchViewModel(repository: Repository, connectionStateUtil: ConnectionStateUtil): SearchViewModel {
        return ViewModelProviders.of(fragment, SearchViewModel.Factory(repository, connectionStateUtil)).get(SearchViewModel::class.java)
    }

    @Provides
    fun provideRepository(repository: SimpleRepository): Repository {
        return repository
    }

    @Provides
    fun provideConnectionStateUtil(): ConnectionStateUtil {
        return ConnectionStateUtil(context)
    }

    @Provides
    fun provideNetworkOperations(networkOperations: SimpleNetworkOperations): NetworkOperations{
        return networkOperations
    }

    @Provides
    fun provideDatabase(): Database{
        return Room.databaseBuilder(context,
            Database::class.java, Database.name)
            .build()
    }

    @Provides
    fun provideAlbumDao(database: Database): AlbumDao{
        return database.getAlbumDao()
    }

    @Provides
    fun provideRetrofit(): Retrofit {
        val clientBuilder = OkHttpClient.Builder()

        if(BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(interceptor)
        }

        val baseUrl = context.resources.getString(R.string.itunes_api_url)
        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
    }
}