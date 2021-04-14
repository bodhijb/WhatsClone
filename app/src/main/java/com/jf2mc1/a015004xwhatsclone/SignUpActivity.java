package com.jf2mc1.a015004xwhatsclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import libs.mjn.prettydialog.PrettyDialog;

public class SignUpActivity extends AppCompatActivity implements
        View.OnClickListener {

    private EditText etSignupEmail, etSignupUsername, etSignupPassword;
    private Button btnSignupSignup, btnSignupLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        etSignupEmail = findViewById(R.id.su_et_email);
        etSignupUsername = findViewById(R.id.su_et_username);
        etSignupPassword = findViewById(R.id.su_et_pword);
        btnSignupSignup = findViewById(R.id.su_btn_signup);
        btnSignupLogin = findViewById(R.id.su_btn_login);

        btnSignupSignup.setOnClickListener(SignUpActivity.this);
        btnSignupLogin.setOnClickListener(SignUpActivity.this);



        // keyword enter == login
        etSignupPassword.setOnKeyListener(new View.OnKeyListener() {
            //            What this means is that you've now told the edittext view that you want to be
//            informed every time the user presses a key while this EditText has the focus.
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // sign up user
                    onClick(btnSignupSignup);
                }
                return false;
            }
        });




    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.su_btn_signup:
                signUpUser();
                break;

            case R.id.su_btn_login:
                goToLogInActivity();
                break;

            default:
                break;
        }




    }

    private void signUpUser() {

        String email = etSignupEmail.getText().toString();
        String username = etSignupUsername.getText().toString();
        String password = etSignupPassword.getText().toString();

        if(email.equals("") || username.equals("") || password.equals("")) {
            FancyToast.makeText(SignUpActivity.this,
                    "Info required :(",
                    FancyToast.LENGTH_LONG, FancyToast.ERROR,
                    true).show();

        } else {
            ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setMessage("Working...");
            progressDialog.show();

            ParseUser newUser = new ParseUser();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword(password);

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        FancyToast.makeText(SignUpActivity.this,
                                newUser.getUsername() + " Signed up :)",
                                FancyToast.LENGTH_LONG, FancyToast.SUCCESS,
                                true).show();

                        goToUsersActivity();

                    } else {
                        FancyToast.makeText(SignUpActivity.this,
                                "Something went wrong :(\n" + e.getMessage() ,
                                FancyToast.LENGTH_LONG, FancyToast.ERROR,
                                true).show();
                        Log.e("SIGNUPERROR", "From exception " + e.getMessage());
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void goToUsersActivity() {

        startActivity(new Intent(SignUpActivity.this, UsersActivity.class));
        finish();
    }


    private void goToLogInActivity() {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}