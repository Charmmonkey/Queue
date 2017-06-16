package com.stream.jerye.queue.MusicPage;

/**
 * Created by jerye on 6/14/2017.
 */

public class SpotifyTrack {
    private String trackUrl;

    public SpotifyTrack(String trackUrl){
        this.trackUrl = trackUrl;

    }

    public String getTrack() {
        return trackUrl;
    }

    public void setTrack(String trackUrl) {
        this.trackUrl = trackUrl;
    }
}
