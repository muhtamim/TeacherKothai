package teacherkothai.example.com.teacherkothai;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentRequestsFragment extends Fragment {

    private View studentRequestView;

    private String currentUID;

    private RecyclerView requestList;
    private ProgressDialog requestProgressDialog;
    private RecyclerView.LayoutManager requestLayoutManager;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference requestsReference;
    private DatabaseReference databaseReference;
    private DatabaseReference studentDatabaseRef;


    public StudentRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        studentRequestView =  inflater.inflate(R.layout.fragment_student_requests, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        requestsReference = FirebaseDatabase.getInstance().getReference().child("requests").child(currentUID);

        requestList = (RecyclerView) studentRequestView.findViewById(R.id.student_requestview_list);
        requestList.setHasFixedSize(true);
        requestLayoutManager = new LinearLayoutManager(getContext());
        requestList.setLayoutManager(requestLayoutManager);

        requestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    getStudentRequests();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return studentRequestView;
    }

    private void getStudentRequests() {
        FirebaseRecyclerAdapter<RequestsStudent, StudentReqViewHolder> requestAdapter = new FirebaseRecyclerAdapter<RequestsStudent, StudentReqViewHolder>
                (
                        RequestsStudent.class,
                        R.layout.student_requests_layout,
                        StudentRequestsFragment.StudentReqViewHolder.class,
                        requestsReference
                ) {
            @Override
            protected void populateViewHolder(final StudentRequestsFragment.StudentReqViewHolder viewHolder, RequestsStudent model, int position) {

                final String tutorID = getRef(position).getKey();

                databaseReference.child("tutors").child(tutorID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();

                        viewHolder.setName(name);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        requestList.setAdapter(requestAdapter);
    }

    public static class StudentReqViewHolder extends RecyclerView.ViewHolder{
        View reqView;

        public StudentReqViewHolder(View itemView) {
            super(itemView);
            reqView = itemView;
        }

        public void setName(String name){
            TextView tutorName = (TextView) reqView.findViewById(R.id.student_req_name);
            tutorName.setText(name);
        }

    }

}
