package com.antym.popularmovies2;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

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
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(movie.getOriginalTitle());
            }
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

            //Load movie title into textview
            TextView title = (TextView) context.findViewById(R.id.movie_detail);
            if (title == null) {
                Log.e(TAG, "Title null");
            }

            title.setText(movie.getOriginalTitle());

            //Assign button to variable
            final ToggleButton button = (ToggleButton) context.findViewById(R.id.mark_as_favorite);

            isFav = fc.isFavorite(movie.getId());

//            if (isFav) {
//                button.setChecked(true);
//            }
//            else {
//                button.setChecked(false);
//            }

//            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                public void onCheckedChanged(CompoundButton button, boolean isChecked) {
//                    //Toast "toggling favorite: on/off"
//                    Context context = context;
//                    CharSequence text = movie.getId();
//                    int duration = Toast.LENGTH_SHORT;
//
//                    if (isChecked) {
//                        // The toggle is enabled
//                        //set favorite
//                        fc.setFavorite(movie.getId());
//                        button.setChecked(true);
//                        text = "Set favorite";
//                        Toast.makeText(context, text, duration).show();
//                    } else {
//                        // The toggle is disabled
//                        //unset favorite
//                        fc.unSetFavorite(movie.getId());
//                        button.setChecked(false);
//                        text = "Unset favorite";
//                        Toast.makeText(context, text, duration).show();
//                    }
//                }
//            });

            //Load movie poster into imageview
            if (getActivity() == null) {
                Log.e(TAG, "Null getActivity");
            }

            Picasso.with(getActivity())
                    .load(movie.getPosterURL())
                    .into((ImageView) context.findViewById(R.id.imageMoviePoster));

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
        }

    }
}
