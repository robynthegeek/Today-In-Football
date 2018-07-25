package com.robynandcory.goodnewseveryone;

import android.graphics.Bitmap;

public class NewsItem {
    private String mStoryTitle;
    private String mStoryAuthor;
    private String mStoryDate;
    private String mStoryURL;
    // Thumbnail stored as BMP from Guardian API
    private Bitmap mImageResource;

    public NewsItem(String storyTitle, String storyAuthor, String storyDate, String storyURL, Bitmap imageResource){
//        if(storyAuthor == null){
//        mStoryAuthor = "Author Unknown";}
//        else if (imageResource == null){
//        mImageResource = R.drawable.oziltest;}

        mStoryTitle = storyTitle;
        mStoryAuthor = storyAuthor;
        mStoryDate = storyDate;
        mStoryURL = storyURL;
        mImageResource = imageResource;
    }

    //Returns the story title
    public String getStoryTitle() {return mStoryTitle; }

    //Returns the story author
    public String getStoryAuthor() {return mStoryAuthor; }

    //Returns the story Date
    public String getStoryDate() {return mStoryDate; }

    //Returns the story URL
    public String getStoryURL() {return mStoryURL; }

    //Returns the story image
    public Bitmap getStoryImage() {return mImageResource; }
}
