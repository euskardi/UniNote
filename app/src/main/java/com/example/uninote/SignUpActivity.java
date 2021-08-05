package com.example.uninote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static final int MINIMUM_OF_CHARACTERS = 6;

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignUp;

    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Missing fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < MINIMUM_OF_CHARACTERS) {
                    Toast.makeText(SignUpActivity.this, "the password needs a minimum of 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                signUpUser(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString());

            }
        });
    }

    private void signUpUser(String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Internet Connection Fail", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String id = firebaseAuth.getCurrentUser().getUid();
                final Map<String, Object> map = new HashMap<>();

                map.put("username", username);
                map.put("email", email);
                map.put("password", password);

                databaseReference.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> taskTwo) {
                        if (!taskTwo.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Internet Connection Fail", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }
                });

            }
        });
    }
}