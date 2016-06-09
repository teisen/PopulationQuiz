package com.teisentraeger.populationquiz;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.teisentraeger.populationquiz.model.Country;
import com.teisentraeger.populationquiz.persistence.CountriesDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by teisentraeger on 6/8/2016.
 * following tutorial at http://www.androidauthority.com/create-simple-android-widget-608975/
 */
public class CountryWidgetProvider extends AppWidgetProvider {

    public static final String LOG_TAG = "CountryWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "onUpdate");
        final int count = appWidgetIds.length;

        // get 20 most pop contries
        CountriesDataSource datasource = new CountriesDataSource(context);
        datasource.open();
        ArrayList<Country> mAllCountries = new ArrayList<Country>(datasource.getMostPopulousCountries(20));

        // get one random country
        Collections.shuffle(mAllCountries, new Random(System.nanoTime()));


        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            Country currCountry = mAllCountries.get(i);

            // set View parameters according to current country
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.coutry_widget);
            remoteViews.setTextViewText(R.id.textViewCountry, currCountry.name.substring(0, Math.min(currCountry.name.length(), 8)));
            remoteViews.setTextViewText(R.id.textViewCapital, currCountry.capital.substring(0, Math.min(currCountry.capital.length(), 8)));
            //remoteViews.setTextViewText(R.id.textViewPop, currCountry.population.toString());
            //remoteViews.setTextViewText(R.id.textViewArea, currCountry.getAreaRounded());
            remoteViews.setTextViewText(R.id.textViewDensity, currCountry.getDensityRounded());
            int resID = context.getResources().getIdentifier(currCountry.getFilename(), "drawable", context.getPackageName());
            remoteViews.setImageViewResource(R.id.imageViewFlag, resID);

            Intent intent = new Intent(context, CountryWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

}
