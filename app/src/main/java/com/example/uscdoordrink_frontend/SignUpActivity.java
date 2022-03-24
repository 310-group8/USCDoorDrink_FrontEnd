package com.example.uscdoordrink_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.constants.Constants;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @Author: Yuxiang Zhang
 * @Date: 2022/3/23 01:03
 */

public class SignUpActivity extends AppCompatActivity {

    EditText userName, password, contactInformation;
    Button login, register;
    RadioGroup select;
    RadioButton userType;
    UserType u;
    boolean isRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.et_account);
        password = findViewById(R.id.et_password);
        contactInformation = findViewById((R.id.et_ci));
        register = findViewById(R.id.btn_register);
        login = findViewById(R.id.Login);

        //initiate firebase database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userTable = firebaseDatabase.getReference("User");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTable.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Checking User avail
                        if (dataSnapshot.child(userName.getText().toString()).exists()) {
                            //username already exists
                            Toast.makeText(SignUpActivity.this, "Username already exits.", Toast.LENGTH_SHORT).show();
                        } else {
                            int radioID = select.getCheckedRadioButtonId();
                            userType = findViewById(radioID);
                            if (userType.getText().equals("customer")) {
                                User user = new User(userName.getText().toString(), password.getText().toString(), UserType.CUSTOMER);
                                userTable.child(contactInformation.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                isRegistered = true;
                            } else if (userType.getText().equals("seller")) {
                                User user = new User(userName.getText().toString(), password.getText().toString(), UserType.SELLER);
                                userTable.child(contactInformation.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                isRegistered = true;
                            } else {
                                Toast.makeText(SignUpActivity.this, "Please select your user type!", Toast.LENGTH_SHORT).show();
                                isRegistered = false;
                            }
                            if(isRegistered){
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}