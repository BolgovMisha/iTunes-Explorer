package apps.mishka.mindsandbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import apps.mishka.testapp.R
import apps.mishka.testapp.data.model.Track
import kotlinx.android.synthetic.main.item_track.view.*
import kotlin.random.Random

class TrackAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.setTrack(tracks[position])
    }
}

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun setTrack(track: Track) {
        itemView.textViewTrackName.text = track.trackName
        itemView.textViewTrackNumber.text = track.trackNumber.toString()

        val totalDurationSeconds = track.trackTimeMillis/1000
        val durationMinutes = totalDurationSeconds/60
        val durationSeconds = totalDurationSeconds%60

        itemView.textViewTrackDuration.text = String.format("%d:%02d", durationMinutes, durationSeconds)
    }
}