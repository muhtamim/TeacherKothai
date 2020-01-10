package teacherkothai.example.com.teacherkothai;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;



public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesRVHClass> {

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static ProgressDialog progressDialog;

    private String currentUID;
    private String location;
    private String studyLevel;
    private Context ctx;
    private ArrayList<CoursesDataProvider> arrayList = new ArrayList<CoursesDataProvider>();

    public CoursesAdapter(ArrayList<CoursesDataProvider> arrayList,
                          Context ctx,
                          String currentUID,
                          String location,
                          String studyLevel ) {

        this.arrayList = arrayList;
        this.ctx = ctx;
        this.currentUID = currentUID;
        this.location = location;
        this.studyLevel = studyLevel;
        progressDialog = new ProgressDialog(ctx);
    }

    @Override
    public CoursesRVHClass onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_selection_layout, parent, false);

        CoursesRVHClass coursesRVHClass = new CoursesRVHClass(  view, arrayList, ctx, currentUID, location,
                                                                studyLevel );
        return coursesRVHClass;
    }

    @Override
    public void onBindViewHolder(final CoursesRVHClass  holder, int position) {

        String subName = arrayList.get(position).getSubjectName();
        holder.subjectTV.setText(subName);


        int val = arrayList.get(position).getVal();

        //Toast.makeText(ctx,location+studyLevel+subName+val, Toast.LENGTH_SHORT).show();

        if(val == 1){
            holder.selectImage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class CoursesRVHClass extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView subjectTV;
        ImageView selectImage;
        String currentUID;
        String location;
        String studyLevel;
        String subName;
        Context ctx;
        ArrayList<CoursesDataProvider> arrayList = new ArrayList<CoursesDataProvider>();

        public CoursesRVHClass(View itemView,
                               ArrayList<CoursesDataProvider> arrayList,
                               Context ctx,
                               String currentUID, String location, String studyLevel) {

            super(itemView);
            this.arrayList = arrayList;
            this.ctx = ctx;
            this.currentUID = currentUID;
            this.location = location;
            this.studyLevel = studyLevel;
            itemView.setOnClickListener(this);

            subjectTV = (TextView) itemView.findViewById(R.id.courselayout_name);
            selectImage = (ImageView) itemView.findViewById(R.id.courselayout_image);

        }

        @Override
        public void onClick(View v) {

            progressDialog.setTitle("Courses Selection");
            progressDialog.setMessage("Updating courses");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            int position = getLayoutPosition();
            CoursesDataProvider coursesDataProvider = this.arrayList.get(position);
            subName = coursesDataProvider.getSubjectName().toString().trim();

            if(selectImage.getVisibility() == View.INVISIBLE){
                databaseReference.child(location).child(studyLevel).child(subName).child(currentUID).child("val").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            selectImage.setVisibility(View.VISIBLE);
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(ctx, "Ther was an Error, Try again Later" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if(selectImage.getVisibility() == View.VISIBLE){
                databaseReference.child(location).child(studyLevel).child(subName).child(currentUID).child("val").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            selectImage.setVisibility(View.INVISIBLE);
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(ctx, "Ther was an Error, Try again Later" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }
}
