package com.stream.jerye.queue.MusicPage;

import android.support.annotation.Nullable;

public interface QueuePlayer {

    void play(String url);

    void pause();

    void resume();

    void seekTo(int newPosition);

    void next();

    boolean isPlaying();

    boolean isPaused();

    @Nullable
    String getCurrentTrack();

    void release();

    void setNextTrack(String url);


}
