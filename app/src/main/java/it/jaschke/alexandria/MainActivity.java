package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReceiver;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    private static final String VIEWSTATE1 = "1";
    private static final String VIEWSTATE2 = "2";

    FrameLayout fragmentContainer1;
    FrameLayout fragmentContainer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageReceiver = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,filter);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));


        fragmentContainer1 = (FrameLayout) findViewById(R.id.fragmentContainer1);
        fragmentContainer2 = (FrameLayout) findViewById(R.id.fragmentContainer2);
        /*
        saved instance state, fragment may exist
        look up the instance that already exists by tag
         */
        if (savedInstanceState != null) {
            if(getResources().getBoolean(R.bool.dual_pane)) {
                if (getSupportFragmentManager().findFragmentByTag(VIEWSTATE2) != null && getSupportFragmentManager().findFragmentById(R.id.fragmentContainer1) instanceof ListOfBooks) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer1, getSupportFragmentManager().findFragmentByTag(VIEWSTATE1))
                            .replace(R.id.fragmentContainer2, getSupportFragmentManager().findFragmentByTag(VIEWSTATE2))
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer1, getSupportFragmentManager().findFragmentByTag(VIEWSTATE1))
                            .commit();
                    fragmentContainer2.setVisibility(View.GONE);
                }


            }else if (!getResources().getBoolean(R.bool.dual_pane)){
                if (getSupportFragmentManager().findFragmentByTag(VIEWSTATE2) != null && getSupportFragmentManager().findFragmentById(R.id.fragmentContainer1) instanceof ListOfBooks) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer1, getSupportFragmentManager().findFragmentByTag(VIEWSTATE1))
                            .replace(R.id.fragmentContainer2, getSupportFragmentManager().findFragmentByTag(VIEWSTATE2))
                            .commit();
                    fragmentContainer1.setVisibility(View.GONE);
                    fragmentContainer2.setVisibility(View.VISIBLE);
                }else{
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer1, getSupportFragmentManager().findFragmentByTag(VIEWSTATE1))
                            .commit();
                    fragmentContainer1.setVisibility(View.VISIBLE);
                    fragmentContainer2.setVisibility(View.GONE);
                }

            }

        } else if (savedInstanceState == null) {
            //If no fragment present then create a new one and place it in our main UI.
            //Check if we are in dual pane mode or not.
            Log.d("FRAG", "Creating new Frag");
            if(getResources().getBoolean(R.bool.dual_pane)){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer1, new ListOfBooks(), VIEWSTATE1)
                        .commit();
            }else{
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer1, new ListOfBooks(),VIEWSTATE1)
                        .commit();
            }
        }


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {


        Fragment nextFragment;
        switch (position){
            default:
            case 0:
                nextFragment = new ListOfBooks();
                break;
            case 1:
                nextFragment = new AddBook();
                break;
            case 2:
                nextFragment = new About();
                break;
        }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer1, nextFragment, VIEWSTATE1)
                    .addToBackStack(VIEWSTATE1)
                    .commit();
        if (fragmentContainer1!=null && fragmentContainer2!=null) {
            fragmentContainer1.setVisibility(View.VISIBLE);
            fragmentContainer2.setVisibility(View.GONE);
        }
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);
        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        if(getResources().getBoolean(R.bool.dual_pane)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer2, fragment, VIEWSTATE2)
                    .addToBackStack(VIEWSTATE2)
                    .commit();
            fragmentContainer1.setVisibility(View.VISIBLE);
            fragmentContainer2.setVisibility(View.VISIBLE);
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer2, fragment, VIEWSTATE2)
                    .addToBackStack(VIEWSTATE2)
                    .commit();
            fragmentContainer1.setVisibility(View.GONE);
            fragmentContainer2.setVisibility(View.VISIBLE);
        }
    }



    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    //TODO: Remove this method
    public void goBack(View view){
        getSupportFragmentManager().popBackStack();
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ) {
            getSupportFragmentManager().popBackStackImmediate();
            if (!(getResources().getBoolean(R.bool.dual_pane)) ){
                fragmentContainer1.setVisibility(View.VISIBLE);
            }else {
                fragmentContainer1.setVisibility(View.VISIBLE);
                fragmentContainer2.setVisibility(View.VISIBLE);
            }
        }else {
            super.onBackPressed();
        }
    }
}