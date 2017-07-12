package com.stream.jerye.queue.lobby;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by jerye on 7/1/2017.
 */

@IgnoreExtraProperties
public class Room {
    private String title;
    private String password;
    private transient String roomKey;

    public Room() {
    }

    public Room(String title, String password) {
        this.title = title;
        this.password = password;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String title) {
        this.password = password;
    }


}
