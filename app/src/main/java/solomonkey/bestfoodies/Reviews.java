package solomonkey.bestfoodies;

/**
 * Created by arvin on 10/4/2017.
 */

public class Reviews {

    private String datetime;
    private String title;
    private String description;
    private int rating;

    public Reviews(String datetime, String title, String description, int rating) {
        this.datetime = datetime;
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
