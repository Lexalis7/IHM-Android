package fr.unice.polytech.polynews.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import fr.unice.polytech.polynews.R;
import fr.unice.polytech.polynews.activities.DetailsActivity;
import fr.unice.polytech.polynews.fragments.MapsFragment;
import fr.unice.polytech.polynews.fragments.NewsGridFragment;
import fr.unice.polytech.polynews.fragments.AddFragment;

public class ViewAndAddActivity extends AppCompatActivity {

    public void SeeDetails(View view) {
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.putExtra("position", view.getTag().toString());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        int fragmentNumber = 0;
        try {
            fragmentNumber = Integer.parseInt(getIntent().getStringExtra("fragmentNumber"));
        } catch (Exception ignored) { }
        mViewPager.setCurrentItem(fragmentNumber);

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        @NonNull
        private final String[] pageTitles = {"Mes incidents", "Ajouter", "La carte"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a AddFragment (defined as a static inner class below).
            //return AddFragment.newInstance(position + 1);
            if (position == 0)
                return NewsGridFragment.newInstance(position + 1);
            else if (position == 1)
                return AddFragment.newInstance(position + 1, getIntent().getStringExtra("email"));
            else
                return MapsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                return 3;
            else
                return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position > pageTitles.length - 1)
                throw new IllegalArgumentException("<ommitted>");
            return pageTitles[position];
        }
    }
}
