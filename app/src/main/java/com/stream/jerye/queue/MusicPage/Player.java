package com.stream.jerye.queue.MusicPage;

import android.support.annotation.Nullable;

public interface Player {

    void play(String url);

    void pause();

    void resume();

    boolean isPlaying();

    boolean isPaused();

    @Nullable
    String getCurrentTrack();

    void release();

    void enqueue();
}
