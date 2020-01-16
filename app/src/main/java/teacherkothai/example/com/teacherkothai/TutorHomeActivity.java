package teacherkothai.example.com.teacherkothai;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class TutorHomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private Toolbar tutorHomeToolbar;
    private TabLayout tutorHomeTabs;
    private ViewPager tutorViewPager;
    private SectionPagerAdapterTutor tutorSectionPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

        firebaseAuth = FirebaseAuth.getInstance();

        tutorHomeToolbar = (Toolbar) findViewById(R.id.tutor_home_appbar);
        setSupportActionBar(tutorHomeToolbar);
        getSupportActionBar().setTitle("Home Tutor");

        tutorViewPager = (ViewPager) findViewById(R.id.tutor_pager);
        tutorSectionPager = new SectionPagerAdapterTutor(getSupportFragmentManager());
        tutorViewPager.setAdapter(tutorSectionPager);
        tutorHomeTabs = (TabLayout) findViewById(R.id.tutor_tabLayout);
        tutorHomeTabs.setupWithViewPager(tutorViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.tutor_menu_about_edit){
            Intent intent = new Intent(TutorHomeActivity.this, TutorProfileActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.tutor_menu_pdf){
            Intent intent = new Intent(TutorHomeActivity.this, BookActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.tutor_menu_schedule){
            Intent intent = new Intent(TutorHomeActivity.this, SchedulerActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.tutor_menu_logout){
            firebaseAuth.signOut();
            Intent intent = new Intent(TutorHomeActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }
}
