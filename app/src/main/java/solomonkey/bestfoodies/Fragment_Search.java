package solomonkey.bestfoodies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Fragment_Search extends Fragment {

    Context context;
    public static Context staticContext;
    static Activity staticActivity;
    static LinearLayout loadinglayout ;
    static ProgressBar progressBar;
    static TextView textViewMessage;
    static LinearLayout resultsLayout;

    static RequestQueue requestQueue;
    static StringRequest request;

    //adapaters
    private RecyclerView recyclerView;
    private static RecipeAdapter adapter;
    private static List<Recipes> recipesList;

    public Fragment_Search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        staticContext = context;
        staticActivity = getActivity();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         loadinglayout = (LinearLayout) getActivity().findViewById(R.id.search_loadinglayout);
         progressBar = (ProgressBar) getActivity().findViewById(R.id.search_loading_progress);
         textViewMessage = (TextView) getActivity().findViewById(R.id.search_loading_text);
         resultsLayout = (LinearLayout) getActivity().findViewById(R.id.search_results);

        //Recycler
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recipesList = new ArrayList<>();
        adapter = new RecipeAdapter(context, recipesList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        showfirst4();
    }

    public static void doSearch(final String keyword) {
        recipesList.clear();
        Log.wtf("doSearch", "Keyword: " + keyword);
        updateDisplayInterface(true, true, "Searching");
        if (!isNetworkAvailable()) {
            showSnackbar();
            updateDisplayInterface(true,false,"No Internet Connection");
        } else {
            final String server_url = TempHolder.HOST_ADDRESS + "/get_data.php";
             requestQueue = Volley.newRequestQueue(staticContext);
             request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                try {
                                    Log.wtf("onResponse", "Response:" + response);
                                    //clear the list in the UI
                                    double rating;
                                    String recipe_id, recipe_name, ingredients, procedures,  reviews, thumbnailfile, videolink;
                                    JSONObject object = new JSONObject(response);
                                    JSONArray Jarray = object.getJSONArray("mydata");
                                    Recipes recipes;

                                    if (Jarray.length() > 0) {
                                        updateDisplayInterface(false,false,null);
                                        Log.wtf("onResponse", "Result count: " + Jarray.length());
                                        for (int i = 0; i < Jarray.length(); i++) {
                                            JSONObject Jasonobject = Jarray.getJSONObject(i);
                                            recipe_id = Jasonobject.getString("recipe_id");
                                            recipe_name = Jasonobject.getString("name");
                                            ingredients = Jasonobject.getString("ingredients");
                                            procedures = Jasonobject.getString("procedures");
                                            rating = Jasonobject.getDouble("rating");
                                            reviews = Jasonobject.getString("reviews");
                                            thumbnailfile = TempHolder.HOST_ADDRESS + "/images/" + Jasonobject.getString("imagefilename");
                                            videolink = Jasonobject.getString("videolink");
                                            recipes = new Recipes(recipe_id, recipe_name, ingredients, procedures, rating, reviews, thumbnailfile, videolink);
                                            recipesList.add(recipes);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        updateDisplayInterface(true,false,"No result(s)");
                                    }
                                } catch (Exception ee) {
                                    updateDisplayInterface(true,false,"An Error Occurred");
                                    Log.wtf("loadRecipe ERROR (onResponse)", ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            updateDisplayInterface(true,false,message);
                            Log.wtf("loadRecipe: onErrorResponse", "Volley Error \n" + message);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    String query = "SELECT re.*,coalesce(count(id),0) reviews,truncate(coalesce(avg(rating),0),1) rating from tbl_recipes re LEFT JOIN tbl_ratings ra ON re.recipe_id = ra.recipe_id WHERE CONCAT(re.name,' ',re.ingredients,' ',re.procedures) LIKE '%"+keyword+"%' GROUP BY re.recipe_id ;";
                    params.put("qry", query);
                    Log.wtf("loadRecipe", "Map<> Query: " + query);
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
    public static void showfirst4(){
        recipesList.clear();
        updateDisplayInterface(true, true, "Searching");
        if (!isNetworkAvailable()) {
            showSnackbar();
            updateDisplayInterface(true,false,"No Internet Connection");
        } else {
            final String server_url = TempHolder.HOST_ADDRESS + "/get_data.php";
            requestQueue = Volley.newRequestQueue(staticContext);
            request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                try {
                                    Log.wtf("onResponse", "Response:" + response);
                                    //clear the list in the UI
                                    double rating;
                                    String recipe_id, recipe_name, ingredients, procedures,  reviews, thumbnailfile, videolink;
                                    JSONObject object = new JSONObject(response);
                                    JSONArray Jarray = object.getJSONArray("mydata");
                                    Recipes recipes;
                                    if (Jarray.length() > 0) {
                                        updateDisplayInterface(false,false,null);
                                        Log.wtf("onResponse", "Result count: " + Jarray.length());
                                        for (int i = 0; i < Jarray.length(); i++) {
                                            JSONObject Jasonobject = Jarray.getJSONObject(i);
                                            recipe_id = Jasonobject.getString("recipe_id");
                                            recipe_name = Jasonobject.getString("name");
                                            ingredients = Jasonobject.getString("ingredients");
                                            procedures = Jasonobject.getString("procedures");
                                            rating = Jasonobject.getDouble("rating");
                                            reviews = Jasonobject.getString("reviews");
                                            thumbnailfile = TempHolder.HOST_ADDRESS + "/images/" + Jasonobject.getString("imagefilename");
                                            videolink = Jasonobject.getString("videolink");
                                            recipes = new Recipes(recipe_id, recipe_name, ingredients, procedures, rating, reviews, thumbnailfile, videolink);
                                            recipesList.add(recipes);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                } catch (Exception ee) {
                                    updateDisplayInterface(true,false,"An Error Occurred");
                                    Log.wtf("loadRecipe ERROR (onResponse)", ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            updateDisplayInterface(true,false,message);
                            Log.wtf("loadRecipe: onErrorResponse", "Volley Error \n" + message);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    String query = "SELECT re.*,coalesce(count(id),0) reviews,truncate(coalesce(avg(rating),0),1) rating from tbl_recipes re LEFT JOIN tbl_ratings ra ON re.recipe_id = ra.recipe_id GROUP BY re.recipe_id LIMIT 4;";
                    params.put("qry", query);
                    Log.wtf("loadRecipe", "Map<> Query: " + query);
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


    protected static String getVolleyError(VolleyError volleyError){
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
    public static void updateDisplayInterface(boolean showMessageLayout, boolean showProgressBar, String messageText){
        Log.wtf("updateDisplayInterface",showMessageLayout+" "+showProgressBar+" "+messageText);
        if(showMessageLayout){
            resultsLayout.setVisibility(View.GONE);
            loadinglayout.setVisibility(View.VISIBLE);

            if(showProgressBar){
                progressBar.setVisibility(View.VISIBLE);
            }else{
                progressBar.setVisibility(View.GONE);
            }
            textViewMessage.setText(messageText);
        }else{
            loadinglayout.setVisibility(View.GONE);
            resultsLayout.setVisibility(View.VISIBLE);
            resultsLayout.bringToFront();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Search");
    }

    protected static void showSnackbar(){
        RelativeLayout parent = (RelativeLayout) staticActivity.findViewById(R.id.parentPanel);
        Snackbar.make(parent, "You're offline", Snackbar.LENGTH_LONG)
                .setAction("Go online", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        staticContext.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setActionTextColor(staticContext.getResources().getColor(R.color.colorPrimary))
                .show();
    }
    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) staticContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
