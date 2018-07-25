package com.robynandcory.goodnewseveryone;

public class NewsItem {
    private String mStoryTitle;
    private String mStoryAuthor;
    private String mStoryDate;
    private String mStoryURL;
    private int mImageResource;

    public NewsItem(String storyTitle, String storyAuthor, String storyDate, String storyURL, int imageResource){
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
    public int getStoryImage() {return mImageResource; }
}
