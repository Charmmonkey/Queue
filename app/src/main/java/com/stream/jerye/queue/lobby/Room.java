package com.stream.jerye.queue.lobby;

/**
 * Created by jerye on 7/1/2017.
 */

public class Room {
    private String title;
    private String password;

    public Room() {
    }

    public Room(String title, String password) {
        this.title = title;
        this.password = password;
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
