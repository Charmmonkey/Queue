package com.stream.jerye.queue.MusicPage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stream.jerye.queue.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerye on 6/16/2017.
 */

public class MusicQueueAdapter extends RecyclerView.Adapter<MusicQueueAdapter.MusicQueueViewHolder> {
    private List<String> mItems = new ArrayList<>();
    private Context mContext;

    public MusicQueueAdapter(Context context) {
        mContext = context;
    }

    public class MusicQueueViewHolder extends RecyclerView.ViewHolder {
        private TextView track;

        public MusicQueueViewHolder(View itemView) {
            super(itemView);

            track = (TextView) itemView.findViewById(R.id.queued_music_url);

        }
    }

    @Override
    public MusicQueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.queued_music_layout, parent, false);
        return new MusicQueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicQueueViewHolder holder, int position) {
        holder.track.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public String peek(){
        return mItems.get(0);
    }

    public void enqueue(String enqueuedMusic) {
        mItems.add(enqueuedMusic);
        notifyDataSetChanged();
    }

    public void dequeue(){
        mItems.remove(0);
        notifyItemRemoved(0);
    }

    public void remove(int position){
        mItems.remove(position);
        notifyItemRemoved(position);
    }
}
