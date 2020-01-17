package apps.mishka.testapp.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import apps.mishka.mindsandbox.TrackAdapter

import apps.mishka.testapp.R
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.ui.MainActivity
import apps.mishka.testapp.ui.common.CommonDividerItemDecoration
import apps.mishka.testapp.ui.common.ImageLoadedCallback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_album_details.view.*

class AlbumDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album_details, container, false)

        val album = arguments?.getSerializable(Album.TAG) as Album?
        if (album != null)
            setAlbum(view, album)

        initToolbar(view)

        return view
    }

    //    Set fragment's toolbar as activity's action bar
//    Enable back navigation button
    private fun initToolbar(view: View) {
        val activity = activity as MainActivity? ?: return

        activity.setSupportActionBar(view.toolbar)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        view.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        view.toolbar.navigationIcon = resources.getDrawable(R.drawable.icon_back)
    }

    private fun setAlbum(view: View, album: Album) {
        Picasso.get().load(album.coverUrl).into(view.imageViewAlbumCover, ImageLoadedCallback(view.imageViewAlbumCover))

        view.toolbar.title = album.name

        val layoutManager = LinearLayoutManager(context)
        view.recyclerViewTracks.layoutManager = layoutManager
        view.recyclerViewTracks.adapter = TrackAdapter(album.tracks ?: listOf())
        view.recyclerViewTracks.addItemDecoration(CommonDividerItemDecoration.getInstance(view.context))

        val trackCountPlural = resources.getQuantityString(R.plurals.song_plurals, album.trackCount)

        view.textViewAlbumInfo.text =
            resources.getString(
                R.string.album_info,
                album.artistName.cropIfTooLong(),
                album.trackCount,
                trackCountPlural
            )
    }

    private fun String.cropIfTooLong(): String {
        val MAX_LENGTH = 40
        if (length > MAX_LENGTH)
            return substring(0, 40) + "…"
        else return this
    }


}
