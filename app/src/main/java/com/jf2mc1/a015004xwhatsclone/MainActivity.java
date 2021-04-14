package com.jf2mc1.a015004xwhatsclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogInLogin, btnLoginSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Log In");

        etLoginEmail = findViewById(R.id.li_et_email);
        etLoginPassword = findViewById(R.id.li_et_pword);
        btnLogInLogin = findViewById(R.id.li_btn_login);
        btnLoginSignUp = findViewById(R.id.li_btn_signup);

        btnLogInLogin.setOnClickListener(this::onClick);
        btnLoginSignUp.setOnClickListener(this::onClick);

        etLoginPassword.setOnKeyListener(new View.OnKeyListener() {
            //            What this means is that you've now told the edittext view that you want to be
//            informed every time the user presses a key while this EditText has the focus.
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // sign up user
                    onClick(btnLogInLogin);
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch ((view.getId())) {
            case R.id.li_btn_login:
                logInUser();
                break;
            case R.id.li_btn_signup:
                goToSignUpActivity();
                break;
            default:
                break;
        }



    }

    private void logInUser() {

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if(email.equals("") || password.equals("")) {
            FancyToast.makeText(MainActivity.this,
                    "Email & password is required :(",
                    FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

        } else {

            ParseUser.logInInBackground(email, password,
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(user != null && e == null) {
                                FancyToast.makeText(MainActivity.this,
                                        user.getUsername() + " is logged in :)",
                                        FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

                                goToUsersActivity();

                            } else {
                                FancyToast.makeText(MainActivity.this,
                                        "Something went wrong :(" + e.getMessage(),
                                        FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

                            }
                        }
                    });

        }

    }

    private void goToUsersActivity() {

        startActivity(new Intent(MainActivity.this, UsersActivity.class));
        finish();
    }

    private void goToSignUpActivity() {

        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}