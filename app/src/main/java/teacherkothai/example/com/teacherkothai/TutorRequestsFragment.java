package teacherkothai.example.com.teacherkothai;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TutorRequestsFragment extends Fragment {

    private View tutorRequestView;

    private String currentUID;

    private RecyclerView requestList;
    private ProgressDialog requestProgressDialog;
    private RecyclerView.LayoutManager requestLayoutManager;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference requestsReference;
    private DatabaseReference databaseReference;
    private DatabaseReference tutorDatabaseRef;


    public TutorRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        tutorRequestView =  inflater.inflate(R.layout.fragment_tutor_requests, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        requestsReference = FirebaseDatabase.getInstance().getReference().child("requests").child(currentUID);

        requestList = (RecyclerView) tutorRequestView.findViewById(R.id.tutor_request_list);
        requestList.setHasFixedSize(true);
        requestLayoutManager = new LinearLayoutManager(getContext());
        requestList.setLayoutManager(requestLayoutManager);

        requestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    getTutorRequests();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return tutorRequestView;
    }

    private void getTutorRequests() {

        FirebaseRecyclerAdapter<RequestsTutor, ReqViewHolder> requestAdapter = new FirebaseRecyclerAdapter<RequestsTutor, ReqViewHolder>
                (
                        RequestsTutor.class,
                        R.layout.tutor_requests_layout,
                        TutorRequestsFragment.ReqViewHolder.class,
                        requestsReference
                ) {
            @Override
            protected void populateViewHolder(final TutorRequestsFragment.ReqViewHolder viewHolder, RequestsTutor model, int position) {

                final String studentID = getRef(position).getKey();

                databaseReference.child("students").child(studentID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();

                        viewHolder.setName(name);

                        viewHolder.accBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Map requestMap = new HashMap();

                                requestMap.put("requests/" + currentUID + "/" + studentID + "/request_type", null);
                                requestMap.put("requests/" + studentID + "/" + currentUID + "/request_type", null);
                                requestMap.put("classes/" + currentUID + "/" + studentID + "/type", "student");
                                requestMap.put("classes/" + studentID + "/" + currentUID + "/type", "tutor");

                                databaseReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if(databaseError != null){
                                            Toast.makeText(getContext(), "There was an error", Toast.LENGTH_LONG);
                                        }
                                        else{
                                            Toast.makeText(getContext(), name + "has been accepted", Toast.LENGTH_LONG);
                                        }
                                    }
                                });
                            }
                        });
                        viewHolder.declineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Map requestMap = new HashMap();

                                requestMap.put("requests/" + currentUID + "/" + studentID + "/request_type", null);
                                requestMap.put("requests/" + studentID + "/" + currentUID + "/request_type", null);

                                databaseReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if(databaseError != null){
                                            Toast.makeText(getContext(), "There was an error", Toast.LENGTH_LONG);
                                        }
                                        else{
                                            Toast.makeText(getContext(), name + "has been rejected", Toast.LENGTH_LONG);
                                        }
                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        requestList.setAdapter(requestAdapter);

    }

    public static class ReqViewHolder extends RecyclerView.ViewHolder{
        View reqView;
        private Button accBtn;
        private Button declineBtn;

        public ReqViewHolder(View itemView) {
            super(itemView);
            reqView = itemView;
            accBtn = reqView.findViewById(R.id.tutor_reqview_accbtn);
            declineBtn = reqView.findViewById(R.id.tutor_reqview_declinebtn);

        }

        public void setName(String name){
            TextView studentName = (TextView) reqView.findViewById(R.id.student_req_name);
            studentName.setText(name);
        }

    }

}
