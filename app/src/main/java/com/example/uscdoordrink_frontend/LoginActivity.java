package com.example.uscdoordrink_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @Author: Yuxiang Zhang
 * @Date: 2022/3/23 01:03
 */

public class LoginActivity extends AppCompatActivity {

    EditText userName, password;
    Button login, register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.et_account);
        password = findViewById(R.id.et_password);
        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.Register);

        //initiate firebase database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userTable = firebaseDatabase.getReference("User");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTable.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Checking User avail
                        if (dataSnapshot.child(userName.getText().toString()).exists()) {
                            //Get User data
                            User user = dataSnapshot.child(userName.getText().toString()).getValue(User.class);
                            assert user != null;
                            if (user.getPassword().equals(password.getText().toString())) {
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                Constants.currentUser = user;
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                        }   else{
                            Toast.makeText(LoginActivity.this, "Login failed, user does not exist", Toast.LENGTH_SHORT);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}