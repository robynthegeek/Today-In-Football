package com.robynandcory.goodnewseveryone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * references:
 * https://medium.com/@droidbyme/android-recyclerview-fca74609725e
 */

public class NewsRecycler extends RecyclerView.Adapter<NewsRecycler.NewsHolder> {
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

        holder.authorName.setText(currentStory.getStoryAuthor());
        holder.storyName.setText(currentStory.getStoryTitle());
        //holder.storyImage.setImageResource(R.drawable.oziltest);

       // NEEDS NETWORKING
            if (currentStory.getStoryImage() != null)
            holder.storyImage.setImageBitmap(currentStory.getStoryImage());

        holder.storyUrl = Uri.parse(currentStory.getStoryURL());
    }

    // ViewHolder for the current Story with OnClickListener
    class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView authorName;
        private TextView storyName;
        private ImageView storyImage;
        private Uri storyUrl;

        private NewsHolder(View newsCardView) {
            super(newsCardView);
            authorName = newsCardView.findViewById(R.id.cardAuthorView);
            storyName = newsCardView.findViewById(R.id.cardHeadlineView);
            storyImage = newsCardView.findViewById(R.id.cardImageView);

            newsCardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //Open the news story in the user's chosen browser on item click.
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, storyUrl);
            context.startActivity(browserIntent);

        }

    }
}
