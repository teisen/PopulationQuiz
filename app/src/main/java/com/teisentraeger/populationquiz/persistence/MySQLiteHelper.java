package com.teisentraeger.populationquiz.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.teisentraeger.populationquiz.model.Country;

/**
 * Created by teisentraeger on 5/28/2016.
 * Adapted from http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_COUNTRIES = "countries";
    public static final String COLUMN_ID = "_id";

    private static final String DATABASE_NAME = "countries.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_COUNTRIES + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + Country.MAPPING_NAME + " text not null, "
            + Country.MAPPING_CAPITAL + " text not null, "
            + Country.MAPPING_REGION + " text not null, "
            + Country.MAPPING_POPULATION + " integer not null, "
            + Country.MAPPING_AREA + " double not null);";

    public MySQLiteHelper(Context pContext) {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
        context = pContext;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        onCreate(db);
    }

}
