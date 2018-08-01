package com.robynandcory.goodnewseveryone;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;


import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    //URL for querying Guardian API
    private String mUrl;

    /**
     * Contstructor for a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     String to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<NewsItem> newsItemList = QueryUtils.getNewsData(mUrl);
        return newsItemList;
    }

}
