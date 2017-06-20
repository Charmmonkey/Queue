package com.stream.jerye.queue.MusicPage;

/**
 * Created by jerye on 6/16/2017.
 */

public interface MusicQueueListener {

    void dequeue();
    void getSongProgress(int positionInMs);
    void getSongDuraction(int durationInMs);
}
