package com.robynandcory.goodnewseveryone;

import android.app.LoaderManager;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.content.AsyncTaskLoader;
import android.app.LoaderManager;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * With thanks to references:
 * https://github.com/udacity/ud843-QuakeReport
 * https://github.com/doowtnehpets/BitBacklashNewsfeed
 */

public class NewsListActivity extends AppCompatActivity
        implements LoaderCallbacks<List<NewsItem>> {

    private static final String LOG_TAG = NewsListActivity.class.getName();
    //URL for data from the Guardian API
    private static final String stringURL =
            "https://content.guardianapis.com/search?show-fields=thumbnail%2Cbyline&section=football&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5";

    //Loader ID
    private static final int NEWS_LOADER_ID = 0;


    private RecyclerView newsView;
    private NewsRecycler recycler;


    private ArrayList<NewsItem> newsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        newsView = findViewById(R.id.newsRecycler);
        newsView.setLayoutManager(new LinearLayoutManager(this));

        newsArrayList = new ArrayList<>();
        recycler = new NewsRecycler(this, newsArrayList);
        newsView.setAdapter(recycler);

        //Check internet connectivity, get details on data network
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if there's a network, get data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadIndicator = findViewById(R.id.loading);
            loadIndicator.setVisibility(View.GONE);
Toast.makeText(NewsListActivity.this, "No results were found", Toast.LENGTH_LONG).show();        }
Log.e(LOG_TAG, "The loader was initialized");
        }

    @NonNull
    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, "OnCreateLoader is called");
        return new NewsLoader(this, stringURL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
       View loadIndicator = findViewById(R.id.loading);
       loadIndicator.setVisibility(View.GONE);

       if (newsItems != null && !newsItems.isEmpty()){
           Log.e(LOG_TAG, "THis is a news item" + newsItems);
            newsArrayList.addAll(newsItems);
           recycler.notifyDataSetChanged();
       }
    }
    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        newsArrayList.clear();
        recycler.notifyDataSetChanged();
    }


}
