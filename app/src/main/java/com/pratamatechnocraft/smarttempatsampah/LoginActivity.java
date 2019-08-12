package com.pratamatechnocraft.smarttempatsampah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText Email;
    private EditText Password;

    Button btn_sign_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);

        btn_sign_out = (Button) findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(this);
        /*btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(Login.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btn_sign_out.setEnabled(false);
                                showSignInOptions();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Init provider
        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build() // Google Builder
        );

        showSignInOptions();*/

    }

    private void proseslogin() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }
        String Emailstring = Email.getText().toString();
        String Passwordstring = Password.getText().toString();

        mAuth.signInWithEmailAndPassword(Emailstring, Passwordstring)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());

                        } else {
                            Toast.makeText(Login.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user){
        String username = usernameFromEmail(user.getEmail());

        /*//go to mainActivity
        startActivity(new Intent(AdminLoginActivity.this, MainActivity.class));
        finish();*/

        Toast.makeText(getApplicationContext(),"Login Berhasil",Toast.LENGTH_LONG).show();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        }else {
            return email;
        }

    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(Email.getText().toString())) {
            Email.setError("Required");
            result = false;
        }else {
            Email.setError(null);
        }

        if (TextUtils.isEmpty(Password.getText().toString())) {
            Password.setError("Required");
            result = false;
        }else {
            Password.setError(null);
        }

        return result;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_sign_out){
            proseslogin();
        }
    }

    /*private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.MyTheme)
                .build(),MY_REQUEST_CODE
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK)
            {
                //Get User
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Show email on Toast
                Toast.makeText( this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
                btn_sign_out.setEnabled(true);

            }
            else
            {
                Toast.makeText(this, ""+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}


