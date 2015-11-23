package com.antym.popularmovies2;

/**
 * Created by matthewmcgivney on 11/23/15.
 */
public class Review {
    protected String author;
    protected String content;

    public Review(String author, String content) {
        this.setAuthor(author);
        this.setContent(content);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
