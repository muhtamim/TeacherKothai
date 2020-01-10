package teacherkothai.example.com.teacherkothai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorDetailsActivity extends AppCompatActivity {

    private CircleImageView tutorImage;
    private TextView tutorName;
    private TextView tutorProfession;
    private TextView languages;
    private TextView tutorExpertise;
    private Button sendReqBtn;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference requestReference;
    private String currentUID;
    private String tutorUID;
    private  String loc;
    private  String nameEX;
    private  String professionEX;
    private  String languageEX;
    private  String expertiseEX;

    private String requestState = "not_sent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_details);
        getSupportActionBar().setTitle("Tutor Detailed");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tutorImage = (CircleImageView) findViewById(R.id.tutor_details_image);
        tutorName = (TextView) findViewById(R.id.tutor_details_name);
        tutorProfession = (TextView) findViewById(R.id.tutor_details_profession);
        languages = (TextView) findViewById(R.id.tutor_details_languages);
        tutorExpertise = (TextView) findViewById(R.id.tutor_details_expertise);
        sendReqBtn = (Button) findViewById(R.id.tutor_details_sendrequest);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        requestReference = FirebaseDatabase.getInstance().getReference().child("requests");

        nameEX = getIntent().getStringExtra("tutor_name");
        professionEX = getIntent().getStringExtra("tutor_proff");
        languageEX = getIntent().getStringExtra("tutor_lang");
        expertiseEX = getIntent().getStringExtra("tutor_exp");
        tutorUID = getIntent().getStringExtra("tutor_id");

        final String image = getIntent().getStringExtra("tutor_image");

        if(!image.equals("default")) {

            Picasso.with(TutorDetailsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.usericon).into(tutorImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(TutorDetailsActivity.this).load(image).placeholder(R.drawable.usericon).into(tutorImage);

                }
            });

        }

        tutorName.setText(nameEX);
        tutorProfession.setText(professionEX);
        languages.setText(languageEX);
        tutorExpertise.setText(expertiseEX);

        getRequestState();

        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestState.equals("sent")){
                    removeRequest();
                }else if(requestState.equals("not_sent")){
                    sendRequest();
                }


            }
        });

    }

    private void removeRequest() {
        Map requestMap = new HashMap();

        requestMap.put(currentUID + "/" + tutorUID + "/request_type", null);
        requestMap.put(tutorUID + "/" + currentUID + "/request_type", null);

        requestReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null){
                    Toast.makeText(TutorDetailsActivity.this, "There was an error", Toast.LENGTH_LONG);
                }
                else{
                    requestState = "not_sent";
                    Toast.makeText(TutorDetailsActivity.this, "Request has been cancelled", Toast.LENGTH_LONG);
                    setRequstButton();
                }
            }
        });
    }

    private void sendRequest() {
        Map requestMap = new HashMap();

        requestMap.put(currentUID + "/" + tutorUID + "/request_type", "sent");
        requestMap.put(tutorUID + "/" + currentUID + "/request_type", "recived");

        requestReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null){
                    Toast.makeText(TutorDetailsActivity.this, "There was an error", Toast.LENGTH_LONG);
                }
                else{
                    requestState = "sent";
                    Toast.makeText(TutorDetailsActivity.this, "Request has been sent", Toast.LENGTH_LONG);
                    setRequstButton();
                }
            }
        });
    }

    private void setRequstButton() { Toast.makeText(TutorDetailsActivity.this, "" + requestState, Toast.LENGTH_SHORT).show();
        if(requestState.equals("sent")){
            sendReqBtn.setText("Cancel Request");
        }else if(requestState.equals("not_sent")){
            sendReqBtn.setText("Send a Request");
        }else if(requestState.equals("class_member")){
            sendReqBtn.setText("Joined Classes");
            sendReqBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            sendReqBtn.setClickable(false);
            //Drawable d = sendReqBtn.getBackground();
            sendReqBtn.setBackground(getResources().getDrawable(R.drawable.button_style2));
        }
    }

    private void getRequestState() {

        databaseReference.child("classes").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(tutorUID)){
                    requestState = "class_member";
                }
                else{
                    requestReference.child(currentUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(tutorUID)){
                                requestState = "sent";
                            }
                            else{
                                requestState = "not_sent";
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }setRequstButton();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
}

