package apps.mishka.testapp.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import apps.mishka.testapp.R
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.ui.common.ImageLoadedCallback
import apps.mishka.testapp.ui.common.OnItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_album.view.*

abstract class AlbumAdapter(private val clickListener: OnItemClickListener<Album>) :
    RecyclerView.Adapter<AlbumViewHolder>() {
    private val albums = mutableListOf<Album>()
    fun setAlbums(albums: List<Album>) {
        this.albums.clear()
        this.albums.addAll(albums.rearrange())
        notifyDataSetChanged()
    }

    //    Sort albums in accordance with recycler view this adapter belongs to
    abstract fun List<Album>.rearrange(): List<Album>

    abstract val animateImageAppearance: Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view, clickListener)
    }

    override fun getItemCount() = albums.size

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.setAlbum(albums[position], animateImageAppearance)
    }
}

class SearchResultAlbumAdapter(clickListener: OnItemClickListener<Album>) : AlbumAdapter(clickListener) {
    override fun List<Album>.rearrange(): List<Album> {
        return sortedBy { it.name }
    }

    override val animateImageAppearance = true
}

class SearchHistoryAlbumAdapter(clickListener: OnItemClickListener<Album>) : AlbumAdapter(clickListener) {
    override fun List<Album>.rearrange(): List<Album> {
        return reversed()
    }
    override val animateImageAppearance = false
}

class AlbumViewHolder(view: View, private val clickListener: OnItemClickListener<Album>) :
    RecyclerView.ViewHolder(view) {
    fun setAlbum(album: Album, animateImageAppearance: Boolean) {
        itemView.textViewAlbumName.text = album.name
        itemView.textViewArtistName.text = album.artistName

        if (animateImageAppearance)
            Picasso.get().load(album.coverUrl).into(
                itemView.imageViewAlbumCover,
                ImageLoadedCallback(itemView.imageViewAlbumCover)
            )
        else
            Picasso.get().load(album.coverUrl).into(itemView.imageViewAlbumCover)

        itemView.setOnClickListener { clickListener.onItemClick(album) }
    }
}