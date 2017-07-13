package com.stream.jerye.queue.musicPage;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by jerye on 6/14/2017.
 */

public class SimpleTrack {
    private String trackUrl;
    private int vote;
    private String name;
    private String artistName;
    private transient String key;
    private String albumImage;
    private long durationInMS;

    public SimpleTrack() {
    }

    public SimpleTrack(Track track) {
        this.trackUrl = track.uri;
        this.name = track.name;

        String artists = "";
        for (ArtistSimple artistSimple : track.artists) {
            artists = artists + artistSimple.name + ", ";
        }
        this.artistName = artists.substring(0, artists.length() - 2);
        this.albumImage = track.album.images.get(0).url;
        this.durationInMS = track.duration_ms;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTrack() {
        return trackUrl;
    }

    public void setTrack(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public long getDurationInMS() {
        return durationInMS;
    }

    public void setDurationInMS(long durationInMS) {
        this.durationInMS = durationInMS;
    }
}
