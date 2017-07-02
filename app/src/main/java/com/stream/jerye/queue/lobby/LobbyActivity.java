package com.stream.jerye.queue.lobby;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.stream.jerye.queue.R;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity);


    }

    public void createRoom(View v){
        new CreateRoomDiaglog().show(getFragmentManager(),"CreateRoomDialog");
    }

    public void joinRoom(View v){
        new JoinRoomDialog().show(getFragmentManager(), "JoinRoomDialog");
    }
}
