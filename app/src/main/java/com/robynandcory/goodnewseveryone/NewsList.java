package com.robynandcory.goodnewseveryone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * With thanks to references:
 * https://github.com/udacity/ud843-QuakeReport
 * https://github.com/doowtnehpets/BitBacklashNewsfeed
 */

public class NewsList extends AppCompatActivity {

    private static final String LOG_TAG = NewsList.class.getName();

    private RecyclerView newsView;
    private NewsRecycler recycler;
    private ArrayList<NewsItem> newsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(LOG_TAG, "help");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        newsView = findViewById(R.id.newsRecycler);
        newsView.setLayoutManager(new LinearLayoutManager(this));

        newsArrayList = new ArrayList<>();
        recycler = new NewsRecycler(this, newsArrayList);
        newsView.setAdapter(recycler);

//        newsView.onItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> adapterView)
//        })

        getNewsList();


        //https://content.guardianapis.com/sections?q=football&from-date=2018-01-01&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5
        //https://content.guardianapis.com/search?q=football&from-date=2018-07-24&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5
        //https://content.guardianapis.com/search?show-fields=thumbnail%2Cbyline&section=football&from-date=2018-07-24&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5
    }

//    private void getJUNK() {
//        newsArrayList.add(new NewsItem("Bon Voyage", "jaina kyle", "Jan 4 2001", "http://www.fake.com", R.drawable.oziltest));
//        newsArrayList.add(new NewsItem("Have a nice trip", "Lori camembert", "Jan 4 2001", "http://www.fake.com", R.drawable.oziltest));
//        newsArrayList.add(new NewsItem("Bye Felicia", "Cantika Jones", "Jan 4 2001", "http://www.fake.com", R.drawable.oziltest));
//        recycler.notifyDataSetChanged();
//    }

    private void getNewsList() {
        /**
         * take a string from Guardian API, turn it into a JSONObject,
         * parse it into the fields of a NewsItem and add it to an ArrayList
         */
        try {
            Log.e(LOG_TAG, "Attempting JSON parsing");
            JSONObject rootJSON = new JSONObject(QueryUtils.tempJSON);
            JSONObject newsJSON = rootJSON.getJSONObject("response");
            JSONArray newsArray = newsJSON.getJSONArray("results");

            //loop through the JSON response and parse out each story
            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject thisNewsItem = newsArray.getJSONObject(i);
                String storyTitle = thisNewsItem.getString("webTitle");
                String storyURL = thisNewsItem.getString("webUrl");
                String storyDate = thisNewsItem.getString("webPublicationDate");

                //get additional fields that may not be in every story.
                JSONObject newsFields = thisNewsItem.getJSONObject("fields");
                String storyAuthor = newsFields.optString("byline");
                String storyImage = newsFields.optString("thumbnail");

                newsArrayList.add(new NewsItem(storyTitle, storyAuthor, storyDate, storyURL, getStoryImage(storyImage)));


                recycler.notifyDataSetChanged();

            }
        } catch (JSONException error) {
            Log.e(LOG_TAG, "there was a JSON error", error);

        }

    }

    private static Bitmap getStoryImage(String url) {
        Bitmap storyImage = null;
        try {
            InputStream inputStream = new URL(url).openStream();
            storyImage = BitmapFactory.decodeStream(inputStream);
        } catch (Exception bmpError) {
            Log.e(LOG_TAG, "This is the URL that failed: " + url, bmpError);
        }
        return storyImage;

    }

}
