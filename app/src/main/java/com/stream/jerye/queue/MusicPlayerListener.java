package com.stream.jerye.queue;

import com.stream.jerye.queue.MusicPage.SimpleTrack;

/**
 * Created by jerye on 6/16/2017.
 */

public interface MusicPlayerListener {

    void queueNextSong(SimpleTrack oldTrackToRemove);
    void getSongProgress(int positionInMs);
    void getSongDuration(int durationInMs);
}
