package com.stream.jerye.queue.lobby;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.stream.jerye.queue.R;
import com.stream.jerye.queue.RoomActivity;
import com.stream.jerye.queue.firebase.FirebaseEventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jerye on 7/1/2017.
 */

public class CreateRoomDiaglog extends DialogFragment {
    @BindView(R.id.room_title)
    EditText roomTitleEditText;
    @BindView(R.id.room_password)
    EditText roomPasswordEditText;

    private String mRoomTitle;
    private String mRoomPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_room_dialog, null);
        ButterKnife.bind(this, dialogView);

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
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mRoomTitle = roomTitleEditText.getText().toString();
                        mRoomPassword = roomPasswordEditText.getText().toString();

                        if (mRoomTitle != null) {

                            FirebaseEventBus.RoomDatabaseAccess roomDatabaseAccess = new FirebaseEventBus.RoomDatabaseAccess(getActivity());
                            roomDatabaseAccess.push(mRoomTitle, mRoomPassword);

                            Intent intent = new Intent(getActivity(), RoomActivity.class)
                                    .putExtra("title",mRoomTitle)
                                    .putExtra("password",mRoomPassword);
                            startActivity(intent);
                        }

                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        AlertDialog dialog = builder.create();

        return dialog;

    }
}
