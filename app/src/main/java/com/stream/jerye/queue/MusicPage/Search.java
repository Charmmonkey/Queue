package com.stream.jerye.queue.MusicPage;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class Search {

    public interface View {
        void reset();

        void addData(List<Track> items);

        void onTrackSelected(SimpleTrack simpleTrack);
    }

    public interface ActionListener {

        void init(String token);

        String getCurrentQuery();

        void search(String searchQuery);

        void loadMoreResults();

        void selectTrack(Track item);

        void resume();

        void pause();

        void destroy();

        void refreshToken(String token);

    }
}
