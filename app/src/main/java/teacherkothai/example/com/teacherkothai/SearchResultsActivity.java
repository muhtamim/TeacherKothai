package teacherkothai.example.com.teacherkothai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchResultsActivity extends AppCompatActivity {

    private Toolbar resultToolbar;
    private RecyclerView resultList;
    private ProgressDialog resultProgressDialog;
    private RecyclerView.LayoutManager resultLayoutManager;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference tutorDatabaseRef;
    private String currentUID = "";
    private String location = "";
    private String studyLevel = "";
    private String subName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        resultToolbar = (Toolbar) findViewById(R.id.result_toolbar);
        setSupportActionBar(resultToolbar);
        getSupportActionBar().setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultProgressDialog = new ProgressDialog(this);
        resultProgressDialog.setTitle("Please Wait");
        resultProgressDialog.setMessage("Searching for available Tutors");
        resultProgressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        tutorDatabaseRef = FirebaseDatabase.getInstance().getReference().child("tutors");
        currentUID = firebaseAuth.getCurrentUser().getUid();
        location = getIntent().getStringExtra("user_Location");
        studyLevel = getIntent().getStringExtra("study_Level");
        subName = getIntent().getStringExtra("sub_Name");
        databaseReference = FirebaseDatabase.getInstance().getReference().child(location).child(studyLevel).child(subName);

        resultList = (RecyclerView) findViewById(R.id.result_list);
        resultList.setHasFixedSize(true);
        resultLayoutManager = new LinearLayoutManager(this);
        resultList.setLayoutManager(resultLayoutManager);

        searchTutors();
    }

    private void searchTutors() {
        FirebaseRecyclerAdapter<ResultsTutor, SearchViewHolder> resultsAdapter = new FirebaseRecyclerAdapter<ResultsTutor, SearchViewHolder>
                (
                        ResultsTutor.class,
                        R.layout.search_list_layout,
                        SearchViewHolder.class,
                        databaseReference
                ) {
            @Override
            protected void populateViewHolder(final SearchViewHolder viewHolder, ResultsTutor model, int position) {
                resultProgressDialog.show();
                //Toast.makeText(SearchResultsActivity.this,location+studyLevel+subName, Toast.LENGTH_SHORT).show();
                //viewHolder.setVal(model.getValue());

                final String tutorID = getRef(position).getKey();

                tutorDatabaseRef.child(tutorID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String profession = dataSnapshot.child("profession").getValue().toString();
                        final String language = dataSnapshot.child("languages").getValue().toString();
                        final String expertise = dataSnapshot.child("expertise").getValue().toString();
                        String loc = dataSnapshot.child("location").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();

                        viewHolder.setName(name);
                        viewHolder.setLocation(loc);

                        viewHolder.resultView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(SearchResultsActivity.this, TutorDetailsActivity.class);
                                intent.putExtra("tutor_name", name);
                                intent.putExtra("tutor_proff", profession);
                                intent.putExtra("tutor_lang", language);
                                intent.putExtra("tutor_exp", expertise);
                                intent.putExtra("tutor_id", tutorID);
                                intent.putExtra("tutor_image", image);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                resultProgressDialog.dismiss();
            }
        };
        resultList.setAdapter(resultsAdapter);
    }
    public static class SearchViewHolder extends RecyclerView.ViewHolder{
        View resultView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            resultView = itemView;
        }

        public void setName(String name){
            TextView tutorName = (TextView) resultView.findViewById(R.id.message_view_name);
            tutorName.setText(name);
        }
        public void setLocation(String loc){
            TextView tutorName = (TextView) resultView.findViewById(R.id.resultlist_location);
            tutorName.setText(loc);
        }
        public void setVal(int val){
            TextView tutorName = (TextView) resultView.findViewById(R.id.resultlist_location);
            tutorName.setText(""+val);
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
