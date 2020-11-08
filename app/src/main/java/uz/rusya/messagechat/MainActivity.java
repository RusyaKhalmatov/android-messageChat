package uz.rusya.messagechat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends BaseActivity {

    public final static String TABLE_CONTACTS = "contacts";

    private static final String TAG = "EmailPassword";
    private EditText emailField, passwordField, userNameField;
    private Button signInButton, createButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private User newUser;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null)
                {
                    Log.d(TAG,"onAuthStateChanged: signed_in: " + currentUser.getUid());
                }else
                {
                    Log.d(TAG,"onAuthStateChanged: signed_in:");
                }



            }
        };
        }


        public void init()
        {
            emailField = findViewById(R.id.emailField);
            passwordField = findViewById(R.id.passwordField);
            signInButton = findViewById(R.id.sign_in_button);
            createButton = findViewById(R.id.create_button);
            userNameField = findViewById(R.id.nameField);
        }

    public void createAccount(String email, String pwd)
    {
        Toast.makeText(this,"Create function", Toast.LENGTH_SHORT).show();
       Log.d(TAG,"createAccount:" + email);
        if(!validForm())
        {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email,pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"createUserwithEmail: onComplete: " + task.isSuccessful());

                        if(!task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this,"Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                            hideProgressDialog();
                            String userId = currentUser.getUid();
                            writeNewUser(userId,userNameField.getText().toString(), emailField.getText().toString());

                    }


                });
        signIn(email,pwd);

    }

    public void signInUser(View view) {
        String email = emailField.getText().toString();
        String pwd = passwordField.getText().toString();
        signIn(email,pwd);
    }

    public void signIn(String email,String pwd)
    {
        Log.d(TAG,"signIn: " + email);
        if(!validForm())
        {return;}

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email,pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"signInwithEmail: onComplete: " + task.isSuccessful());

                        if(!task.isSuccessful())
                        {
                            Log.w(TAG,"signInWithEmail: failed",task.getException());
                            Toast.makeText(MainActivity.this,"Authorization failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {

                            hideProgressDialog();

                            Intent intent = new Intent(MainActivity.this, Dialogs.class);
                            String userId = currentUser.getUid();
                            intent.putExtra("currentUseId", userId);
                            startActivity(intent);

                        }


                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    public void writeNewUser(String userId,String name, String email) {
        newUser = new User(name, email);
        mDatabase.child("users").child(userId).child("user_id").setValue(userId);
        mDatabase.child("users").child(userId).child("username").setValue(name);
        mDatabase.child("users").child(userId).child("user_email").setValue(email);
       // mDatabase.child("users").child(userId).child("username").setValue(name);
    }

    private boolean validForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            emailField.setError("Required");
            valid = false;
        }else
        {
            emailField.setError(null);
        }

        String pwd = passwordField.getText().toString();
        if(TextUtils.isEmpty(pwd))
        {
            passwordField.setError("Required");
            valid=false;
        }else
        {
            passwordField.setError(null);
        }
        return valid;

    }

    public void createUser(View view) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();


        Toast.makeText(this,"Fuck",Toast.LENGTH_SHORT).show();

        createAccount(email,password);
    }
}
