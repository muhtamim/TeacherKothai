package teacherkothai.example.com.teacherkothai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class TutorProfileActivity extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;

    private CircleImageView tutorImage;
    private ImageButton tutorImageBtn;
    private EditText tutorName;
    private EditText tutorProfession;
    private EditText tutorLanguages;
    private EditText tutorExpertise;
    private Button tutorSaveBtn;
    private Spinner locationSpinner;
    private TextView location;
    private String loc = "";

    private ProgressDialog tutorProgressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private StorageReference mImageStorage;
    private DatabaseReference databaseReference;
    private String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tutorProgressDialog = new ProgressDialog(this);
        tutorProgressDialog.setTitle("Tutor Profile");
        tutorProgressDialog.setMessage("Please wait");

        tutorImage = (CircleImageView) findViewById(R.id.tutorprofile_image);
        tutorImageBtn = (ImageButton) findViewById(R.id.tutorprofile_imagebtn);
        tutorName = (EditText) findViewById(R.id.tutorprofile_name);
        tutorProfession = (EditText) findViewById(R.id.tutorprofile_profession);
        tutorLanguages = (EditText) findViewById(R.id.tutorprofile_languages);
        tutorExpertise = (EditText) findViewById(R.id.tutorprofile_expertise);
        tutorSaveBtn = (Button) findViewById(R.id.tutorprofile_savebtn);
        locationSpinner = (Spinner) findViewById(R.id.tutor_location);
        location = (TextView) findViewById(R.id.tutorprofile_location);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        currentUID = firebaseAuth.getCurrentUser().getUid();

        loadUserData();

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loc = parent.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tutorImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });


        onSaveButtonClicked();
    }

    private void loadUserData() {
        tutorProgressDialog.show();
        databaseReference.child("tutors").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String profession = dataSnapshot.child("profession").getValue().toString();
                String language = dataSnapshot.child("languages").getValue().toString();
                String expertise = dataSnapshot.child("expertise").getValue().toString();
                String currloc = dataSnapshot.child("location").getValue().toString();

                final String image = dataSnapshot.child("image").getValue().toString();

                if(!image.equals("default")) {

                    Picasso.with(TutorProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.usericon).into(tutorImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(TutorProfileActivity.this).load(image).placeholder(R.drawable.usericon).into(tutorImage);

                        }
                    });

                }

                tutorName.setText(name);
                tutorProfession.setText(profession);
                tutorLanguages.setText(language);
                tutorExpertise.setText(expertise);
                location.setText(currloc);

                tutorProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tutorProgressDialog.dismiss();
            }
        });
    }

    private void onSaveButtonClicked() {

        tutorSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorProgressDialog.show();
                String name = tutorName.getText().toString();
                String profession = tutorProfession.getText().toString();
                String language = tutorLanguages.getText().toString();
                String expertise  = tutorExpertise.getText().toString();

                Map tutorMap = new HashMap<String, String>();
                tutorMap.put("name",name);
                tutorMap.put("profession",profession);
                tutorMap.put("languages",language);
                tutorMap.put("expertise",expertise);
                tutorMap.put("location",loc);

                databaseReference.child("tutors").child(currentUID).updateChildren(tutorMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            tutorProgressDialog.dismiss();
                            Toast.makeText(TutorProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            loadUserData();
                        }
                        else{
                            tutorProgressDialog.dismiss();
                            Toast.makeText(TutorProfileActivity.this, "There was an Error connecting to database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = currentUID;


                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");



                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image", download_url);
                                        update_hashMap.put("image_thumb", thumb_downloadUrl);

                                        databaseReference.child("tutors").child(currentUID).updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    Toast.makeText(TutorProfileActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });


                                    } else {

                                        Toast.makeText(TutorProfileActivity.this, "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();

                                    }


                                }
                            });



                        } else {

                            Toast.makeText(TutorProfileActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();

                        }

                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }


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
