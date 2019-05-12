package com.example.tinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.tinder.R;

public class LoginActivity extends AppCompatActivity {
    private CoordinatorLayout mainLayout;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private LinearLayout loadingLayout;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login - Tinder"); //posar el nom que vulguem

        mainLayout = findViewById(R.id.main_layout);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loadingLayout = findViewById(R.id.loading_layout);

        Button loginButton = findViewById(R.id.login);
        Button forgotPassword = findViewById(R.id.forgot_password);
        Button registerButton = findViewById(R.id.register);

        loginButton.setOnClickListener(v -> login());
        forgotPassword.setOnClickListener(v -> forgotPassword());
        registerButton.setOnClickListener(v -> register());

    }

    private void login() {
        username = this.usernameEditText.getText().toString();
        String password = this.passwordEditText.getText().toString();

        // TODO: Check for errors in user input!

        // Show the loading layout
        loadingLayout.setVisibility(View.VISIBLE);
       // Manager21Points.getInstance().login(username, password, this);
    }

    private void forgotPassword() {
        Snackbar.make(mainLayout, "No, you didn't.", Snackbar.LENGTH_LONG).show();
    }

    private void register() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }


}
