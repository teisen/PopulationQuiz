package com.teisentraeger.populationquiz.persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.teisentraeger.populationquiz.model.Country;

/**
 * Created by teisentraeger on 5/28/2016.
 * Adapted from http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */
public class CountriesDataSource {

    private final static String LOG_TAG = "CountriesDataSource";
    private final Context mContext;
    CountriesProvider mProvider = new CountriesProvider();


    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            Country.MAPPING_NAME, Country.MAPPING_CAPITAL, Country.MAPPING_REGION, Country.MAPPING_POPULATION, Country.MAPPING_AREA};

    public CountriesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        mContext = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<Country>();

        Uri myUri =  new Uri.Builder().scheme("content")
                .authority(CountriesProvider.AUTHORITY)
                .appendPath(CountriesProvider.COUNTRIES_TABLE).build();

        String selection = null;
        String[] selectionArgs = {""};
        String sortOrder = null;

        Cursor cursor = mProvider.query(myUri, allColumns, selection, selectionArgs, sortOrder);

        return getCountriesFromCursor(countries, cursor);
    }

    public List<Country> getMostPopulousCountries(int amount) {
        List<Country> countries = new ArrayList<Country>();

        Uri myUri =  new Uri.Builder().scheme("content")
                .authority(CountriesProvider.AUTHORITY)
                .appendPath(CountriesProvider.COUNTRIES_TABLE).build();

        String selection = ""+amount;
        String[] selectionArgs = {""};
        String sortOrder = Country.MAPPING_POPULATION + " DESC";

        Cursor cursor = mProvider.query(myUri, allColumns, selection, selectionArgs, sortOrder);

        return getCountriesFromCursor(countries, cursor);
    }

    private List<Country> getCountriesFromCursor(List<Country> comments, Cursor cursor) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Country country = cursorToCountry(cursor);
            comments.add(country);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }


    public Country createCountry(Country country) {
       // TODO: this method could be implemented inside the ContentProvider
        if(country.anyValueIsNull()) {
            //Log.d(LOG_TAG, "Not adding Country " + country.name + " to DB, as one of its values is null." + country.toString());
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(Country.MAPPING_NAME, country.name);
        values.put(Country.MAPPING_CAPITAL, country.capital);
        values.put(Country.MAPPING_REGION, country.region);
        values.put(Country.MAPPING_POPULATION, country.population);
        values.put(Country.MAPPING_AREA, country.area);
        long insertId = database.insert(MySQLiteHelper.TABLE_COUNTRIES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COUNTRIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Country newCountry = cursorToCountry(cursor);
        cursor.close();
        //Log.d(LOG_TAG, "Country " + country.name + " created in DB.");
        return newCountry;
    }

    private Country cursorToCountry(Cursor cursor) {
        Country country = new Country();
        country.name = cursor.getString(1);
        country.capital = cursor.getString(2);
        country.region = cursor.getString(3);
        country.population = cursor.getLong(4);
        country.area = cursor.getDouble(5);
        return country;
    }

    /**
     * Deletes all countries
     */
    public void scrubCountries() {
        Log.w(LOG_TAG, "Deleting all entries from "+ MySQLiteHelper.TABLE_COUNTRIES);
        database.execSQL("delete from "+ MySQLiteHelper.TABLE_COUNTRIES);
    }

    public boolean isCountriesEmpty() {
        String count = "SELECT count(*) FROM "+ MySQLiteHelper.TABLE_COUNTRIES;
        Cursor mcursor = database.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return (icount<1);
    }
}
