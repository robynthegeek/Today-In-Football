package com.robynandcory.goodnewseveryone;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Today in Football is a news app for Udacity ABND
 * that loads the latest Football stories from the Guardian API.
 * <p>
 * With thanks to references:
 * For JSON parsing and networking reference:
 * https://github.com/udacity/ud843-QuakeReport
 * https://github.com/doowtnehpets/BitBacklashNewsfeed
 * https://medium.com/@JakobUlbrich/building-a-settings-screen-for-android-part-1-5959aa49337c
 * <p>
 * for RecyclerView with CardView assistance:
 * https://medium.com/@droidbyme/android-recyclerview-fca74609725e
 * Free Stock photo from http://absfreepic.com/
 */

public class NewsListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    //URL for data from the Guardian API
    private static final String stringURL =
            "https://content.guardianapis.com/search?show-fields=thumbnail%2Cbyline";
    //API Key for Guardian endpoint
    private static final String apiKey = "91b606b8-a998-4f76-9e92-5faf5f08fdb5";
    //Loader ID
    private static final int NEWS_LOADER_ID = 0;

    //default value for setting limiting stories to a single day
    private Boolean defaultLimitDate = false;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView newsView;
    private NewsRecycler recycler;
    private TextView noResultsTextView;
    private Boolean isLoading = false;


    private ArrayList<NewsItem> newsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        //find the RecyclerView and set the layoutManager
        newsView = findViewById(R.id.news_recycler);
        newsView.setLayoutManager(new LinearLayoutManager(this));

        //find the SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_recycler);
        swipeRefreshLayout.setRefreshing(true);

        noResultsTextView = findViewById(R.id.no_results);

        newsArrayList = new ArrayList<>();
        recycler = new NewsRecycler(this, newsArrayList);
        newsView.setAdapter(recycler);

        //Start Swipe Refresh
        startSwipeRefresh();

        //Run loader setup
        setupLoader();

    }

    /**
     * Checks for connectivity and runs loader if OK, if not, shows error Toast.
     */
    public void setupLoader() {

        //if there's a successful network connection, get API data using Loader
        if (hasInternet()) {
            getSupportLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
            isLoading = true;
            //if no network is found, show a toast asking the user to check the internet connection
        } else {
            noNetworkError();
        }
    }

    public boolean hasInternet() {
        //Check internet connectivity, get details on data network
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void noNetworkError() {
        swipeRefreshLayout.setRefreshing(false);
        View loadIndicator = findViewById(R.id.loading);
        loadIndicator.setVisibility(View.GONE);
        View loadingText = findViewById(R.id.loading_text);
        loadingText.setVisibility(View.GONE);
        Toast.makeText(NewsListActivity.this, (getResources().getString(R.string.check_connection)), Toast.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
//            "https://content.guardianapis.com/search?show-fields=thumbnail%2Cbyline&section=football&page-size=25&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5"
//            "https://content.guardianapis.com/search?show-fields=thumbnail%252Cbyline&section=football&page-size=10&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String minStories = sharedPreferences.getString(
                getString(R.string.settings_number_of_stories_key),
                getString(R.string.settings_number_of_stories_default));
        Boolean limitToToday = sharedPreferences.getBoolean(
                getString(R.string.settings_date_of_stories_key),
                defaultLimitDate);

        //parse string URL base from Guardian
        Uri uriStart = Uri.parse(stringURL);
        Uri.Builder uriBuilder = uriStart.buildUpon();
        uriBuilder.appendQueryParameter("section", "football");
        uriBuilder.appendQueryParameter("page-size", minStories);
        //If setting to limit to a single day is checked, set the to and from dates to today.
        if (limitToToday) {
            String todaysDate = getTodaysDate();
            uriBuilder.appendQueryParameter("from-date", todaysDate);
            uriBuilder.appendQueryParameter("to-date", todaysDate);
        }
        uriBuilder.appendQueryParameter("api-key", apiKey);

        Log.e("mainactivity", "URL built was: " + uriBuilder.toString());

        return new NewsLoader(this, uriBuilder.toString());
    }

    private String getTodaysDate() {
        DateFormat guardianDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return guardianDateFormat.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {

        swipeRefreshLayout.setRefreshing(false);

        View loadIndicator = findViewById(R.id.loading);
        loadIndicator.setVisibility(View.GONE);
        View loadingText = findViewById(R.id.loading_text);
        loadingText.setVisibility(View.GONE);
        isLoading = false;

        //Comment in the line below to test the condition where the newsItems list is empty
        //I have left it in solely for grading purposes:
        //newsItems = null;

        //Check if the news list has been populated, if so, display the list, if not, show a helpful message.
        if (newsItems != null && !newsItems.isEmpty()) {
            noResultsTextView.setVisibility(View.GONE);
            newsArrayList.clear();
            newsArrayList.addAll(newsItems);
            recycler.notifyDataSetChanged();
        } else {
            newsView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        newsArrayList.clear();
        recycler.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void restartLoader() {
        if (hasInternet()) {
            swipeRefreshLayout.setRefreshing(true);
            isLoading = true;
            getSupportLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        } else {
            noNetworkError();
        }

    }

    private void startSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    restartLoader();
                } else if (isLoading) {
                    Toast.makeText(NewsListActivity.this, (getResources().getString(R.string.please_wait)), Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
