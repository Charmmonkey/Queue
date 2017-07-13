package com.stream.jerye.queue.musicPage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stream.jerye.queue.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerye on 6/16/2017.
 */

public class MusicQueueAdapter extends RecyclerView.Adapter<MusicQueueAdapter.MusicQueueViewHolder> {
    private List<SimpleTrack> mItems = new ArrayList<>();
    private Context mContext;
    private String singleTitle, singleArtist, singleAlbumImage;
    private int noAlbumImageId = R.drawable.ic_pause;
    private String TAG = "MusicQueueAdapter";

    public MusicQueueAdapter(Context context) {
        mContext = context;
    }

    public class MusicQueueViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView artist;
        private ImageView albumImage;

        public MusicQueueViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.queued_music_name);
            artist = (TextView) itemView.findViewById(R.id.queued_music_artists);
            albumImage = (ImageView) itemView.findViewById(R.id.queued_music_album_image);
        }
    }

    @Override
    public MusicQueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.queued_music_layout, parent, false);
        return new MusicQueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicQueueViewHolder holder, int position) {

        singleTitle = mItems.get(position).getName() != null ? mItems.get(position).getName() : "Unknown";
        singleArtist = mItems.get(position).getArtistName() != null ? mItems.get(position).getArtistName() : "Unknown";
        singleAlbumImage = mItems.get(position).getAlbumImage() != null ? mItems.get(position).getAlbumImage() : "";


        holder.title.setText(singleTitle);
        holder.artist.setText(singleArtist);

        if (singleAlbumImage.equals("")) {
            Picasso.with(mContext).load(R.drawable.ic_pause).into(holder.albumImage);

        } else {
            Picasso.with(mContext).load(singleAlbumImage).into(holder.albumImage);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public void enqueue(SimpleTrack enqueuedMusic) {
        mItems.add(enqueuedMusic);
        Log.d(TAG, "Music added");
        notifyDataSetChanged();
    }

    public void dequeue() {
        mItems.remove(0);
        notifyItemRemoved(0);
    }

    public void remove(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }
}
