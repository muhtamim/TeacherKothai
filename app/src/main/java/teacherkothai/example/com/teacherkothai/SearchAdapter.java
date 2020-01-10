package teacherkothai.example.com.teacherkothai;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchRVHClass> {

    private String currentUID;
    private String location;
    private String studyLevel;
    private Context ctx;
    private ArrayList<CoursesDataProvider> arrayList = new ArrayList<CoursesDataProvider>();

    public SearchAdapter(ArrayList<CoursesDataProvider> arrayList,
                         Context ctx,
                         String currentUID,
                         String location,
                         String studyLevel ) {

        this.arrayList = arrayList;
        this.ctx = ctx;
        this.currentUID = currentUID;
        this.location = location;
        this.studyLevel = studyLevel;
    }

    @Override
    public SearchAdapter.SearchRVHClass onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_selection_layout, parent, false);

        SearchAdapter.SearchRVHClass SearchRVHClass = new SearchAdapter.SearchRVHClass(  view, arrayList, ctx, currentUID, location,
                studyLevel );
        return SearchRVHClass;
    }

    @Override
    public void onBindViewHolder(final SearchAdapter.SearchRVHClass holder, int position) {

        String subName = arrayList.get(position).getSubjectName();
        holder.subjectTV.setText(subName);

        int val = arrayList.get(position).getVal();

        //Toast.makeText(ctx,location+studyLevel+subName+val, Toast.LENGTH_SHORT).show();

        /*if(val == 1){
            holder.selectImage.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class SearchRVHClass extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView subjectTV;
        ImageView selectImage;
        String currentUID;
        String location;
        String studyLevel;
        String subName;
        Context ctx;
        ArrayList<CoursesDataProvider> arrayList = new ArrayList<CoursesDataProvider>();

        public SearchRVHClass(View itemView,
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
            int position = getLayoutPosition();
            CoursesDataProvider coursesDataProvider = this.arrayList.get(position);
            subName = coursesDataProvider.getSubjectName().toString().trim();

            Intent intent = new Intent(ctx, SearchResultsActivity.class);
            intent.putExtra("user_Location", location);
            intent.putExtra("study_Level", studyLevel);
            intent.putExtra("sub_Name", subName);
            ctx.startActivity(intent);
        }
    }
}