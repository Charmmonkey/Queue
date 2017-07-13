package com.stream.jerye.queue;

import com.stream.jerye.queue.musicPage.SimpleTrack;

public interface QueuePlayer {

    void play();

    void pause();

    void resume();

    void seekTo(int newPosition);

    void next();

    boolean isPlaying();

    boolean isPaused();

    void release();

    void addTrack(SimpleTrack newTrack);


}
