package com.teisentraeger.populationquiz.persistence;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.teisentraeger.populationquiz.model.Country;

import java.util.ArrayList;

/**
 * Created by teisentraeger on 6/8/2016.
 * Following tutorial at http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
 */
public class CountriesLoader extends AsyncTaskLoader<ArrayList<Country>> {
    public static final String LOG_TAG = "CountriesLoader";

    public CountriesLoader(Context ctx) {
        // Loaders may be used across multiple Activitys (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(ctx);
    }

    /****************************************************/
    /** (1) A task that performs the asynchronous load **/
    /****************************************************/

    @Override
    public ArrayList<Country> loadInBackground() {
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client.
        Log.d(LOG_TAG, "onLoadFinished");
        CountriesDataSource datasource = new CountriesDataSource(getContext());
        datasource.open();
        ArrayList<Country> data = new ArrayList<Country>(datasource.getMostPopulousCountries(20));

        return data;
    }

}
