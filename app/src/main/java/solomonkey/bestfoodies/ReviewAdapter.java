package solomonkey.bestfoodies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by arvin on 10/1/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{

    private Context mContext;
    private List<Reviews> reviewsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_datetime,txt_title,txt_rating, txt_decription;

        public MyViewHolder(View view) {
            super(view);
            txt_datetime = (TextView) view.findViewById(R.id.txtReview_datetime);
            txt_rating = (TextView) view.findViewById(R.id.txtreview_stars);
            txt_title = (TextView) view.findViewById(R.id.txtReview_title);
            txt_decription = (TextView) view.findViewById(R.id.txtreview_description);
        }
    }

    public ReviewAdapter(Context mContext, List<Reviews> reviewsList) {
        this.mContext = mContext;
        this.reviewsList = reviewsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Reviews reviews = reviewsList.get(position);
        holder.txt_datetime.setText(reviews.getDatetime());
        holder.txt_title.setText(reviews.getTitle());
        holder.txt_decription.setText(reviews.getDescription());
        int rating = reviews.getRating();
        String star =" star";
        if(rating>1){
            star+="s";
        }
        holder.txt_rating.setText(rating+star);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

}
