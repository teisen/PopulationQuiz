package com.teisentraeger.populationquiz;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teisentraeger.populationquiz.model.Country;
import com.teisentraeger.populationquiz.persistence.CountriesDataSource;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final String LOG_TAG = "GameActivity";
    private ArrayList<Pair<Long, Country>> mItemArray;
    private ArrayList<Country> mAllCountries;
    private ArrayList<Country> mCurrentCountries;
    private DragListView mDragListView;
    private MySwipeRefreshLayout mRefreshLayout;
    private TextView mCorrectWrongTextView;
    private FloatingActionButton fab;
    private Button mNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCorrectWrongTextView = (TextView) findViewById(R.id.correctWrongTextView);
        mNextBtn = (Button) findViewById(R.id.nextButton);

        mRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setEnabled(false);
        mDragListView = (DragListView) findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                //Toast.makeText(mDragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {

                    Country c = mCurrentCountries.get(fromPosition);
                    mCurrentCountries.remove(c);
                    mCurrentCountries.add(toPosition, c);

                    int guess = guess();
                    Snackbar.make(mDragListView, "Correct = " + guess, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                }
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int guess = guess();

                if (guess > 3) {
                    changeUItoCorrect();
                } else {
                    changeUItoWrong();
                }
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView();
                changeUIToStandard();
            }
        });

        mRefreshLayout.setScrollingView(mDragListView.getRecyclerView());
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.app_color));

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            Log.d(LOG_TAG, "savedInstanceState != null");

            mAllCountries = savedInstanceState.getParcelableArrayList("mAllCountries");
            mCurrentCountries = savedInstanceState.getParcelableArrayList("mCurrentCountries");

            initItemArray();
            setupListRecyclerView();

        } else {
            // initialize members with default values for a new instance
            initView();
        }


    }

    private void initView() {
        Log.d(LOG_TAG, "initView");

        CountriesDataSource datasource = new CountriesDataSource(this);
        datasource.open();
        mAllCountries = new ArrayList<Country>(datasource.getMostPopulousCountries(20));
        String out = "";
        for(Country cnt : mAllCountries) {
            out = out + cnt.getFilename() + "\n";
        }
        Log.d(LOG_TAG, out);

        initCurrentCountries();

        initItemArray();

        setupListRecyclerView();
    }

    private void changeUItoWrong() {
        mCorrectWrongTextView.setText(R.string.wrong);
        mCorrectWrongTextView.setVisibility(View.VISIBLE);
    }

    private void changeUItoCorrect() {
        mCorrectWrongTextView.setText(R.string.correct);
        initItemArray();
        fab.setVisibility(View.GONE);
        mCorrectWrongTextView.setVisibility(View.VISIBLE);
        mNextBtn.setVisibility(View.VISIBLE);
    }

    private void changeUIToStandard() {
        fab.setVisibility(View.VISIBLE);
        mNextBtn.setVisibility(View.GONE);
        mCorrectWrongTextView.setVisibility(View.GONE);
        initCurrentCountries();
        initItemArray();
    }

    private int guess() {
        Double largest = Double.MAX_VALUE;
        int amountCorrect = 0;
        for(Country temp : mCurrentCountries) {
            Log.d(LOG_TAG,temp.toString());
            if (temp.getDensity() < largest) {
                amountCorrect++;
                Log.d(LOG_TAG,"Correct, " + temp.getDensity() + " is smaller than "+ largest + " new count is " + amountCorrect);
                largest = temp.getDensity();
            } else {
                // wrong guess, exit loop, return current number
                break;
            }

        }
        return amountCorrect;
    }

    private void initItemArray() {
        Log.d(LOG_TAG, "initItemArray");
        mItemArray = new ArrayList<>();
        String text;
        for (int i = 0; i < mCurrentCountries.size(); i++) {
           mItemArray.add(new Pair<>(Long.valueOf(i), mCurrentCountries.get(i)));
        }
    }

    private void initCurrentCountries() {
        Log.d(LOG_TAG, "initCurrentCountries");
        Collections.shuffle(mAllCountries, new Random(System.nanoTime()));
        mCurrentCountries = new ArrayList<Country>(mAllCountries.subList(0,4));
    }

    private void setupListRecyclerView() {
        Log.d(LOG_TAG, "setupListRecyclerView");
        mDragListView.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, false, this);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(this, R.layout.list_item));
    }

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.list_item_background));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList("mAllCountries", mAllCountries);
        outState.putParcelableArrayList("mCurrentCountries", mCurrentCountries);
    }

    /*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState");
        mAllCountries = savedInstanceState.getParcelableArrayList("mAllCountries");
        mCurrentCountries = savedInstanceState.getParcelableArrayList("mCurrentCountries");
    }
*/


}
