package teacherkothai.example.com.teacherkothai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TutorCoursesActivity extends AppCompatActivity {

    private Toolbar coursesToolbar;
    private Spinner studyLevel;
    private RecyclerView subectsRV;
    private RecyclerView.Adapter coursesAdapter;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private String studySchool;
    private String[] subName = {};
    private ArrayList<CoursesDataProvider> arrayList;
    private String location;
    private int val = 0;
    private int counter = 0;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String currentUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_courses);

        coursesToolbar = (Toolbar) findViewById(R.id.courses_toolbar);
        setSupportActionBar(coursesToolbar);
        getSupportActionBar().setTitle("Select Courses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        studyLevel = (Spinner) findViewById(R.id.courses_level);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.study_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studyLevel.setAdapter(adapter);


        subectsRV = (RecyclerView) findViewById(R.id.courses_subjects);
        subectsRV.setHasFixedSize(true);
        coursesLayoutManager = new LinearLayoutManager(this);
        subectsRV.setLayoutManager(coursesLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        location = getIntent().getStringExtra("location");
        //location = "Sylhet";


        populateSubjects();

    }

    @Override
    protected void onStart() {
        super.onStart();

        studyLevel.setSelection(0, true);
    }

    private void populateSubjects() {

        databaseReference.child(location).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                studyLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        studySchool = parent.getItemAtPosition(position).toString();
                        //Toast.makeText(TutorCoursesActivity.this,studySchool,Toast.LENGTH_SHORT).show();

                        if(studySchool.equals("Primary")){
                            subName  = getResources().getStringArray(R.array.primary);
                        }
                        else if(studySchool.equals("Secondary")){
                            subName  = getResources().getStringArray(R.array.secondary);
                        }
                        else if(studySchool.equals("Higher Secondary")){
                            subName  = getResources().getStringArray(R.array.higher_secondary);
                        }
                        else if(studySchool.equals("Undergraduate")){
                            subName  = getResources().getStringArray(R.array.undergraduate);
                        }

                        arrayList = new ArrayList<CoursesDataProvider>();

                        int i = 0;
                        for(String name : subName){
                            if(dataSnapshot.child(studySchool).child(subName[i]).hasChild(currentUID)){
                                val = 1;
                            }
                            else{
                                val = 0;
                            }
                            CoursesDataProvider coursesDataProvider = new CoursesDataProvider(val, subName[i]);
                            arrayList.add(i, coursesDataProvider);
                            i++;
                        }
                        coursesAdapter = new CoursesAdapter(
                                arrayList, TutorCoursesActivity.this,
                                currentUID, location, studySchool);
                        subectsRV.setAdapter(coursesAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
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

