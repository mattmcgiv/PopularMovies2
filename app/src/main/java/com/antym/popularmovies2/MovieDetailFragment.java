package com.antym.popularmovies2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private Movie movie;
    private static final String TAG = "MovieDetailFragment";
    private boolean isFav;
    private FavoriteController fc;


    public MovieDetailFragment() {
    }

    /*public MovieDetailFragment(Movie movie) {
        this.movie = movie;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            this.movie = (Movie) getArguments().getSerializable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
           // CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle("Detail");
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View context = getView();

        if (this.movie != null) {

            fc = new FavoriteController(this.getActivity());

//            //Load movie title into textview
//            TextView title = (TextView) context.findViewById(R.id.movie_detail);
//            if (title == null) {
//                Log.e(TAG, "Title null");
//            }
//
//            title.setText(movie.getOriginalTitle());

            //Assign button to variable
            final ToggleButton button = (ToggleButton) context.findViewById(R.id.mark_as_favorite);

            isFav = fc.isFavorite(movie.getId());

            if (isFav) {
                button.setChecked(true);
            }
            else {
                button.setChecked(false);
            }

            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                    //Toast "toggling favorite: on/off"
                    Context context = getActivity();
                    CharSequence text = movie.getId();
                    int duration = Toast.LENGTH_SHORT;

                    if (isChecked) {
                        // The toggle is enabled
                        //set favorite
                        fc.setFavorite(movie.getId());
                        button.setChecked(true);
                        text = "Set favorite";
                        Toast.makeText(context, text, duration).show();
                    } else {
                        // The toggle is disabled
                        //unset favorite
                        fc.unSetFavorite(movie.getId());
                        button.setChecked(false);
                        text = "Unset favorite";
                        Toast.makeText(context, text, duration).show();
                    }
                }
            });

            //Load movie poster into imageview
            if (getActivity() == null) {
                Log.e(TAG, "Null getActivity");
            }

            String posterUrl = movie.getPosterURL();

            ImageView iv = (ImageView) context.findViewById(R.id.imageMoviePoster);

            Picasso.with(getActivity())
                    .load(posterUrl)
                    .into(iv);

            //Load vote average
            TextView rating = (TextView) context.findViewById(R.id.textRating);
            rating.setText(movie.getVoteAverage() + "/10");

            //Load number of votes
            TextView votes = (TextView) context.findViewById(R.id.textNumVotes);
            votes.setText("Rating (" + movie.getVoteCount() + " votes)");

            //Load release date
            TextView releaseDate = (TextView) context.findViewById(R.id.releaseDate);
            releaseDate.setText(movie.getReleaseDate());

            //Load overview
            TextView overview = (TextView) context.findViewById(R.id.overview);
            overview.setText(movie.getOverview());

            new TrailerRetriever(getActivity()).execute(movie.getId());
            new ReviewRetriever(getActivity()).execute(movie.getId());
        }

    }

    public void launchYoutubeTrailer(String id) {
        //launch web activity
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=" + id)));
    }

    public class TrailerRetriever extends AsyncTask<String, Void, com.antym.popularmovies2.MovieTrailers> {
        protected Context context;

        public TrailerRetriever(Context c) {
            this.context = c;
        }

        protected MovieTrailers doInBackground(String... movieIds) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // MovieTrailers object for results
            MovieTrailers mts = new MovieTrailers();

            // Will contain the raw JSON response as a string.
            String result = null;
            String scheme = "https";
            String apiUrlBase = "api.themoviedb.org";
            String apiVersion = "3";
            String apiUrlVerb = "movie";
            String movieId = movieIds[0];
            String apiUrlEndpoint = "videos";
            String api_key = MovieListActivity.API_KEY;

            //https://api.themoviedb.org/3/movie/286217/videos?api_key={{api_key}}
            //scheme + apiUrlBase + apiVersion + apiURLVerb + movieID + apiUrlEndpoint
            try {
                // Construct the URL for the query
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(scheme)
                        .authority(apiUrlBase)
                        .appendPath(apiVersion)
                        .appendPath(apiUrlVerb)
                        .appendPath(movieId)
                        .appendPath(apiUrlEndpoint)
                        .appendQueryParameter("api_key", api_key);

                String stringUrl = builder.build().toString();
                URL url = new URL(stringUrl);

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    result = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    result = null;
                }
                result = buffer.toString();
                try {
                    //TODO:remove debug
                    Log.d(TAG, "Trailers returned " + this.getNumResults(result) + " results."
                            + "\nThey were: " + result);
                    for (int i = 0; i < this.getNumResults(result); i++) {
                        JSONObject jo = getTrailerJSON(result, i);
                        //only YouTube trailers for now
                        if (jo.getString("site").equals("YouTube")) {
                            MovieTrailer mt = getTrailerFromJson(jo);
                            Log.d(TAG, "Trailer created:: title:'" + mt.get_title()
                                    + "' id:'" + mt.get_youtube_id() + "'");
                            mts.addTrailer(mt);
                        }
                    }
                } catch (JSONException je){
                    Log.e(TAG, "JSON error while trying to get poster path: " + je.getMessage());
                }

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                result = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            if (result == null) {
                return null;
            }
            else {
                return mts;
            }
        }

        protected void onPostExecute(com.antym.popularmovies2.MovieTrailers mt) {
            com.antym.popularmovies2.MovieTrailers mts = mt;
            TrailerAdapter adapter = new TrailerAdapter(this.context,
                    R.layout.trailer_title, mt);
            ListView listView = (ListView) getActivity().findViewById(R.id.trailer_list_view);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //ArrayList<com.antym.popularmovies2.MovieTrailer> al = mts.getTrailers();
                    //launchYoutubeTrailer(al.get(position).get_youtube_id());
                }
            });
        }

        protected int getNumResults(String movieJsonString) throws JSONException {
            JSONObject results = new JSONObject(movieJsonString);
            JSONArray movies = results.getJSONArray("results");
            return movies.length();
        }

        protected JSONObject getTrailerJSON(String trailerJsonString, int trailerIndex) throws JSONException{
            JSONObject results = new JSONObject(trailerJsonString);
            JSONArray trailers = results.getJSONArray("results");
            JSONObject trailer = trailers.getJSONObject(trailerIndex);
            return trailer;
        }

        protected com.antym.popularmovies2.MovieTrailer getTrailerFromJson(JSONObject trailer) throws JSONException {
            String id = trailer.getString("key");
            String title = trailer.getString("name");
            com.antym.popularmovies2.MovieTrailer mt = new com.antym.popularmovies2.MovieTrailer(id, title);
            return mt;
        }

    }

    public class ReviewRetriever extends AsyncTask<String, Void, ArrayList<Review>> {
        protected Context context;

        public ReviewRetriever(Context c) {
            this.context = c;
        }

        protected ArrayList<Review> doInBackground(String... movieIds) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // MovieTrailers object for results
            ArrayList<Review> reviews = new ArrayList<Review>();

            // Will contain the raw JSON response as a string.
            String result = null;
            String scheme = "https";
            String apiUrlBase = "api.themoviedb.org";
            String apiVersion = "3";
            String apiUrlVerb = "movie";
            String movieId = movieIds[0];
            String apiUrlEndpoint = "reviews";
            String api_key = MovieListActivity.API_KEY;

            try {
                // Construct the URL for the query
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(scheme)
                        .authority(apiUrlBase)
                        .appendPath(apiVersion)
                        .appendPath(apiUrlVerb)
                        .appendPath(movieId)
                        .appendPath(apiUrlEndpoint)
                        .appendQueryParameter("api_key", api_key);

                String stringUrl = builder.build().toString();
                URL url = new URL(stringUrl);

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    result = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    result = null;
                }
                result = buffer.toString();
                try {
                    //TODO:remove debug
                    Log.d(TAG, "Reviews returned " + this.getNumResults(result) + " results."
                            + "\nThey were: " + result);
                    for (int i = 0; i < this.getNumResults(result); i++) {
                        JSONObject jo = getTrailerJSON(result, i);
                        //TODO this needs to be specific to reviews (not trailers)

                        Review review = getReviewFromJson(jo);
                        Log.d(TAG, "Review created:: author:'" + review.getAuthor()
                                + "' content:'" + review.getContent() + "'");
                        reviews.add(review);
                    }
                } catch (JSONException je){
                    Log.e(TAG, "JSON error while trying to get poster path: " + je.getMessage());
                }

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                result = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            if (result == null) {
                return null;
            }
            else {
                return reviews;
            }

        }

        protected int getNumResults(String movieJsonString) throws JSONException {
            JSONObject results = new JSONObject(movieJsonString);
            JSONArray movies = results.getJSONArray("results");
            return movies.length();
        }

        protected JSONObject getTrailerJSON(String trailerJsonString, int trailerIndex) throws JSONException{
            JSONObject results = new JSONObject(trailerJsonString);
            JSONArray trailers = results.getJSONArray("results");
            JSONObject trailer = trailers.getJSONObject(trailerIndex);
            return trailer;
        }

        protected Review getReviewFromJson(JSONObject reviewJson) throws JSONException {
            String author = reviewJson.getString("author");
            String content = reviewJson.getString("content");
            Review review = new Review(author, content);
            return review;
        }

        protected void onPostExecute(ArrayList<Review> reviews) {
            Log.d(TAG, "Reached onPostExecute for reviews.");

            Log.d(TAG, "onPostExecute, reviews: " + reviews);
            if (reviews.isEmpty()) {
                reviews.add(new Review("No reviews available.",""));
            }
            ReviewAdapter adapter = new ReviewAdapter(this.context,
                    R.layout.trailer_title, reviews);
            ListView listView = (ListView) getActivity().findViewById(R.id.review_list_view);
            listView.setAdapter(adapter);

        }
    }
}
