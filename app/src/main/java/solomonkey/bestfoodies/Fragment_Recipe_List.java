package solomonkey.bestfoodies;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Fragment_Recipe_List extends Fragment {

    Context context;

    //layouts
    LinearLayout loadingLayout,messageLayout,resultLayout;
    TextView messagelayout_textview;
    Button messagelayout_button;

    //adapaters
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipes> recipesList;

    public Fragment_Recipe_List() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.wtf("onAttach","Fragment_Recipe_List");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TempHolder.selectedCategory);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingLayout = (LinearLayout) getActivity().findViewById(R.id.loadinglayout);
        messageLayout = (LinearLayout) getActivity().findViewById(R.id.messagelayout);
        resultLayout = (LinearLayout) getActivity().findViewById(R.id.resultlayout);
        messagelayout_textview = (TextView) getActivity().findViewById(R.id.messagelayout_textview);
        messagelayout_button = (Button) getActivity().findViewById(R.id.messagelayout_button);
        messagelayout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRecipe();
            }
        });

        //Recycler
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recipesList = new ArrayList<>();
        adapter = new RecipeAdapter(context, recipesList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(8),true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        loadRecipe();
    }

    protected void loadRecipe(){
        showloadingLayout();
        Log.wtf("loadRecipe List","loadRecipe called");
        if(!isNetworkAvailable()){
            showSnackbar();
            showMessage("No Internet Connection","Retry");
        }else{
            String server_url = TempHolder.HOST_ADDRESS+"/get_data.php";
            Log.wtf("Request Link",server_url);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response!=null){
                                try{
                                    Log.wtf("onResponse","Response:" +response+"\n Response Length: "+response.length());
                                    double rating;
                                    String recipe_id,recipe_name,ingredients,procedures,reviews,thumbnailfile,videolink;
                                    JSONObject object = new JSONObject(response);
                                    JSONArray Jarray  = object.getJSONArray("mydata");
                                    Recipes recipes;
                                    if(Jarray.length()>0) {
                                        for (int i = 0; i < Jarray.length(); i++) {
                                            JSONObject Jasonobject = Jarray.getJSONObject(i);
                                            Log.wtf("JSON loop","Index: "+i);
                                            recipe_id = Jasonobject.getString("recipe_id");
                                            recipe_name = Jasonobject.getString("name");
                                            ingredients = Jasonobject.getString("ingredients");
                                            procedures = Jasonobject.getString("procedures");
                                            rating = Jasonobject.getDouble("rating");
                                            reviews = Jasonobject.getString("reviews");
                                            thumbnailfile = TempHolder.HOST_ADDRESS+"/images/"+Jasonobject.getString("imagefilename");
                                            videolink = Jasonobject.getString("videolink");
                                            recipes = new Recipes(recipe_id,recipe_name,ingredients,procedures,rating,reviews,thumbnailfile,videolink);
                                            recipesList.add(recipes);
                                        }
                                        adapter.notifyDataSetChanged();
                                        showResults();
                                    }else{
                                        showMessage("No Recipes to Display","Refresh");
                                    }
                                }catch (Exception ee)
                                {
                                    showMessage("An Error Occurred","Retry");
                                    Log.wtf("loadRecipe ERROR (onResponse)",ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            showMessage(message,"Retry");
                            Log.wtf("loadRecipe: onErrorResponse","Volley Error \n"+message);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                   // String query = "SELECT re.*,coalesce(count(id),0) reviews,truncate(coalesce(avg(rating),0),1) rating from tbl_recipes re LEFT JOIN tbl_ratings ra ON re.recipe_id = ra.recipe_id "+TempHolder.listLoaderWhereClause+" GROUP BY re.recipe_id ;";
                    String query = "SELECT re.*,coalesce(count(id),0) reviews,truncate(coalesce(avg(rating),0),1) rating from tbl_recipes re LEFT JOIN tbl_ratings ra ON re.recipe_id = ra.recipe_id "+TempHolder.listLoaderWhereClause+" GROUP BY re.recipe_id ;";
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

    //SCREEN TRANSITIONS
    protected void showResults(){
        messageLayout.setVisibility(View.INVISIBLE);
        resultLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
    }
    protected void showMessage(String message, String buttonMessage){
        messageLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
        messagelayout_textview.setText(message);
        messagelayout_button.setText(buttonMessage);
    }
    protected void showloadingLayout(){
        messageLayout.setVisibility(View.INVISIBLE);
        resultLayout.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
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
