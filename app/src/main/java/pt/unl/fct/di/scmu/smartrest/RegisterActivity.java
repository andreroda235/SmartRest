package pt.unl.fct.di.scmu.smartrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText mRestNameView;
    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPassView;
    private Button mSignupBtn;
    private ImageButton mReturnBtn;
    private RelativeLayout mLoadingPanelView;
    private TextView mLoadingTextView;
    private LinearLayout mRegisterLayoutView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRestNameView = findViewById(R.id.register_rest_text);
        mNameView = findViewById(R.id.register_name_text);
        mEmailView = findViewById(R.id.register_email_text);
        mPasswordView = findViewById(R.id.register_password_text);
        mConfirmPassView = findViewById(R.id.register_confirmPass_text);
        mSignupBtn = findViewById(R.id.register_signup_btn);
        mReturnBtn = findViewById(R.id.register_return_btn);

        mLoadingPanelView = findViewById(R.id.register_loadingPanel);
        mLoadingTextView = findViewById(R.id.register_loading_text);
        mRegisterLayoutView = findViewById(R.id.register_layout);

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }


    private void attemptRegister(){

        //TODO: handle async task ongoing by hiding button

        boolean cancel = false;

        mRestNameView.setError(null);
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPassView.setError(null);

        String restName = mRestNameView.getText().toString();
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPass = mConfirmPassView.getText().toString();

        if(!isValid(restName)){
            mRestNameView.requestFocus();
            mRestNameView.setHint("Invalid Restaurant Name!");
            cancel = true;
        }

        if(!isValid(name)){
            mNameView.requestFocus();
            mNameView.setHint("Invalid Name!");
            cancel = true;
        }

        //more email restrictions
        if(!isValid(email) || !validEmail(email)){
            mEmailView.requestFocus();
            mEmailView.setHint("Invalid Email!");
            cancel = true;
        }

        if(!isValid(password) || validPassword(password)){
            mPasswordView.requestFocus();
            mPasswordView.setHint("Invalid Password! Must Contain at least 8 characters (numbers and letters).");
            cancel = true;
        }

        if(!password.equals(confirmPass)){
            mConfirmPassView.requestFocus();
            mConfirmPassView.setHint("Password and Confirmed Password must be equal!");
            cancel = true;
        }

        if(cancel){
            return;
        }else{
            LoadingAnimation(true);
            register(restName, name, email, password);
        }

    }

    private void register(final String restaurant, final String name, final String email, final String password){
        //TODO: loading animation
        //TODO: handle error cases
        Task registerTask = mAuth.createUserWithEmailAndPassword(email,password);
        registerTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User registered.", Toast.LENGTH_SHORT).show();
                    mLoadingTextView.setText("User Registered!");

                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("password", password);
                    user.put("name", name);
                    user.put("restaurantid", restaurant);
                    user.put("role", "E"); //employee
                    user.put("tables", null);
                    user.put("uid", mAuth.getUid());
                    user.put("register_token", null);

                    db.collection("users").document(mAuth.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mLoadingTextView.setText("Logging user...");

                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            mLoadingTextView.setText("Done!");
                                            Intent intent = new Intent(RegisterActivity.this, TableMapActivity.class);
                                            startActivity(intent);
                                            finish();


                                        }else{
                                            Log.d(TAG, "Login failed: " + task.getException().toString());
                                            LoadingAnimation(false);
                                        }
                                    }
                                });

                            }else{
                                Log.d(TAG, "Adding user to firestore failed: " + task.getException().toString());
                                LoadingAnimation(false);
                            }
                        }
                    });

                }else{
                    Log.d(TAG, "User creation Auth failed: " + task.getException().toString());
                    LoadingAnimation(false);
                }
            }
        });
    }

    private boolean isValid(String field){
        //TODO: more restriciton
        return field.length() > 0;
    }

    private boolean validEmail(String email){
        return(email.contains("@") &&
                email.indexOf('@') == email.lastIndexOf('@') &&
                email.substring(0, email.indexOf('@')).length() > 0 &&
                email.substring(email.indexOf('@')).length() > 0 &&
                email.substring(email.indexOf('@')).contains("."));
    }

    private boolean validPassword(String password){
        return password.length() > 7 &&
                password.matches(".*\\d+.*");
    }


    private void LoadingAnimation(boolean animate){
        if(animate){
            mRegisterLayoutView.setVisibility(View.GONE);
            mReturnBtn.setVisibility(View.GONE);
            mLoadingPanelView.setVisibility(View.VISIBLE);
        }else{
            mLoadingTextView.setText("Registering User...");
            mLoadingPanelView.setVisibility(View.GONE);
            mRegisterLayoutView.setVisibility(View.VISIBLE);
            mReturnBtn.setVisibility(View.VISIBLE);

        }
    }

}