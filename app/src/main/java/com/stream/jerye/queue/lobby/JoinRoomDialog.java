package com.stream.jerye.queue.lobby;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stream.jerye.queue.R;
import com.stream.jerye.queue.RoomActivity;
import com.stream.jerye.queue.firebase.FirebaseEventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jerye on 7/1/2017.
 */

public class JoinRoomDialog extends DialogFragment implements FirebaseEventBus.FirebaseRoomInfoHandler {
    @BindView(R.id.dialog_join_room_title)
    EditText roomTitleEditText;
    @BindView(R.id.dialog_join_room_password)
    EditText roomPasswordEditText;

    private String mTitleAttempt;
    private String mPasswordAttempt;
    private List<Room> listOfRooms = new ArrayList<>();


    @Override
    public void getRooms(Room room) {
        listOfRooms.add(room);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.join_room_dialog, null);
        ButterKnife.bind(this, dialogView);

        FirebaseEventBus.RoomDatabaseAccess mRoomAccessDatabase = new FirebaseEventBus.RoomDatabaseAccess(getActivity(), this);
        mRoomAccessDatabase.getRooms();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        roomTitleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });
        roomPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });
        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_join, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        final AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            Button positiveButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!roomTitleEditText.getText().toString().equals("")){
                        mTitleAttempt = roomTitleEditText.getText().toString();
                    }else{
                        Toast.makeText(getActivity(), "Title can't be blank!", Toast.LENGTH_SHORT).show();
                    }
                    mPasswordAttempt = roomPasswordEditText.getText().toString();

                    Log.d("Dialog", mTitleAttempt + mPasswordAttempt + "|||");
                    for (Room room : listOfRooms) {
                        Log.d("Dialog", "title: " + room.getTitle() + "password: " + room.getPassword() + "|||");

                        if (mTitleAttempt.equals(room.getTitle()) && (mPasswordAttempt.equals(room.getPassword()) || room.getPassword() == null)) {
                            SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
                            prefs.edit().putString("room key", room.getRoomKey()).apply();
                            Log.d("Dialog", "room key: " + room.getRoomKey());

                            Intent intent = new Intent(getActivity(), RoomActivity.class)
                                    .putExtra("room title", mTitleAttempt)
                                    .putExtra("room password", mPasswordAttempt);
                            startActivity(intent);
                            alertDialog.dismiss();
                            break;
                        }

                    }

                    Toast.makeText(getActivity(), "Incorrect Room & Password\nPlease Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
