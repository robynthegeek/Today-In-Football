package com.robynandcory.goodnewseveryone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NewsRecycler extends RecyclerView.Adapter<NewsRecycler.NewsHolder> {

    private static final String LOG_TAG = NewsListActivity.class.getName();

    private LayoutInflater layoutInflater;
    private ArrayList<NewsItem> newsItemArrayList;
    private Context context;

    //Constructor for recycler for news items
    public NewsRecycler(Context context, ArrayList<NewsItem> newsItemArrayList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.newsItemArrayList = newsItemArrayList;
    }

    //get the size of the news item array
    @Override
    public int getItemCount() {
        return newsItemArrayList.size();
    }

    //create a view and inflate it with the recycler
    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.news_card, parent, false);
        return new NewsHolder(view);
    }

    //Bind the array to the layout views indicated
    @Override
    public void onBindViewHolder(NewsHolder holder, int position) {
        final NewsItem currentStory = newsItemArrayList.get(position);
        if (currentStory.getStoryAuthor() != null) {
            holder.authorName.setText(currentStory.getStoryAuthor());
        } else {
            holder.authorName.setText(R.string.unknown_writer);
        }
        holder.storyName.setText(currentStory.getStoryTitle());
        holder.storyDate.setText(formatDate(currentStory.getStoryDate()));
        holder.storySection.setText(currentStory.getStorySection());

        if (currentStory.getStoryImage() != null) {
            holder.storyImage.setImageBitmap(currentStory.getStoryImage());
            holder.storyUrl = Uri.parse(currentStory.getStoryURL());
        } else {
            holder.storyImage.setImageResource(R.drawable.no_image_found);
        }
    }

    private String formatDate(String storyDate) {
        SimpleDateFormat guardianDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("MMMM d, yyyy");
        try {
            Date originalDate = guardianDateFormat.parse(storyDate);
            return newDateFormat.format(originalDate);
        } catch (ParseException dateError) {
            Log.e(LOG_TAG, "There was a date parsing error: ", dateError);
            return null;
        }
    }

    // ViewHolder for the current Story with OnClickListener
    class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView authorName;
        private TextView storyName;
        private TextView storyDate;
        private TextView storySection;
        private ImageView storyImage;
        private Uri storyUrl;

        private NewsHolder(View newsCardView) {
            super(newsCardView);
            authorName = newsCardView.findViewById(R.id.cardAuthorView);
            storyName = newsCardView.findViewById(R.id.cardHeadlineView);
            storyDate = newsCardView.findViewById(R.id.cardDateView);
            storySection = newsCardView.findViewById(R.id.cardSectionView);
            storyImage = newsCardView.findViewById(R.id.cardImageView);

            newsCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Open the news story in the user's chosen browser on item click.
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, storyUrl);
            PackageManager packageManager = context.getPackageManager();
            if (browserIntent.resolveActivity(packageManager) != null) {
                context.startActivity(browserIntent);
            } else {
               Log.e(LOG_TAG, "There is no browser available to handle the intent");
               Toast.makeText(context, R.string.install_browser, Toast.LENGTH_LONG).show();
            }
        }
    }
}
