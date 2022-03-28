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

import com.example.uscdoordrink_frontend.entity.Store;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.UserService;


/**
 * @Author: Yuxiang Zhang
 * @Date: 2022/3/23 01:03
 */

public class SignUpActivity extends AppCompatActivity {

    EditText userName, password, contactInformation;
    Button login, register;
    RadioGroup select;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.et_account);
        password = findViewById(R.id.et_password);
        contactInformation = findViewById((R.id.et_ci));
        register = findViewById(R.id.btn_register);
        login = findViewById(R.id.Login);
        select = (RadioGroup)findViewById(R.id.select);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService s = new UserService();
                if (s.isRegistered(userName.getText().toString()).equals("true")) {
                    Toast.makeText(SignUpActivity.this, "Username already exits, please log in.", Toast.LENGTH_SHORT).show();
                } else {
                    int id = select.getCheckedRadioButtonId();
                    switch (id) {
                        case R.id.customer:
                            User u1 = new User(userName.getText().toString(), password.getText().toString(), contactInformation.getText().toString(), UserType.CUSTOMER);
                            s.register(u1, new OnSuccessCallBack<Void>() {
                                @Override
                                public void onSuccess(Void input) {
                                    Toast.makeText(SignUpActivity.this, "Customer sign up successfully!", Toast.LENGTH_SHORT).show();
                                    setRegister();
                                }
                            }, new OnFailureCallBack<Exception>() {
                                @Override
                                public void onFailure(Exception input) {
                                    Toast.makeText(getApplicationContext(),
                                            "sign up failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                            case R.id.seller:
                                User u2 = new User(userName.getText().toString(), password.getText().toString(), contactInformation.getText().toString(), UserType.SELLER);
                                s.register(u2, new OnSuccessCallBack<Void>() {
                                @Override
                                public void onSuccess(Void input) {
                                    Toast.makeText(SignUpActivity.this, "Seller sign up successfully!", Toast.LENGTH_SHORT).show();
                                    setRegister();
                                }
                            }, new OnFailureCallBack<Exception>() {
                                @Override
                                public void onFailure(Exception input) {
                                    Toast.makeText(getApplicationContext(),
                                            "sign up failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                                break;
                                default:
                                    Toast.makeText(SignUpActivity.this, "Please select your user type!", Toast.LENGTH_SHORT).show();
                                    break;

                    }
                }
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

    public void setRegister(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}