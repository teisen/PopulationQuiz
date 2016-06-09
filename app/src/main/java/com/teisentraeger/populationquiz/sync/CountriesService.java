package com.teisentraeger.populationquiz.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.teisentraeger.populationquiz.model.Country;
import com.teisentraeger.populationquiz.persistence.CountriesDataSource;
import com.teisentraeger.populationquiz.persistence.MySQLiteHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by teisentraeger on 5/28/2016.
 * Following and adapting tutorial at http://www.survivingwithandroid.com/2014/03/consume-webservice-in-android-intentservice.html
 *
 */
public class CountriesService extends IntentService {

    public static final String LOG_TAG = "CountriesService";
    public static final String SERVICE_URL = "https://restcountries.eu/rest/v1/all";

    public CountriesService() {
        super("CountriesService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * If it needs to pull or send data to/from a web service or API only once, or on a
     * per request basis (such as a search application), app uses an IntentService to do so.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CountriesService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        long startTime = System.nanoTime();
        Log.d(LOG_TAG, "onHandleIntent - Sync started");

        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(SERVICE_URL)).openConnection();
            con.setRequestMethod("GET");
            con.connect();
            InputStream is = con.getInputStream();

            CountriesDataSource datasource = new CountriesDataSource(this);
            datasource.open();
            // read in data and populate table if Db is empty (on first boot)
            if(datasource.isCountriesEmpty()) {
                // Start parsing JSON
                List<Country> countries = readJsonStream(is);

                for (Country temp : countries) {
                    datasource.createCountry(temp);
                }
            } else {
                Log.d(LOG_TAG, "onHandleIntent - DB already has countries.");
            }

            }
        catch(UnknownHostException e0) {
            Log.e(LOG_TAG, "Could not connect to server. Retry.");
        }
        catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        Log.d(LOG_TAG, "onHandleIntent - ended - duration in nanos " + duration);
    }


    /**
     * Takes and IS and returns a list representation of it
     * Adapted from example at https://developer.android.com/reference/android/util/JsonReader.html
     * @param in
     * @return
     * @throws IOException
     */
    public List readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readCountriesArray(reader);
        }
        finally{
            reader.close();
        }
    }

    /**
     * Reads in an array of Countries
     * @param reader
     * @return
     * @throws IOException
     */
    public List readCountriesArray(JsonReader reader) throws IOException {
        List countries = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            countries.add(readCountry(reader));
        }
        reader.endArray();
        return countries;
    }

    /**
     * Reads in a single country
     * @param reader
     * @return
     * @throws IOException
     */
    public Country readCountry(JsonReader reader) throws IOException {
        long id = -1;
        String text = null;
        Country newCountry = new Country();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if(reader.peek() == JsonToken.NULL) {
                // skip null values
                reader.skipValue();
            } else if (name.equals(Country.MAPPING_NAME)) {
                newCountry.name = reader.nextString();
            } else if (name.equals(Country.MAPPING_CAPITAL)) {
                newCountry.capital = reader.nextString();
            } else if (name.equals(Country.MAPPING_REGION)) {
                newCountry.region = reader.nextString();
            } else if (name.equals(Country.MAPPING_POPULATION)) {
                newCountry.population = reader.nextLong();
            } else if (name.equals(Country.MAPPING_AREA)) {
                newCountry.area = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return newCountry;
    }

}
