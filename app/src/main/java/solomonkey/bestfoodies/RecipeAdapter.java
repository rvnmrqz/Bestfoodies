package solomonkey.bestfoodies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import static solomonkey.bestfoodies.R.id.imageView;

/**
 * Created by arvin on 10/1/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder>{

    private Context mContext;
    private List<Recipes> recipesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView recipe_id,recipename, ratings;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            recipe_id = (TextView) view.findViewById(R.id.card_recipe_id);
            recipename = (TextView) view.findViewById(R.id.card_recipeName);
            ratings = (TextView) view.findViewById(R.id.card_rating);
            thumbnail = (ImageView) view.findViewById(R.id.card_thumbnail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TempHolder.selectedRecipeID = recipe_id.getText().toString();
                    MainActivity.changeBackstack(true,new Fragment_Recipe_Opened(),"Recipe");
                    Log.wtf("Clicked Recipe ID",TempHolder.selectedRecipeID);
                }
            });
        }

    }

    public RecipeAdapter(Context mContext, List<Recipes> recipesList) {
        this.mContext = mContext;
        this.recipesList = recipesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Recipes recipes = recipesList.get(position);
        holder.recipe_id.setText(recipes.getRecipe_id());
        holder.recipename.setText(recipes.getRecipe_name());
        holder.ratings.setText(recipes.getRating()+"");
        // loading online image
        Log.wtf("Image URL",recipes.getThumbnailLink());
        Picasso.with(mContext).load(recipes.getThumbnailLink()).resize(300,300).placeholder(R.drawable.no_image).into(holder.thumbnail);
       //Glide.with(mContext).load(recipes.getThumbnailLink()).placeholder(R.drawable.no_image).dontAnimate().fitCenter().into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return recipesList.size();
    }

}
