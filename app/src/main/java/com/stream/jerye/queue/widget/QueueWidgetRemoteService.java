//package com.stream.jerye.queue.widget;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.RemoteViews;
//import android.widget.RemoteViewsService;
//
//import com.squareup.picasso.Picasso;
//import com.stream.jerye.queue.MusicPage.SimpleTrack;
//import com.stream.jerye.queue.R;
//import com.stream.jerye.queue.Utility;
//import com.stream.jerye.queue.firebase.FirebaseEventBus;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by jerye on 7/8/2017.
// */
//
//public class QueueWidgetRemoteService extends RemoteViewsService {
//
//
//    @Override
//    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        Bundle bundle = intent.getExtras();
//        ArrayList<Integer> appWidgetIds = bundle.getIntegerArrayList("appWidgetIds");
//        return new QueueRemoteViewsFactory(appWidgetIds);
//    }
//
//    private class QueueRemoteViewsFactory implements RemoteViewsFactory,FirebaseEventBus.FirebaseQueueAdapterHandler {
//        private FirebaseEventBus.MusicDatabaseAccess mMusicDatabaseAccess;
//        private List<SimpleTrack> mList = new ArrayList<>();
//        private int[] appWidgetIdsArray;
//
//        @Override
//        public void enqueue(SimpleTrack simpleTrack) {
//            mList.add(simpleTrack);
//            onDataSetChanged();
//        }
//
//        public QueueRemoteViewsFactory(ArrayList<Integer> appWidgetIds){
//            appWidgetIdsArray = Utility.convertIntegers(appWidgetIds);
//            mMusicDatabaseAccess = new FirebaseEventBus.MusicDatabaseAccess(getApplicationContext(),this);
//        }
//
//        @Override
//        public void onCreate() {
//
//        }
//
//        @Override
//        public int getCount() {
//            return mList.size();
//        }
//
//        @Override
//        public void onDataSetChanged() {
//
//        }
//
//        @Override
//        public void onDestroy() {
//
//        }
//
//        @Override
//        public RemoteViews getViewAt(int position) {
//
//            SimpleTrack track = mList.get(position);
//            String imageUrl = track.getAlbumImage();
//            String artistName = track.getArtistName();
//            String trackName = track.getName();
//            RemoteViews widgetItemView;
//
//            widgetItemView = new RemoteViews(getPackageName(), R.layout.widget_list_item);
//
//            Picasso.with(getApplicationContext()).load(imageUrl).into(widgetItemView,R.id.widget_queued_music_album_image, appWidgetIdsArray);
//            widgetItemView.setTextViewText(R.id.widget_queued_music_name,trackName);
//            widgetItemView.setTextViewText(R.id.widget_queued_music_artists,artistName);
//
//            return widgetItemView;
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            return 3;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public RemoteViews getLoadingView() {
//            return new RemoteViews(getPackageName(), R.layout.widget_list_item);
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//    }
//}
//
//
