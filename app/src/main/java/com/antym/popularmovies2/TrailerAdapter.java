package com.antym.popularmovies2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by matthewmcgivney on 11/22/15.
 */
public class TrailerAdapter extends ArrayAdapter {

    private Context context;
    private int mResource;
    private MovieTrailers movieTrailers;

    public TrailerAdapter(Context context, int resource, MovieTrailers movieTrailers) {
        super(context, resource, movieTrailers.getTrailers());
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //build a linearlayout containing an imageview and a text view
        ImageView iv = new ImageView(this.context);
        //TODO replace this
        //iv.setImageResource(R.drawable.ic_play_arrow_black_48dp);

        TextView tv = new TextView(this.context);
        MovieTrailer mt = (MovieTrailer) this.getItem(position);
        String title = mt.get_title();
        tv.setText(title);
        LinearLayout ll = new LinearLayout(this.context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(iv);
        ll.addView(tv);

        return ll;
    }
}
