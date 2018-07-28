package com.robynandcory.goodnewseveryone;

import android.app.LoaderManager;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Today in Football is a news app that loads the latest Football stories from the Guardian API
 *
 * With thanks to references:
 * For JSON parsing and networking reference:
 * https://github.com/udacity/ud843-QuakeReport
 * https://github.com/doowtnehpets/BitBacklashNewsfeed
 *
 * for RecyclerView with CardView assistance:
 * https://medium.com/@droidbyme/android-recyclerview-fca74609725e
 * Free Stock photo from http://absfreepic.com/
 */

public class NewsListActivity extends AppCompatActivity
        implements LoaderCallbacks<List<NewsItem>> {

    //URL for data from the Guardian API
    private static final String stringURL =
            "https://content.guardianapis.com/search?show-fields=thumbnail%2Cbyline&section=football&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5";

    //Loader ID
    private static final int NEWS_LOADER_ID = 0;

    private RecyclerView newsView;
    private NewsRecycler recycler;
    private TextView noResultsTextView;

    private ArrayList<NewsItem> newsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        //find the RecyclerView and set the layoutmanager
        newsView = findViewById(R.id.news_recycler);
        newsView.setLayoutManager(new LinearLayoutManager(this));

        noResultsTextView = findViewById(R.id.no_results);

        newsArrayList = new ArrayList<>();
        recycler = new NewsRecycler(this, newsArrayList);
        newsView.setAdapter(recycler);

        //Check internet connectivity, get details on data network
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if there's a successful network connection, get API data using Loader
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            //if no network is found, show a toast asking the user to check the internet connection
        } else {
            View loadIndicator = findViewById(R.id.loading);
            loadIndicator.setVisibility(View.GONE);
            View loadingText = findViewById(R.id.loading_text);
            loadingText.setVisibility(View.GONE);
            Toast.makeText(NewsListActivity.this, (getResources().getString(R.string.check_connection)), Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, stringURL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        View loadIndicator = findViewById(R.id.loading);
        loadIndicator.setVisibility(View.GONE);
        View loadingText = findViewById(R.id.loading_text);
        loadingText.setVisibility(View.GONE);

        //Comment in the line below to test the condition where the newsItems list is empty
        //I have left it in solely for grading purposes:
        //newsItems = null;

        //Check if the news list has been populated, if so, display the list, if not, show a helpful message.
        if (newsItems != null && !newsItems.isEmpty()) {
            noResultsTextView.setVisibility(View.GONE);
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
}
