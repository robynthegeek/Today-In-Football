package com.robynandcory.goodnewseveryone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    private static final String LOG_TAG = NewsListActivity.class.getName();

    private QueryUtils() {
        //query utils should not be constructed elsewhere
    }

    /**
     * convert URLstring into URL object
     *
     * @return URL object
     */

    public static List<NewsItem> getNewsData(String requestUrl) {
        URL url = getURL(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = startHttpRequest(url);
        } catch (IOException ioError) {
            Log.e(LOG_TAG, "Trouble with http request: ", ioError);
        }

        //get JSON response and create a list of {@link NewsItem}s
        List<NewsItem> newsItems = extractFeatureFromJson(jsonResponse);
        return newsItems;
    }

    private static URL getURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException urlError) {
            Log.e(LOG_TAG, "The url had an error: ", urlError);
        }
        return url;

    }

    private static String startHttpRequest(URL url) throws IOException {
        //Returns empty response early if URL is null.
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            //if the connection works, parse it, if not, error response
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Log.e(LOG_TAG, "The url had an error: " + httpURLConnection.getResponseCode());
            } else {
                Log.e(LOG_TAG, "The url had an error: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException ioError) {
            Log.e(LOG_TAG, "The io when getting JSON results had an error: ", ioError);
        } finally {
            //close the URL connection and input stream if they were successful.
            if (httpURLConnection != null) httpURLConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * Convert the inputStream into a String which is the JSON response ready for parsing
     *
     * @param inputStream
     * @return the String output JSON from the server
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder outputString = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                outputString.append(line);
                line = reader.readLine();
            }

        }
        return outputString.toString();
    }

    /**
     * take a string from Guardian API, turn it into a JSONObject,
     * parse it into the fields of a NewsItem and add it to an ArrayList
     */
    private static List<NewsItem> extractFeatureFromJson(String inputJSON) {
        if (TextUtils.isEmpty((inputJSON))) {
            return null;
        }

        List<NewsItem> newsArrayList = new ArrayList<>();

        try {
            Log.e(LOG_TAG, "Attempting JSON parsing");

            JSONObject rootJSON = new JSONObject(inputJSON);
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
            }
        } catch (JSONException error) {
            Log.e(LOG_TAG, "there was a JSON parsing error", error);
        }
        return newsArrayList;
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
