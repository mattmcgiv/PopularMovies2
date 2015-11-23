package com.antym.popularmovies2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by matthewmcgivney on 11/23/15.
 */
public class ReviewAdapter extends ArrayAdapter{

    private Context context;
    private int mResource;
    private ArrayList<Review> reviews;

    public ReviewAdapter(Context context, int resource, ArrayList<Review> reviews) {
        super(context, resource, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get review data
        Review rvw = this.reviews.get(position);
        String author = rvw.getAuthor();
        String content = rvw.getContent();

        //add author name to TextView
        TextView authorTextView = new TextView(this.context);
        authorTextView.setText(this.reviews.get(position).getAuthor());

        //add content to another TextView
        TextView contentTextView = new TextView(this.context);
        contentTextView.setText(this.reviews.get(position).getContent());

        //
        LinearLayout ll = new LinearLayout(this.context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(authorTextView);
        ll.addView(contentTextView);

        return ll;
    }
}
