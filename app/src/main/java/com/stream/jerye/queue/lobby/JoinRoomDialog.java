package com.stream.jerye.queue.lobby;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stream.jerye.queue.R;
import com.stream.jerye.queue.RoomActivity;
import com.stream.jerye.queue.firebase.FirebaseEventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jerye on 7/1/2017.
 */

public class JoinRoomDialog extends DialogFragment {
    @BindView(R.id.room_title)
    EditText roomTitleEditText;
    @BindView(R.id.room_password)
    EditText roomPasswordEditText;

    private String mRoomTitle;
    private String mRoomPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        FirebaseEventBus.RoomDatabaseAccess roomDatabaseAccess = new FirebaseEventBus.RoomDatabaseAccess(getActivity());

        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.create_room_dialog, null);
        ButterKnife.bind(this, dialogView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.join_room_dialog, null))
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
                    mRoomTitle = roomTitleEditText.getText().toString();
                    mRoomPassword = roomPasswordEditText.getText().toString();

                    SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
                    String roomKey = prefs.getString("room key", "");
                    if (!roomKey.equals("")) {

                        Intent intent = new Intent(getActivity(), RoomActivity.class)
                                .putExtra("room title", mRoomTitle)
                                .putExtra("room password", mRoomPassword);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
