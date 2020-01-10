package teacherkothai.example.com.teacherkothai;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                StudentSearchFragment  studentSearchFragment = new StudentSearchFragment();
                return studentSearchFragment;
            case 1:
                StudentMessagesFragment studentMessagesFragment = new StudentMessagesFragment();
                return studentMessagesFragment;
            case 2:
                StudentRequestsFragment studentRequestsFragment  = new StudentRequestsFragment();
                return studentRequestsFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Search";
            case 1:
                return "Messages";
            case 2:
                return "Requests";
            default:
                return null;
        }
    }
}
