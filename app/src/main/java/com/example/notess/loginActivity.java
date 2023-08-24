package com.example.notess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.v;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {
    EditText et_email, et_pass;
    Button login_btn;
    ProgressBar progress_bar;

    TextView createaccount_tv_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        login_btn = findViewById(R.id.login_btn);
        progress_bar = findViewById(R.id.progress_bar);
        createaccount_tv_btn = findViewById(R.id.createaccount_tv_btn);

        login_btn.setOnClickListener((v)-> loginUser() );
        createaccount_tv_btn.setOnClickListener((v)->startActivity(new Intent(loginActivity.this, CreateAccoutActivity.class)) );

    }
    void loginUser(){
        String email  =et_email.getText().toString();
        String password  = et_pass.getText().toString();


        boolean isValidated = validateData(email,password);
        if(!isValidated){
            return;
        }

        loginAccountInFirebase(email,password);

    }

    void loginAccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    //login is success
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        //go to mainactivity
                        startActivity(new Intent(loginActivity.this,MainActivity.class));
                        finish();
                    }else{
                        Utility.showToast(loginActivity.this,"Email not verified, Please verify your email.");
                    }

                }else{
                    //login failed
                    Utility.showToast(loginActivity.this,task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progress_bar.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
        }else{
            progress_bar.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email,String password){
        //validate the data that are input by user.

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
           et_pass.setError("Password length is invalid");
            return false;
        }
        return true;
    }

}
