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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    final private static int FRAGMENT = R.layout.fragment_profile;
    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
    private Button btnLogOut;
    private TextView titleProfile;
    private ImageView ivProfile;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(FRAGMENT, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogOut = view.findViewById(R.id.btnLogOut);
        titleProfile = view.findViewById(R.id.titleProfile);
        ivProfile = view.findViewById(R.id.ivProfile);

        getUser();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser();
            }
        });
    }

    private void getUser() {
        rootDatabase.child("Users").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                titleProfile.setText(snapshot.child("username").getValue().toString());
                if (snapshot.child("image").getValue() != null) {
                    Glide.with(getContext()).load(snapshot.child("image").getValue().toString()).into(ivProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logOutUser() {
        firebaseAuth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }
}