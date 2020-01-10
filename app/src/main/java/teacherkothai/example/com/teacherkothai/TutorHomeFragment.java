package teacherkothai.example.com.teacherkothai;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TutorHomeFragment extends Fragment {

    private CircleImageView tutorImage;
    private TextView tutorName;
    private TextView tutorProfession;
    private TextView languages;
    private TextView tutorExpertise;
    private Button selectCourseBtn;

    private  View tutorHomeView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference tutorDatabase;
    private String currentUID;
    private  String loc;


    public TutorHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tutorHomeView = inflater.inflate(R.layout.fragment_tutor_home, container, false);

        tutorImage = (CircleImageView) tutorHomeView.findViewById(R.id.tutorhome_image);
        tutorName = (TextView) tutorHomeView.findViewById(R.id.tutorhome_name);
        tutorProfession = (TextView) tutorHomeView.findViewById(R.id.tutorhome_profession);
        languages = (TextView) tutorHomeView.findViewById(R.id.tutorhome_languages);
        tutorExpertise = (TextView) tutorHomeView.findViewById(R.id.tutorhome_expertise);
        selectCourseBtn = (Button) tutorHomeView.findViewById(R.id.tutorhome_selectcourses);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tutorDatabase = FirebaseDatabase.getInstance().getReference();
        loadUserData();


        selectCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TutorCoursesActivity.class);
                intent.putExtra("location", loc);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return tutorHomeView;

    }

    private void loadUserData() {

        tutorDatabase.child("tutors").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String profession = dataSnapshot.child("profession").getValue().toString();
                String language = dataSnapshot.child("languages").getValue().toString();
                String expertise = dataSnapshot.child("expertise").getValue().toString();

                loc = dataSnapshot.child("location").getValue().toString();

                final String image = dataSnapshot.child("image").getValue().toString();

                if(!image.equals("default")) {

                    Picasso.with(getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.usericon).into(tutorImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(getContext()).load(image).placeholder(R.drawable.usericon).into(tutorImage);

                        }
                    });

                }

                tutorName.setText(name);
                tutorProfession.setText(profession);
                languages.setText(language);
                tutorExpertise.setText(expertise);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
