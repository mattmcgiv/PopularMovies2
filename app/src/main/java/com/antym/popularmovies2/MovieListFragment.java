package com.antym.popularmovies2;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;

/**
 * A list fragment representing a list of Movies. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link MovieDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MovieListFragment extends Fragment {

    public static final String TAG = "MovieListFragment";
    public static MovieManager movieManager;
    public static ArrayList<String> movPosterURLStubs = new ArrayList<>();
    public static ArrayList<String> movPosterURLs = new ArrayList<>();
    public ImageAdapter imageAdapter;
    public GridView gridView;
    public enum SortOrder {
        POPULARITY, RATING, FAVORITES
    }

    static SortOrder sort = SortOrder.POPULARITY;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Movie movie) {
            Log.d(TAG, "onItemSelected: movie is: " + movie);
        }
    };

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieListFragment() {
    }

    public void refreshData(ImageAdapter2 ia) {
        this.gridView.setAdapter(ia);
        this.imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gridview, container, true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        movieManager = new MovieManager(getActivity());
        imageAdapter = new ImageAdapter(getActivity(), movieManager);
        new PopularMovieRetriever(getActivity(), imageAdapter).execute();
        new FavoriteMovieRetriever(getActivity(), imageAdapter).execute();
        this.gridView = (GridView) getView();
        if (this.gridView != null) {
        }
        else {
            Log.e(TAG, "Null gridView in onActivityCreated");
        }
        this.gridView.setAdapter(this.imageAdapter);
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //TODO: need to pass on to detail fragment

                //Log.d(TAG, "onItemClick: id is: " + movieManager.getMovie(position));
                mCallbacks.onItemSelected(movieManager.getMovie(position));
                //Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                //intent.putExtra(MMM_ID, imageAdapter.movieManager.getMovie(position));
                //startActivity(intent);

            }
        });
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sort == SortOrder.POPULARITY) {
                    Log.d("SORT", "Popularity");
                    sort = SortOrder.RATING;
                } else if (sort == SortOrder.RATING) {
                    Log.d("SORT", "Rating");
                    sort = SortOrder.FAVORITES;
                } else if (sort == SortOrder.FAVORITES) {
                    Log.d("SORT", "Favorites");
                    sort = SortOrder.POPULARITY;
                }
                Snackbar.make(view, "Resorting by: " + sort, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                //TODO:: this is next...filter by favorites
                new PopularMovieRetriever(getActivity(), imageAdapter).execute();
            }
        });
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Restore the previously serialized activated item position.
//        if (savedInstanceState != null
//                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
//            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

//    @Override
//    public void onListItemClick(ListView listView, View view, int position, long id) {
//        super.onListItemClick(listView, view, position, id);
//
//        // Notify the active callbacks interface (the activity, if the
//        // fragment is attached to one) that an item has been selected.
//        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

//    /**
//     * Turns on activate-on-click mode. When this mode is on, list items will be
//     * given the 'activated' state when touched.
//     */
//    public void setActivateOnItemClick(boolean activateOnItemClick) {
//        // When setting CHOICE_MODE_SINGLE, ListView will automatically
//        // give items the 'activated' state when touched.
//        getListView().setChoiceMode(activateOnItemClick
//                ? ListView.CHOICE_MODE_SINGLE
//                : ListView.CHOICE_MODE_NONE);
//    }
//
//    private void setActivatedPosition(int position) {
//        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
//
//        mActivatedPosition = position;
//    }

    public class FavoriteMovieRetriever extends AsyncTask<Void,Void,ImageAdapter2> {
        public Activity activity;
        private ImageAdapter2 ia;

        public FavoriteMovieRetriever(Activity a, ImageAdapter ia) {
            this.activity = a;
            this.ia = new ImageAdapter2(this.activity, movieManager);
        }

        protected ImageAdapter2 doInBackground(Void... voids) {
            FavoriteController fc = new FavoriteController(this.activity);
            ArrayList<String> favList = fc.getAllFavorites();

            for (int i = 0; i < favList.size(); i++) {
                //query API
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                //https://api.themoviedb.org/3/movie/{{id}}?api_key={{your_key}}
                String apiResponseJsonStr = null;
                String apiUrlBase = "api.themoviedb.org";
                String apiVersion = "3";
                String apiUrlEndpoint = "movie";
                String apiMovieId = favList.get(i);
                String api_key = MovieListActivity.API_KEY;

                try {
                    // Construct the URL for the query
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority(apiUrlBase)
                            .appendPath(apiVersion)
                            .appendPath(apiUrlEndpoint)
                            .appendPath(apiMovieId)
                            .appendQueryParameter("api_key", api_key);
                    String stringUrl = builder.build().toString();
                    Log.d(TAG, stringUrl);
                    URL url = new URL(stringUrl);

                    // Create the request, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //parse response as Movie object
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        apiResponseJsonStr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        apiResponseJsonStr = null;
                    }
                    apiResponseJsonStr = buffer.toString();
                    Log.d(TAG, apiResponseJsonStr);
                    try {
                        movPosterURLStubs.clear();
                        this.ia.movieManager.clear();
//                        for (int i = 0; i < numResults; i++) {
                        //Build a list of poster paths
                        String myPosterPath = getPosterPath(apiResponseJsonStr, i);
                        Movie currentMovie = new Movie();

                        //Set movie object's values
                        currentMovie.setId(getValue(apiResponseJsonStr, i, "id"));
                        currentMovie.setBackdropPath(getValue(apiResponseJsonStr, i, "backdrop_path"));
                        //currentMovie.setBackdropUrl(currentMovie.getBackdropPath());
                        currentMovie.setOriginalTitle(getValue(apiResponseJsonStr, i, "original_title"));
                        currentMovie.setOverview(getValue(apiResponseJsonStr, i, "overview"));
                        currentMovie.setReleaseDate(getValue(apiResponseJsonStr, i, "release_date"));
                        currentMovie.setPosterPath(getValue(apiResponseJsonStr, i, "poster_path"));
                        currentMovie.setPopularity(Double.parseDouble(getValue(apiResponseJsonStr, i, "popularity")));
                        currentMovie.setVoteAverage(Double.parseDouble(getValue(apiResponseJsonStr, i, "vote_average")));
                        currentMovie.setVoteCount(Integer.parseInt(getValue(apiResponseJsonStr, i, "vote_count")));

                        movPosterURLStubs.add(myPosterPath);
                        String movPostURL;
                        movPostURL = getMoviePosterURL(myPosterPath);
                        currentMovie.setPosterURL(movPostURL);
                        movPosterURLs.add(movPostURL);

                        //add movie object to movie manager
                        this.ia.movieManager.addMovie(currentMovie);

//                        }
                    } catch (JSONException je){
                        Log.e(TAG, "JSON error while trying to get poster path: " + je.getMessage());
                    }



                } catch (IOException e) {
                    Log.e(TAG, "Error ", e);
                    // If the code didn't successfully get the data, there's no point in attempting
                    // to parse it.
                    apiResponseJsonStr = null;
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
                return this.ia;

                //add Movie to static movie manager
            }

            return this.ia;
        }
    }

    public class PopularMovieRetriever extends AsyncTask<Void,Void,ImageAdapter2> {

        public Activity activity;
        private ImageAdapter2 ia;

        public PopularMovieRetriever(Activity a, ImageAdapter ia) {
            this.activity = a;
            this.ia = new ImageAdapter2(this.activity, movieManager);
        }

        protected ImageAdapter2 doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //TODO add the sort order here
            // Will contain the raw JSON response as a string.
            String apiResponseJsonStr = null;
            String apiUrlBase = "api.themoviedb.org";
            String apiVersion = "3";
            String apiUrlVerb = "discover";
            String apiUrlEndpoint = "movie";
            String api_key = MovieListActivity.API_KEY;

            //Optional params
            String sort_by;
            String voteMinimum = "1";

            if (sort == SortOrder.POPULARITY) {
                sort_by = "popularity.desc";
            }
            else if (sort == SortOrder.RATING) {
                sort_by = "vote_average.desc";
                //require at least 1000 votes to keep
                //obscure movies out of results
                voteMinimum = "1000";
            }
            //TODO update this
            else {
                sort_by = "vote_average.asc";
            }

            //https://api.themoviedb.org/3/discover/movie?api_key=API_KEY&sort_by=popularity.desc
            try {
                // Construct the URL for the query
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority(apiUrlBase)
                        .appendPath(apiVersion)
                        .appendPath(apiUrlVerb)
                        .appendPath(apiUrlEndpoint)
                        .appendQueryParameter("api_key", api_key)
                        .appendQueryParameter("sort_by", sort_by)
                        .appendQueryParameter("vote_count.gte",voteMinimum);
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
                    apiResponseJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    apiResponseJsonStr = null;
                }
                apiResponseJsonStr = buffer.toString();

                try {
                    int numResults = getNumResults(apiResponseJsonStr);
                    movPosterURLStubs.clear();
                    this.ia.movieManager.clear();
                    for (int i = 0; i < numResults; i++) {
                        //Build a list of poster paths
                        String myPosterPath = getPosterPath(apiResponseJsonStr, i);
                        Movie currentMovie = new Movie();

                        //Set movie object's values
                        currentMovie.setId(getValue(apiResponseJsonStr, i, "id"));
                        currentMovie.setBackdropPath(getValue(apiResponseJsonStr, i, "backdrop_path"));
                        //currentMovie.setBackdropUrl(currentMovie.getBackdropPath());
                        currentMovie.setOriginalTitle(getValue(apiResponseJsonStr, i, "original_title"));
                        currentMovie.setOverview(getValue(apiResponseJsonStr, i, "overview"));
                        currentMovie.setReleaseDate(getValue(apiResponseJsonStr, i, "release_date"));
                        currentMovie.setPosterPath(getValue(apiResponseJsonStr, i, "poster_path"));
                        currentMovie.setPopularity(Double.parseDouble(getValue(apiResponseJsonStr, i, "popularity")));
                        currentMovie.setVoteAverage(Double.parseDouble(getValue(apiResponseJsonStr, i, "vote_average")));
                        currentMovie.setVoteCount(Integer.parseInt(getValue(apiResponseJsonStr, i, "vote_count")));

                        movPosterURLStubs.add(myPosterPath);
                        String movPostURL;
                        movPostURL = getMoviePosterURL(myPosterPath);
                        currentMovie.setPosterURL(movPostURL);
                        movPosterURLs.add(movPostURL);

                        //add movie object to movie manager
                        this.ia.movieManager.addMovie(currentMovie);

                    }
                } catch (JSONException je){
                    Log.e(TAG, "JSON error while trying to get poster path: " + je.getMessage());
                }



            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                apiResponseJsonStr = null;
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
            return this.ia;
        }

        protected void onPostExecute(ImageAdapter2 ia){
            MovieListFragment listFrag = (MovieListFragment) getFragmentManager()
                    .findFragmentById(R.id.movie_list);
            if (listFrag != null) {
                listFrag.refreshData(ia);
            } else {
                Log.e(TAG, "listFrag.refreshData(NULL)");
            }
        }

        protected String getPosterPath(String movieJsonString, int movieIndex) throws JSONException{
            JSONObject movie = getMovieJSON(movieJsonString, movieIndex);
            String posterPath = movie.getString("poster_path");
            return posterPath;
        }

        protected String getValue(String movieJsonString, int movieIndex, String key) throws JSONException{
            JSONObject movie = getMovieJSON(movieJsonString,movieIndex);
            String val = movie.getString(key);
            return val;
        }

        protected JSONObject getMovieJSON(String movieJsonString, int movieIndex) throws JSONException{
            JSONObject results = new JSONObject(movieJsonString);
            JSONArray movies = results.getJSONArray("results");
            JSONObject movie = movies.getJSONObject(movieIndex);
            return movie;
        }

        protected int getNumResults(String movieJsonString) throws JSONException {
            JSONObject results = new JSONObject(movieJsonString);
            JSONArray movies = results.getJSONArray("results");
            return movies.length();
        }
    }

    public String getMoviePosterURL(String stub) throws MalformedURLException {
        String apiUrlBase = "image.tmdb.org";
        String apiUrl0 = "t";
        String apiUrl1 = "p";
        String apiUrl2 = "w185";
        String api_key = MovieListActivity.API_KEY;
        String posterPath = stub;

        // Construct the URL for the query
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(apiUrlBase)
                .appendPath(apiUrl0)
                .appendPath(apiUrl1)
                .appendPath(apiUrl2)
                .appendEncodedPath(posterPath)
                .appendQueryParameter("api_key", api_key);

        String stringUrl = builder.build().toString();
        URL url = new URL(stringUrl);
        return url.toString();
    }

    /**
     * Created by matthewmcgivney on 8/22/15.
     */
    public static class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public MovieManager movieManager;
        public ImageAdapter(Context c, MovieManager m) {
            if (c == null) {
                Log.e(TAG, "Null context passed to ImageAdapter");
                this.mContext = c;
            }
            else {
                this.mContext = c;
            }
            if (m == null) {
                Log.e(TAG, "Null MovieManager passed to ImageAdapter");
                this.movieManager = m;
            }
            else {
                this.movieManager = m;
            }

        }

        public int getCount() {
            return movieManager.getNumMovies();
        }

        public Object getItem(int position) {
            return movieManager.getMovie(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(this.mContext);
                imageView.setAdjustViewBounds(true);
                //imageView.setLayoutParams(new GridView.LayoutParams(85,85));
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setPadding(8,8,8,8);
            } else {
                imageView = (ImageView) convertView;
            }

            String finalURL = movieManager.getMovie(position).getPosterURL();
            Picasso.with(this.mContext).load(finalURL).resize(185,277).into(imageView);
            //imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7
        };
    }
    public static class ImageAdapter2 extends BaseAdapter {
        private Context mContext;
        public MovieManager movieManager;
        public ImageAdapter2(Context c, MovieManager m) {
            if (c == null) {
                Log.e(TAG, "Null context passed to ImageAdapter");
                this.mContext = c;
            }
            else {
                this.mContext = c;
            }
            if (m == null) {
                Log.e(TAG, "Null MovieManager passed to ImageAdapter");
                this.movieManager = m;
            }
            else {
                this.movieManager = m;
            }

        }

        public int getCount() {
            return movieManager.getNumMovies();
            //return mThumbIds.length;
        }

        public Object getItem(int position) {
            return movieManager.getMovie(position);
            //return null;
        }

        public long getItemId(int position) {
            return position;
            //return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(this.mContext);

                //imageView.setLayoutParams(new GridView.LayoutParams(85,85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(4, 2, 4, 2);

                imageView.setAdjustViewBounds(true);
            } else {
                imageView = (ImageView) convertView;
            }

            String finalURL = movieManager.getMovie(position).getPosterURL();
            Picasso.with(this.mContext).load(finalURL).resize(185,277).into(imageView);
//            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.negate_2, R.drawable.negate_3,
                R.drawable.negate_4, R.drawable.negate_5,
                R.drawable.negate_6, R.drawable.negate_3,
                R.drawable.negate_0, R.drawable.negate_1,
                R.drawable.negate_2, R.drawable.negate_3,
                R.drawable.negate_4, R.drawable.negate_5,
                R.drawable.negate_6, R.drawable.negate_3,
                R.drawable.negate_0, R.drawable.negate_1,
                R.drawable.negate_2, R.drawable.negate_3,
                R.drawable.negate_4, R.drawable.negate_5,
                R.drawable.negate_6, R.drawable.negate_3
        };
    }

}
