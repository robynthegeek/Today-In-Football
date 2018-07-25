package com.robynandcory.goodnewseveryone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class NewsList extends AppCompatActivity {

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

        getNewsList();


        //https://content.guardianapis.com/sections?q=football&from-date=2018-01-01&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5
        //https://content.guardianapis.com/search?tag=football&api-key=91b606b8-a998-4f76-9e92-5faf5f08fdb5
    }


    private void getNewsList() {
        newsArrayList.add(new NewsItem("Bon Voyage", "jaina kyle", "Jan 4 2001", "http://www.fake.com", R.drawable.oziltest));
        newsArrayList.add(new NewsItem("Have a nice trip", "Lori camembert", "Jan 4 2001", "http://www.fake.com", R.drawable.oziltest));
        newsArrayList.add(new NewsItem("Bye Felicia", "Cantika Jones", "Jan 4 2001", "http://www.fake.com", R.drawable.oziltest));
        recycler.notifyDataSetChanged();
    }
}
