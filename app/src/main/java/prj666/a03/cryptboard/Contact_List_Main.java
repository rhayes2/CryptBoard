package prj666.a03.cryptboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class Contact_List_Main extends AppCompatActivity  {

    public List<String> AllNames = null;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__list__main);


        getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout)); //Cyclical ref?
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //search bar



        //add contact button
        FloatingActionButton add_contact = (FloatingActionButton) findViewById(R.id.fab);
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /*Snackbar.make(view, "Create contact not implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent addContactIntent = new Intent(Contact_List_Main.this, AddContact.class);
                startActivity(addContactIntent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact__list__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //DatabaseHandler ContactsDB = new DatabaseHandler(getApplicationContext());
//
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {

                case 0:

                    //List<Contact> names = ContactsDB.getContactList();


                    Contact_List_Fav contact_fav = new Contact_List_Fav();


                    return contact_fav;
                case 1:
                    Contact_List_All contact_all = new Contact_List_All();


                    return contact_all;
                case 2:
                    Contact_List_Quick contact_quick = new Contact_List_Quick();

                    return contact_quick;

                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        SectionsPagerAdapter pagerAdapt;

        pagerAdapt = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }
}