package teacherkothai.example.com.teacherkothai;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button forgotpasswordBtn;
    private EditText userNameET;
    private EditText passwordET;
    private Toolbar logintoolbar;
    private static Context context;

    private ProgressDialog loginProgressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginProgressDialog = new ProgressDialog(this);
        loginProgressDialog.setTitle("Logging in");
        loginProgressDialog.setMessage("Please wait while we verify your credentials");

        loginBtn = (Button) findViewById(R.id.login_login);
        forgotpasswordBtn = (Button) findViewById(R.id.login_forgotpassword);
        userNameET = (EditText) findViewById(R.id.signup_emailid);
        passwordET = (EditText) findViewById(R.id.signup_password);

        logintoolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(logintoolbar);
        getSupportActionBar().setTitle("User Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();

        onLoginClicked();
        LoginActivity.context = getApplicationContext();
     }

    public static Context getAppContext() {
        return LoginActivity.context;
    }

    public static int getPx(Context context, int dimensionDp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
    }

    private void onLoginClicked() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!CheckNetwork.isInternetAvailable(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(userNameET.getText().toString())){
                    Toast.makeText(LoginActivity.this,"Please enter your Email ID",Toast.LENGTH_SHORT).show();
                    return;
                }

                else if(TextUtils.isEmpty(passwordET.getText().toString())){
                    Toast.makeText(LoginActivity.this,"Please enter your Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(passwordET.getText().length() < 8){
                    Toast.makeText(LoginActivity.this,"Please enter a Password of atleast 8 characters",Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = userNameET.getText().toString();
                String password = passwordET.getText().toString();

                loginUser(email,password);

            }
        });

    }

    private void loginUser(String email, String password) {
        loginProgressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    currentUID = firebaseAuth.getCurrentUser().getUid();

                    databaseReference.child("students").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(currentUID)){
                                loginProgressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this,StudentHomeActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            loginProgressDialog.dismiss();
                        }
                    });
                    databaseReference.child("tutors").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(currentUID)){
                                loginProgressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this,TutorHomeActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            loginProgressDialog.dismiss();
                        }
                    });

                }
                else if(!task.isSuccessful()){
                    loginProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Invalid Email or Password",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }
}
