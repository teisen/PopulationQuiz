package com.teisentraeger.populationquiz;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.teisentraeger.populationquiz.persistence.CountriesDataSource;
import com.teisentraeger.populationquiz.sync.CountriesService;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        syncDataIfEmpty();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCountriesEmpty()) {
                    Snackbar.make(view, "The countries are not retrieved yet, please wait a moment and try again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                }
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
