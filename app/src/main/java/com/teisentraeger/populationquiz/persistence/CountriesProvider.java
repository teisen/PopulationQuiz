package com.teisentraeger.populationquiz.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.teisentraeger.populationquiz.model.Country;

/**
 * Created by teisentraeger on 6/21/2016.
 * Following tutorial at https://developer.android.com/guide/topics/providers/content-provider-basics.html
 */
public class CountriesProvider extends ContentProvider {

    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    final static String LOG_TAG = "CountriesProvider";
    public static final String AUTHORITY = "com.teisentraeger.populationquiz.provider";
    public static final String COUNTRIES_TABLE = "countries";

    private static SQLiteDatabase database;
    private static MySQLiteHelper dbHelper;

    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            Country.MAPPING_NAME, Country.MAPPING_CAPITAL, Country.MAPPING_REGION, Country.MAPPING_POPULATION, Country.MAPPING_AREA};


    static {
        /*
         * The calls to addURI() go here, for all of the content URI patterns that the provider
         * should recognize.
         */

        /*
         * Sets the integer value for multiple rows in table 3 to 1. Notice that no wildcard is used
         * in the path
         */
        sUriMatcher.addURI(AUTHORITY, COUNTRIES_TABLE, 1);

    }


    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new MySQLiteHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query");
        /*
         * Choose the table to query and a sort order based on the code returned for the incoming
         * URI. Here, too, only the statements for table 3 are shown.
         */
        switch (sUriMatcher.match(uri)) {


            // If the incoming URI was for all of table
            case 1:

                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                break;

             default:
                 Log.e(LOG_TAG, "URI not recognized: " + uri.toString());
        }

        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_COUNTRIES,    /* Tablename */
                projection, /* columns */
                null,
                null,
                null,
                null,
                sortOrder, /* orderBy */
                selection /* amount */
                );

        return cursor;
       
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // not implemented
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // not implemented
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // not implemented
        return 0;
    }
}
