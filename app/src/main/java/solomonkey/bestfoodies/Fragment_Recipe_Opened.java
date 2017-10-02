package solomonkey.bestfoodies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;


public class Fragment_Recipe_Opened extends Fragment{

   Context context;

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
    TextView txt_RecipeName,txt_recipe_ingredients,txt_recipe_procedure,txt_recipe_reviewcount;
    ImageView img_recipe_finalimage;
    RatingBar ratingBar;
    String link;

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
        youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        loadRecipe();
        ratingbarListener();

        MainActivity.searchItem.setVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.wtf("Opened Fragment","OnDetach");
        MainActivity.searchItem.setVisible(true);
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
        this.context = context;

    }


}
