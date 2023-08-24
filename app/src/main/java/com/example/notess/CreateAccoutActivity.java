package com.example.notess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccoutActivity extends AppCompatActivity {
    EditText et_email, et_pass, et_confirmpass;
    Button createaccount_btn;
    ProgressBar progress_bar;
    TextView login_tv_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_accout);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        et_confirmpass = findViewById(R.id.et_confirmpass);
        progress_bar = findViewById(R.id.progress_bar);
        createaccount_btn = findViewById(R.id.createaccount_btn);
        login_tv_btn = findViewById(R.id.login_tv_btn);

        createaccount_btn.setOnClickListener(v -> createAccount());
        login_tv_btn.setOnClickListener(v -> finish());
    }

    void createAccount() {
        String email = et_email.getText().toString();
        String password = et_pass.getText().toString();
        String confirmPassword = et_confirmpass.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if(!isValidated){
            return;
        }
       createAccountInFirebase(email,password);

    }
    void createAccountInFirebase(String email,String password) {
   changeInProgress(true);

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccoutActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){//creating account is done
                    Toast.makeText(CreateAccoutActivity.this, "Succesfully created account,Check email to verify", Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    finish();
                }
                else{
                    //faliure
                    Toast.makeText(CreateAccoutActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
void changeInProgress(boolean inProgress){
      if(inProgress){
          progress_bar.setVisibility(View.VISIBLE);
          createaccount_btn.setVisibility(View.GONE);
      }else{
          progress_bar.setVisibility(View.GONE);
          createaccount_btn.setVisibility(View.VISIBLE);
      }
}
    boolean validateData(String email, String password, String confirmPassword) {
        //validates the data that user inputs

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Email is Invalid");
            return false;
        }
        if (password.length() < 6) {
            et_pass.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            et_confirmpass.setError("Password not matched");
            return false;
        }

        return true;

    }
}