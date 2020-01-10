package teacherkothai.example.com.teacherkothai;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private Button signupBtn;
    private EditText fullName;
    private EditText emailId;
    private EditText password;
    private RadioButton asStudent;
    private RadioButton asTutor;
    private RadioButton selectionRB;
    private Toolbar signupToolBar;
    private RadioGroup radioGroup;
    private Spinner location;
    private String type = "";
    private String loc = "";

    private ProgressDialog signupProgressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupProgressDialog = new ProgressDialog(this);
        signupProgressDialog.setTitle("Registering new User");
        signupProgressDialog.setMessage("Please wait while we create an account for you");

        signupBtn = (Button) findViewById(R.id.signup_signup);
        fullName  = (EditText) findViewById(R.id.signup_fullname);
        emailId = (EditText) findViewById(R.id.signup_emailid);
        password = (EditText) findViewById(R.id.signup_password);
        radioGroup = (RadioGroup) findViewById(R.id.signup_radiogrp);
        asStudent = (RadioButton) findViewById(R.id.signup_student);
        asTutor = (RadioButton) findViewById(R.id.signup_tutor);
        location = (Spinner) findViewById(R.id.tutor_location);
        location.setSelection(-1);

        signupToolBar = (Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(signupToolBar);
        getSupportActionBar().setTitle("User Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loc = parent.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.signup_student){
                    type = "students";
                    Toast.makeText(SignUpActivity.this, "You have selected to join as a student", Toast.LENGTH_LONG).show();
                }
                else if(checkedId == R.id.signup_tutor){
                    type = "tutors";
                    Toast.makeText(SignUpActivity.this, "You have selected to join as a tutor", Toast.LENGTH_LONG).show();
                }
            }
        });

        onSignUpClicked();
    }

    private void onSignUpClicked() {

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!CheckNetwork.isInternetAvailable(SignUpActivity.this)){
                    Toast.makeText(SignUpActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(fullName.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Please provide your Full Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(emailId.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "PLease provide your Email ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(loc.equals("")){
                    Toast.makeText(SignUpActivity.this, "PLease select a location of your practice", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(type.equals("")){
                    Toast.makeText(SignUpActivity.this, "Are you a Student or Tutor?", Toast.LENGTH_SHORT).show();
                    return;
                }

                String full_Name = fullName.getText().toString();
                String email_ID = emailId.getText().toString();
                String pass_Word = password.getText().toString();


                /*
                if(asStudent.isSelected()){
                    type = "students";
                }
                else if(asTutor.isSelected()){
                    type = "tutors";
                }

                */
                registerUser(full_Name,email_ID,pass_Word, type, loc);
            }
        });

    }

    private void registerUser(final String full_name, final String email_id, String pass_word, final String type, final String loc) {
        signupProgressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email_id,pass_word).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String currentUID = firebaseAuth.getCurrentUser().getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(type).child(currentUID);

                    Map<String, String> userDataMap = new HashMap<String, String>();
                    if(type.equals("students")){
                        userDataMap.put("name",full_name);
                        userDataMap.put("email",email_id);
                        userDataMap.put("institution","default");
                        userDataMap.put("image","default");
                        userDataMap.put("image_thumb","default");
                        userDataMap.put("location", loc);
                    }
                    else{
                        userDataMap.put("name",full_name);
                        userDataMap.put("email",email_id);
                        userDataMap.put("profession","default");
                        userDataMap.put("languages","default");
                        userDataMap.put("expertise","default");
                        userDataMap.put("image","default");
                        userDataMap.put("image_thumb","default");
                        userDataMap.put("location", loc);
                    }

                    databaseReference.setValue(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                signupProgressDialog.dismiss();
                                if(type.equals("students")){
                                    Intent intent = new Intent(SignUpActivity.this, StudentHomeActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else if(type.equals("tutors")){
                                    Intent intent = new Intent(SignUpActivity.this, TutorHomeActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else{
                                signupProgressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "There was an Error connecting to database", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });


                }
                else if(!task.isSuccessful()){
                    signupProgressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "There was an Error in authenticating", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }


}
