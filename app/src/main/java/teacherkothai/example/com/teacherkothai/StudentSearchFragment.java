package teacherkothai.example.com.teacherkothai;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentSearchFragment extends Fragment {

    private View studentSearchView;

    private Spinner studyLevel;
    private RecyclerView subjectList;
    private RecyclerView.LayoutManager searchLayoutManager;
    private RecyclerView.Adapter searchAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String currentUID = "";
    private String location = "";
    private String studySchool = "";
    private ArrayList<CoursesDataProvider> arrayList;
    private String[] subName = {};


    public StudentSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        studentSearchView =  inflater.inflate(R.layout.fragment_student_search, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUID = firebaseAuth.getCurrentUser().getUid();

        studyLevel = (Spinner) studentSearchView.findViewById(R.id.search_studylevel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.study_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studyLevel.setAdapter(adapter);

        subjectList = (RecyclerView) studentSearchView.findViewById(R.id.search_subjects);
        subjectList.setHasFixedSize(true);
        searchLayoutManager = new LinearLayoutManager(getContext());
        subjectList.setLayoutManager(searchLayoutManager);

        getUserLocation();
        getStudyLevel();
        onSearchClicked();

        return studentSearchView;
    }

    private void populateSubjects() {
        arrayList = new ArrayList<CoursesDataProvider>();
        int i = 0;
        for(String name : subName){
            CoursesDataProvider coursesDataProvider = new CoursesDataProvider(0, subName[i]);
            arrayList.add(i, coursesDataProvider);
            i++;
        }
        searchAdapter = new SearchAdapter(
                arrayList, getContext(),
                currentUID, location, studySchool);
        subjectList.setAdapter(searchAdapter);
    }

    private void onSearchClicked() {


    }

    private void getStudyLevel() {
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
                populateSubjects();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getUserLocation() {
        databaseReference.child("students").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location = dataSnapshot.child("location").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
