package com.teisentraeger.populationquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.teisentraeger.populationquiz.persistence.CountriesDataSource;
import com.teisentraeger.populationquiz.sync.CountriesService;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = "MainActivity";
    private AdView mAdView;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        syncDataIfEmpty();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F6F357CEB7FC7F7347D4F7C755056E48")
                .build();
        mAdView.loadAd(adRequest);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCountriesEmpty()) {
                    Snackbar.make(view, "The countries are not retrieved yet, please wait a moment and try again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    syncDataIfEmpty();
                } else {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            // the context of the activity
                            MainActivity.this,

                            // For each shared element, add to this method a new Pair item,
                            // which contains the reference of the view we are transitioning *from*,
                            // and the value of the transitionName attribute
                            new Pair<View, String>(view.findViewById(R.id.fab),
                                    getString(R.string.transition_name_circle))
                    );
                    ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
                }
            }
        });

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


    }

    private boolean isCountriesEmpty() {
        CountriesDataSource datasource = new CountriesDataSource(this);
        datasource.open();
        return datasource.isCountriesEmpty();
    }

    /**
     * Start the Service to get the Data. Will only fetch data one time on initial startup.
     */
    private void syncDataIfEmpty() {

        // read in data and populate table if Db is empty (on first boot)
        if(isCountriesEmpty()) {
            Intent syncIntent = new Intent(this, CountriesService.class);
            startService(syncIntent);
        } else {
            Log.d(LOG_TAG, "Not starting service - data already present.");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        Log.i(LOG_TAG, "Setting screen name: " + LOG_TAG);
        mTracker.setScreenName("Image~" + LOG_TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
