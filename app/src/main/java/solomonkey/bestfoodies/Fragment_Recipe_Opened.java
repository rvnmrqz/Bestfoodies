package solomonkey.bestfoodies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.Rating;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Fragment_Recipe_Opened extends Fragment{

    Context context;
    public static Context staticContext;
    //layouts
    ScrollView layout_recipe;
    LinearLayout layout_loading,layout_message;

    //message layout views
    TextView layout_message_text;
    Button layout_message_button;

    //recipe layout views
    YouTubePlayerSupportFragment youTubePlayerSupportFragment;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    //recipe
    TextView txt_RecipeName,txt_recipe_ingredients,txt_recipe_procedure,txt_recipe_reviewcount,txt_reference;
    ImageView img_recipe_finalimage;
    RatingBar ratingBar;
    String link;
    String reference;

    //rating
    Dialog dialog;
    android.app.AlertDialog.Builder builder;
    TextView txtReviewRecipe;
    int review_value = 0;

    LinearLayout layout_writereview;
    LinearLayout layout_reviews;
    LinearLayout layout_review_message;
    ProgressBar resultMessagelayout_progress;
    TextView resultMessagelayout_text;

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<Reviews> reviewsList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init here
        layout_recipe = (ScrollView) getActivity().findViewById(R.id.loaded_Recipelayout);
        layout_loading = (LinearLayout) getActivity().findViewById(R.id.loadinglayout);
        layout_message = (LinearLayout) getActivity().findViewById(R.id.messagelayout);
        layout_message_text = (TextView) getActivity().findViewById(R.id.messagelayout_textview);
        layout_message_button = (Button) getActivity().findViewById(R.id.messagelayout_button);
        txt_RecipeName = (TextView) getActivity().findViewById(R.id.txt_RecipeName);
        txt_recipe_ingredients = (TextView) getActivity().findViewById(R.id.txt_recipe_ingredients);
        txt_recipe_procedure = (TextView) getActivity().findViewById(R.id.txt_recipe_procedure);
        txt_recipe_reviewcount = (TextView) getActivity().findViewById(R.id.txt_recipe_reviewcount);
        img_recipe_finalimage = (ImageView) getActivity().findViewById(R.id.img_recipe_finalimage);
        ratingBar = (RatingBar) getActivity().findViewById(R.id.ratingbar);
        txtReviewRecipe = (TextView) getActivity().findViewById(R.id.txtWrite_review);
        txt_reference = (TextView) getActivity().findViewById(R.id.txt_reference);

        youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_fragment);

        txtReviewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewDialog();
            }
        });

        txt_recipe_reviewcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loadReviews();
            }
        });

        txt_reference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reference!=null){
                    if(reference.length()>4){
                        //not null
                        try{
                             new AlertDialog.Builder(context)
                                    .setTitle("Confirmation")
                                    .setMessage("Link will open externally, continue?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(reference)));
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();


                        }catch (Exception e){
                            Toast.makeText(context, "Can't view reference", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(context, "Sorry, No reference given", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Sorry, No reference given", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadRecipe();
        ratingbarListener();
        MainActivity.searchItem.setVisible(false);
    }

    protected void ratingbarListener(){
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.wtf("Ratingbar",""+rating);
            }
        });
    }
    protected void loadRecipe(){
        showLoadingLayout();
        Log.wtf("loadRecipe","loadRecipe called");
            final String server_url = TempHolder.HOST_ADDRESS+"/get_data.php";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response!=null){
                                try{
                                    Log.wtf("onResponse","Response:" +response);
                                    //clear the list in the UI

                                    String thumbnailfile,videolink;
                                    double rating;

                                    JSONObject object = new JSONObject(response);
                                    JSONArray Jarray  = object.getJSONArray("mydata");
                                    if(Jarray.length()>0) {

                                        JSONObject Jasonobject = Jarray.getJSONObject(0);
                                        rating = Jasonobject.getDouble("rating");
                                        ratingBar.setRating((float)rating);
                                        txt_RecipeName.setText(Jasonobject.getString("name"));
                                        txt_recipe_ingredients.setText(Jasonobject.getString("ingredients"));
                                        txt_recipe_procedure.setText(Jasonobject.getString("procedures"));
                                        txt_recipe_reviewcount.setText(Jasonobject.getString("reviews")+" review(s)");
                                        reference = Jasonobject.getString("reference");
                                        thumbnailfile = TempHolder.HOST_ADDRESS+"/images/"+Jasonobject.getString("imagefilename");
                                        Picasso.with(context).load(thumbnailfile).error(R.drawable.no_image).into(img_recipe_finalimage);
                                        link = Jasonobject.getString("videolink");
                                        prepareVideo();
                                        showRecipeLayout();
                                    }else{
                                        showMesageLayout("No Recipe to display");
                                    }
                                }catch (Exception ee)
                                {
                                    showMesageLayout("An Error Occurred");
                                    Log.wtf("loadRecipe ERROR (onResponse)",ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            showMesageLayout(message);
                            Log.wtf("loadRecipe: onErrorResponse","Volley Error \n"+message);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String query = "SELECT re.*,coalesce(count(id),0) reviews,coalesce(avg(rating),0) rating from tbl_recipes re LEFT JOIN tbl_ratings ra ON re.recipe_id = ra.recipe_id WHERE re.recipe_id = "+TempHolder.selectedRecipeID+" GROUP BY ra.recipe_id ;";
                    params.put("qry",query);
                    Log.wtf("loadRecipe","Map<> Query: "+query);
                    return params;
                }
            };
            int socketTimeout = TempHolder.TIME_OUT;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            request.setShouldCache(false);
            requestQueue.add(request);
    }
    protected String getVolleyError(VolleyError volleyError){
        String message="";
        if (volleyError instanceof NetworkError) {
            message = "Network Error Encountered";
            Log.wtf("getVolleyError (Volley Error)","NetworkError");
        } else if (volleyError instanceof ServerError) {
            message = "Please check your internet connection";
            Log.wtf("getVolleyError (Volley Error)","ServerError");
        } else if (volleyError instanceof AuthFailureError) {
            message = "Please check your internet connection";
            Log.wtf("getVolleyError (Volley Error)","AuthFailureError");
        } else if (volleyError instanceof ParseError) {
            message = "An error encountered, Please try again";
            Log.wtf("getVolleyError (Volley Error)","ParseError");
        } else if (volleyError instanceof NoConnectionError) {
            message = "No internet connection";
            Log.wtf("getVolleyError (Volley Error)","NoConnectionError");
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection Timeout";
            Log.wtf("getVolleyError (Volley Error)","TimeoutError");
        }
        return message;
    }
    protected void prepareVideo(){
        try{
            Log.wtf("prepareVideo","Link: "+link);
            onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.setShowFullscreenButton(false);
                    youTubePlayer.loadVideo(link);
                }
                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };
            youTubePlayerSupportFragment.initialize(PlayerConfig.API_KEY,onInitializedListener);
        }catch (Exception e){
            Log.wtf("prepareVideo","Exception "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("prepareVideo","Throwable "+t.getMessage());
        }
    }

    private void showReviewDialog(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        review_value = 5;
        View view =  inflater.inflate(R.layout.fragment_review, null);
        layout_writereview = (LinearLayout) view.findViewById(R.id.layout_writereview);
        layout_reviews = (LinearLayout) view.findViewById(R.id.resultlayout);
        RatingBar review_ratingbar = (RatingBar) view.findViewById(R.id.review_ratingbar);
        final TextView txt_review_ratingValue = (TextView) view.findViewById(R.id.review_ratingValue);
        final EditText txt_review_title = (EditText) view.findViewById(R.id.edittxt_reviewTitle);
        final EditText txt_review_description = (EditText) view.findViewById(R.id.edittxt_reviewDescription);
        Button btn_submit = (Button) view.findViewById(R.id.review_submitButton);

        layout_writereview.setVisibility(View.VISIBLE);
        layout_reviews.setVisibility(View.GONE);

        review_ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                review_value = (int) rating;
                Log.wtf("RatingChanged",rating+"");
                txt_review_ratingValue.setText(review_value+"");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("btn_submit","submit clicked");
                String title = txt_review_title.getText().toString();
                String desc = txt_review_description.getText().toString();
                if(title.trim().length()==0){
                    Toast.makeText(context, "Review title is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(desc.trim().length()==0){
                    Toast.makeText(context, "Review description is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(title.length()!=0 && desc.length()!=0){
                    //submit
                    Toast.makeText(context, "   Submitting...", Toast.LENGTH_SHORT).show();
                    sendReview(TempHolder.selectedRecipeID,review_value,title,desc);
                }
            }
        });
        builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.show();
    }
    protected void sendReview(final String recipe_id, final int rating, final String title, final String desc){
        if(!isNetworkAvailable()){
            showSnackbar();
        }else{
            final String server_url = TempHolder.HOST_ADDRESS+"/send_review.php";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response!=null){
                                try{
                                    Log.wtf("onResponse","Response:" +response);
                                    if(response.trim().equals("Process Successful")) {
                                        if (dialog != null) {
                                            dialog.hide();
                                            dialog.dismiss();
                                        }
                                        loadRecipe();
                                    }
                                    Toast.makeText(context, response.trim(), Toast.LENGTH_SHORT).show();
                                }catch (Exception ee)
                                {
                                    Log.wtf("loadRecipe ERROR (onResponse)",ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            Log.wtf("loadRecipe: onErrorResponse","Volley Error \n"+message);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String query = "Iecipe_id,datetime,title,description,rating) VALUES("+recipe_id+",NOW(),'"+title+"','"+desc+"',"+rating+");";
                    params.put("recipe_id",recipe_id);
                    params.put("title",title);
                    params.put("description",desc);
                    params.put("rating",rating+"");
                    Log.wtf("loadRecipe","Map<> Query: "+query);
                    return params;
                }
            };
            int socketTimeout = TempHolder.TIME_OUT;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            request.setShouldCache(false);
            requestQueue.add(request);
        }
    }
    private void loadReviews(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view =  inflater.inflate(R.layout.fragment_review, null);

        resultMessagelayout_progress = (ProgressBar) view.findViewById(R.id.resultMessagelayout_progress);
        resultMessagelayout_text = (TextView) view.findViewById(R.id.resultMessagelayout_text);
        layout_review_message = (LinearLayout) view.findViewById(R.id.resultMessagelayout);
        layout_writereview = (LinearLayout) view.findViewById(R.id.layout_writereview);
        layout_reviews = (LinearLayout) view.findViewById(R.id.resultlayout);
        layout_writereview.setVisibility(View.GONE);
        layout_reviews.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        reviewsList = new ArrayList<>();
        adapter = new ReviewAdapter(view.getContext(), reviewsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1,dpToPx(5),true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        if(!isNetworkAvailable()){
            showSnackbar();
        }else{
            reviewMessageDisplay(true,true,"Loading");
            final String server_url = TempHolder.HOST_ADDRESS+"/get_data.php";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response!=null){
                                try{
                                    Log.wtf("onResponse","Response:" +response);
                                    //clear the list in the UI
                                    int rating;
                                    String datetime,title,description;
                                    JSONObject object = new JSONObject(response);
                                    JSONArray Jarray  = object.getJSONArray("mydata");
                                    Reviews reviews;
                                    if(Jarray.length()>0) {
                                        reviewMessageDisplay(false,false,null);
                                        Log.wtf("onResponse", "Result count: " + Jarray.length());
                                        for (int i = 0; i < Jarray.length(); i++) {
                                            JSONObject Jasonobject = Jarray.getJSONObject(i);
                                            datetime = Jasonobject.getString("datetime");
                                            title = Jasonobject.getString("title");
                                            description = Jasonobject.getString("description");
                                            rating = Jasonobject.getInt("rating");
                                            reviews = new Reviews(datetime, title, description, rating);
                                            reviewsList.add(reviews);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }else reviewMessageDisplay(true,false,"No Reviews");
                                }catch (Exception ee)
                                {
                                    reviewMessageDisplay(true,false,"An error occured while loading");
                                    Log.wtf("loadRecipe ERROR (onResponse)",ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            reviewMessageDisplay(true,false,message);
                            Log.wtf("loadRecipe: onErrorResponse","Volley Error \n"+message);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String query = "SELECT * FROM tbl_ratings WHERE recipe_id = "+TempHolder.selectedRecipeID+" ORDER BY id desc;";
                    params.put("qry",query);
                    Log.wtf("loadRecipe","Map<> Query: "+query);
                    return params;
                }
            };
            int socketTimeout = TempHolder.TIME_OUT;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            request.setShouldCache(false);
            requestQueue.add(request);

            builder = new android.app.AlertDialog.Builder(context);
            builder.setView(view);
            dialog = builder.show();
        }
    }

    protected void reviewMessageDisplay(boolean showMessageLayout,boolean showProgressBar, String message){
        if(showMessageLayout){
            recyclerView.setVisibility(View.GONE);
            layout_review_message.setVisibility(View.VISIBLE);
            if(showProgressBar){
                resultMessagelayout_progress.setVisibility(View.VISIBLE);
            }else{
                resultMessagelayout_progress.setVisibility(View.GONE);
            }
            resultMessagelayout_text.setText(message);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            layout_review_message.setVisibility(View.GONE);
        }

    }

    //CARDS PLACEMENT
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    //screen transitions
    protected void showLoadingLayout(){
        layout_recipe.setVisibility(View.INVISIBLE);
        layout_loading.setVisibility(View.VISIBLE);
        layout_message.setVisibility(View.INVISIBLE);
    }
    protected void showMesageLayout(String message){
        layout_message_text.setText(message);
        layout_recipe.setVisibility(View.INVISIBLE);
        layout_loading.setVisibility(View.INVISIBLE);
        layout_message.setVisibility(View.VISIBLE);

    }
    protected void showRecipeLayout(){
        layout_recipe.setVisibility(View.VISIBLE);
        layout_loading.setVisibility(View.INVISIBLE);
        layout_message.setVisibility(View.INVISIBLE);
    }

    public Fragment_Recipe_Opened() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_opened, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        staticContext = context;
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.searchItem.setVisible(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recipe");
    }

    protected void showSnackbar(){
        RelativeLayout parent = (RelativeLayout) getActivity().findViewById(R.id.parentPanel);
        Snackbar.make(parent, "You're offline", Snackbar.LENGTH_LONG)
                .setAction("Go online", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
