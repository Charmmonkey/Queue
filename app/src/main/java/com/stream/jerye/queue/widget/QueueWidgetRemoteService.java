package com.stream.jerye.queue.widget;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;
import com.stream.jerye.queue.MusicPage.SimpleTrack;
import com.stream.jerye.queue.R;
import com.stream.jerye.queue.firebase.FirebaseEventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerye on 7/8/2017.
 */

public class QueueWidgetRemoteService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Widget", "RemoteViewsService instance present");
        Bundle bundle = intent.getExtras();
        int[] appWidgetIds = bundle.getIntArray("appWidgetIds");
        return new QueueRemoteViewsFactory(appWidgetIds);
    }

    private class QueueRemoteViewsFactory implements RemoteViewsFactory, FirebaseEventBus.FirebaseQueueAdapterHandler {
        private FirebaseEventBus.MusicDatabaseAccess mMusicDatabaseAccess;
        private List<SimpleTrack> mList = new ArrayList<>();
        private int[] appWidgetIdsArray;
        private String TAG = "Widget";

        @Override
        public void enqueue(SimpleTrack simpleTrack) {
            mList.add(simpleTrack);
        }

        public QueueRemoteViewsFactory(int[] appWidgetIds){
            appWidgetIdsArray = appWidgetIds;
            mMusicDatabaseAccess = new FirebaseEventBus.MusicDatabaseAccess(getApplicationContext(), this);
            mMusicDatabaseAccess.addWidgetUpdater();
        }

        @Override
        public void onCreate() {
            Log.d(TAG, "onCreate");

        }

        @Override
        public int getCount() {
            Log.d(TAG, "get count: " + mList.size());

            return mList.size();
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "DataSetChanged");
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "getViewAt: " + position);

            SimpleTrack track = mList.get(position);
            String imageUrl = track.getAlbumImage();
            String artistName = track.getArtistName();
            String trackName = track.getName();
            RemoteViews widgetItemView;

            widgetItemView = new RemoteViews(getPackageName(), R.layout.widget_list_item);

            Picasso.with(getApplicationContext()).load(imageUrl).into(widgetItemView,R.id.widget_queued_music_album_image, appWidgetIdsArray);
            widgetItemView.setTextViewText(R.id.widget_queued_music_name,trackName);
            widgetItemView.setTextViewText(R.id.widget_queued_music_artists,artistName);

            return widgetItemView;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public long getItemId(int position) {
            Log.d(TAG, "getItemId: " + position);

            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            Log.d(TAG, "getLoadingView");

            return new RemoteViews(getPackageName(), R.layout.widget_list_item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}


