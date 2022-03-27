package com.example.janicoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String name, mail, pass;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(getApplicationContext());

        Button createAccount = findViewById(R.id.create_account);
        TextView loginLink = findViewById(R.id.login_link);

        createAccount.setOnClickListener(v -> signUp());

        loginLink.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });
    }

    private void signUp() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        final User user = User.getSingle_instance();
        user.setName(name);
        user.setEmail(mail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "User Registered Successfully",
                            Toast.LENGTH_SHORT).show();
                    mDatabase.child("Users").child(Objects.requireNonNull(mAuth.getUid())).setValue(user);
                    finish();
                    startActivity(new Intent(RegistrationActivity.this, HomeScreenActivity.class));
                } else {
                    Toast.makeText(RegistrationActivity.this, "User Not Registered",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validate() {
        boolean valid = true;

        EditText shopName = findViewById(R.id.person_name_text);
        EditText emailAddress = findViewById(R.id.email_text);
        EditText password = findViewById(R.id.password_text);

        name = shopName.getText().toString();
        mail = emailAddress.getText().toString();
        pass = password.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            shopName.setError("at least 3 characters");
            valid = false;
        } else {
            shopName.setError(null);
        }

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

    private void onSignupFailed() {
        Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show();
    }
}