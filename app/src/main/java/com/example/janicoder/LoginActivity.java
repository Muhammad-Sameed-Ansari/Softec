package com.example.janicoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String mail, pass;
    private DatabaseReference mDatabase;
    Button participantBtn, eventManagerBtn, sponsorBtn;
    TextView loginTopText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(getApplicationContext());

        Button loginButton = findViewById(R.id.login_button);
        TextView sigupLink = findViewById(R.id.signup_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        sigupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        loginTopText = findViewById(R.id.login_top_text);
        participantBtn = findViewById(R.id.participant_btn);
        eventManagerBtn = findViewById(R.id.event_manager_btn);
        sponsorBtn = findViewById(R.id.sponsor_btn);

        userHandling();
    }

    private void login() {
        if (!validate()) {
            loginFailed();
            return;
        }

        final User user = User.getSingle_instance();


        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful",Toast.LENGTH_SHORT).show();
                            mDatabase =
                                    FirebaseDatabase.getInstance().getReference("Users/" + mAuth.getUid() + "/mName");
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    user.setName((String) snapshot.child("Users/" + mAuth.getUid() +
                                            "/mName").getValue());
                                    user.setEmail((String) snapshot.child("Users/" + mAuth.getUid() +
                                            "/mEmail").getValue());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("LoginFailed","Data reading failed");
                                }
                            });
                            finish();
                            startActivity(new Intent(LoginActivity.this, HomeScreenActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginFailed() {
        Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show();
    }

    private boolean validate() {
        boolean valid = true;

        EditText emailAddress = findViewById(R.id.email_text);
        EditText password = findViewById(R.id.password_text);

        mail = emailAddress.getText().toString();
        pass = password.getText().toString();

        if (mail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            emailAddress.setError("Enter a valid email address");
            valid = false;
        } else {
            emailAddress.setError(null);
        }

        if (pass.isEmpty() || pass.length() < 4 || pass.length() > 10) {
            password.setError("Length between 4 to 10");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void userHandling() {
        participantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTopText.setText("Login As Participant");
                participantBtn.setBackgroundColor(getColor(R.color.purple_200));
                eventManagerBtn.setBackgroundColor(Color.WHITE);
                sponsorBtn.setBackgroundColor(Color.WHITE);
            }
        });
        eventManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTopText.setText("Login As Event Manager");
                participantBtn.setBackgroundColor(Color.WHITE);
                eventManagerBtn.setBackgroundColor(getColor(R.color.purple_200));
                sponsorBtn.setBackgroundColor(Color.WHITE);
            }
        });
        sponsorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTopText.setText("Login As Sponsor");
                participantBtn.setBackgroundColor(Color.WHITE);
                eventManagerBtn.setBackgroundColor(Color.WHITE);
                sponsorBtn.setBackgroundColor(getColor(R.color.purple_200));
            }
        });
    }
}