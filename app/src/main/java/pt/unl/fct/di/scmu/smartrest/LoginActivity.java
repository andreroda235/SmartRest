package pt.unl.fct.di.scmu.smartrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mLogin;
    private Button mSignUp;
    private TextView mLoadingTextView;



    private FirebaseAuth mAuth;

    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.login_email_text);
        mPasswordView = findViewById(R.id.login_password_text);
        mLoadingTextView = findViewById(R.id.login_loading_text);

        mLogin = findViewById(R.id.login_btn);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mSignUp = findViewById(R.id.login_signup_btn);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private void attemptLogin(){

        //TODO: handle async task ongoing by hiding button

        boolean cancel = false;

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        //more email restrictions
        if(!isValid(email)){
            mEmailView.requestFocus();
            mEmailView.setHint("Empty Email!");
            cancel = true;
        }

        if(!isValid(password)){
            mPasswordView.requestFocus();
            mPasswordView.setHint("Empty Password!");
            cancel = true;
        }

        if(cancel){
            return;
        }else{
            LoadingAnimation(true);
            login(email, password);
        }

    }

    private void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mLoadingTextView.setText("Done!");
                    Intent intent = new Intent(LoginActivity.this, TableMapActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.e(TAG, "Wrong Email/Password: " + task.getException().toString());
                    LoadingAnimation(false);
                }
            }
        });

    }

    private boolean isValid(String field){
        return field.length() > 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, TableMapActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void LoadingAnimation(boolean animate){
        LinearLayout mLoginLayoutView = findViewById(R.id.login_layout);
        RelativeLayout mLoadingPanelView = findViewById(R.id.login_loadingPanel);

        if(animate){
            mLoginLayoutView.setVisibility(View.GONE);
            mLoadingPanelView.setVisibility(View.VISIBLE);
        }else{
            mLoadingTextView.setText("Logging User...");
            mLoadingPanelView.setVisibility(View.GONE);
            mLoginLayoutView.setVisibility(View.VISIBLE);
        }
    }
}