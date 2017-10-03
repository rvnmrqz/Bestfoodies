package solomonkey.bestfoodies;

/**
 * Created by arvin on 10/1/2017.
 */

public class Recipes {
    private String recipe_id;
    private String recipe_name;
    private String ingredients;
    private String procedures;
    private double rating;
    private String reviews;
    private String thumbnailLink;
    private String videoLink;


    public Recipes(String recipe_id, String recipe_name, String ingredients, String procedures, double rating, String reviews, String thumbnailLink, String videoLink) {
        this.recipe_id = recipe_id;
        this.recipe_name = recipe_name;
        this.ingredients = ingredients;
        this.procedures = procedures;
        this.rating = rating;
        this.reviews = reviews;
        this.thumbnailLink = thumbnailLink;
        this.videoLink = videoLink;
    }


    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getProcedures() {
        return procedures;
    }

    public void setProcedures(String procedures) {
        this.procedures = procedures;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String toString(){
         return recipe_id+" "+recipe_name+" "+ingredients;
    }
}
