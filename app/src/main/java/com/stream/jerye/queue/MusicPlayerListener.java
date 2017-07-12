package com.stream.jerye.queue;

/**
 * Created by jerye on 6/16/2017.
 */

public interface MusicPlayerListener {

//    void queueNextSong(SimpleTrack oldTrackToRemove);
    void getSongProgress(int positionInMs);
    void getSongDuration(int durationInMs);
}
