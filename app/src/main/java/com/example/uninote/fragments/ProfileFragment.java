package com.example.uninote.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uninote.LoginActivity;
import com.example.uninote.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    final private static int FRAGMENT = R.layout.fragment_profile;
    private Button btnLogOut;
    private TextView titleProfile;
    private ImageView ivProfile;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(FRAGMENT, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        titleProfile = view.findViewById(R.id.titleProfile);
        ivProfile = view.findViewById(R.id.ivProfile);
        ParseUser currentUser = ParseUser.getCurrentUser();
        titleProfile.setText(currentUser.getString("username"));
        ParseFile image = currentUser.getParseFile("picture");
        if (image != null){
            Glide.with(getContext()).load(currentUser.getParseFile("picture").getUrl()).into(ivProfile);
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser();
            }
        });
    }

    private void logOutUser() {
        ParseUser.logOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }
}