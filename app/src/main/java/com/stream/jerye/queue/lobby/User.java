package com.stream.jerye.queue.lobby;

/**
 * Created by jerye on 7/8/2017.
 */

public class User {
    private String spotifyProfileId;
    private String registrationToken;
    private String name;

    public User() {
    }

    public User(String name, String spotifyProfileId, String registrationToken) {
        this.spotifyProfileId = spotifyProfileId;
        this.registrationToken = registrationToken;
        this.name = name;
    }

    public String getSpotifyProfileId() {
        return spotifyProfileId;
    }

    public void setSpotifyProfileId(String spotifyProfileId) {
        this.spotifyProfileId = spotifyProfileId;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String title) {
        this.registrationToken = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
