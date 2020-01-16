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

public class StudentHomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private String currentUID;

    private Toolbar studentHomeToolbar;
    private TabLayout studentHomeTabs;
    private ViewPager studentViewPager;
    private SectionPagerAdapter studentSectionPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();

        studentHomeToolbar = (Toolbar) findViewById(R.id.student_home_appbar);
        setSupportActionBar(studentHomeToolbar);
        getSupportActionBar().setTitle("Home Tutor");

        studentViewPager = (ViewPager) findViewById(R.id.student_pager);
        studentSectionPager = new SectionPagerAdapter(getSupportFragmentManager());
        studentViewPager.setAdapter(studentSectionPager);
        studentHomeTabs = (TabLayout) findViewById(R.id.student_tabLayout);
        studentHomeTabs.setupWithViewPager(studentViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.student_menu_profile){
            Intent intent = new Intent(StudentHomeActivity.this, StudentProfileActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.student_menu_note){
            Intent intent = new Intent(StudentHomeActivity.this, NotesActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.student_menu_logout){
            firebaseAuth.signOut();
            Intent intent = new Intent(StudentHomeActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }


}
