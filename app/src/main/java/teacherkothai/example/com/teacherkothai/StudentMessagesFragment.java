package teacherkothai.example.com.teacherkothai;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class StudentMessagesFragment extends Fragment {

    private View studentMessageView;

    private String currentUID;

    private RecyclerView messageList;
    private RecyclerView.LayoutManager messageLayoutManager;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference classesReference;
    private DatabaseReference databaseReference;


    public StudentMessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        studentMessageView =  inflater.inflate(R.layout.fragment_student_messages, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        classesReference = FirebaseDatabase.getInstance().getReference().child("classes").child(currentUID);

        messageList = (RecyclerView) studentMessageView.findViewById(R.id.student_messageview_list);
        messageList.setHasFixedSize(true);
        messageLayoutManager = new LinearLayoutManager(getContext());
        messageList.setLayoutManager(messageLayoutManager);

        classesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    getStudentMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
        return studentMessageView;
    }
    private void getStudentMessages() {
        FirebaseRecyclerAdapter<MessagesStudent, StudentMessagesViewHolder> messageAdapter = new FirebaseRecyclerAdapter<MessagesStudent, StudentMessagesViewHolder>
                (
                        MessagesStudent.class,
                        R.layout.message_layout,
                        StudentMessagesFragment.StudentMessagesViewHolder.class,
                        classesReference
                ) {
            @Override
            protected void populateViewHolder(final StudentMessagesFragment.StudentMessagesViewHolder viewHolder, MessagesStudent model, int position) {

                final String tutorID = getRef(position).getKey();

                databaseReference.child("tutors").child(tutorID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();

                        viewHolder.setName(name);

                        viewHolder.reqView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Messages", "Remove from classes"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                //builder.setTitle("Select Options");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i == 0){
                                            Intent intent = new Intent(getContext(), ChatActivity.class);
                                            intent.putExtra("user_name", name);
                                            intent.putExtra("user_id", tutorID);
                                            startActivity(intent);
                                        }else if(i == 1){
                                            Map requestMap = new HashMap();

                                            requestMap.put("classes/" + currentUID + "/" + tutorID + "/type", null);
                                            requestMap.put("classes/" + tutorID + "/" + currentUID + "/type", null);

                                            databaseReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if(databaseError != null){
                                                        Toast.makeText(getContext(), "There was an error", Toast.LENGTH_LONG);
                                                    }
                                                    else{
                                                        Toast.makeText(getContext(), name + "has been removed from classes", Toast.LENGTH_LONG);
                                                    }
                                                }
                                            });
                                        }

                                    }
                                }); builder.show();
                            }

                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        messageList.setAdapter(messageAdapter);
    }

    public static class StudentMessagesViewHolder extends RecyclerView.ViewHolder{
        View reqView;

        public StudentMessagesViewHolder(View itemView) {
            super(itemView);
            reqView = itemView;
        }

        public void setName(String name){
            TextView studentName = (TextView) reqView.findViewById(R.id.message_view_name);
            studentName.setText(name);
        }

    }

}
