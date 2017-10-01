package solomonkey.bestfoodies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;


public class Fragment_Recipe_List extends Fragment {

    Context context;
    //layouts
    LinearLayout loadingLayout,messageLayout,resultLayout;
    TextView messagelayout_textview;
    Button messagelayout_button;


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

        Toast.makeText(context, TempHolder.selectedCategory+"", Toast.LENGTH_SHORT).show();

        loadRecipe();
    }

    protected void loadRecipe(){
        showloadingLayout();
        Log.wtf("loadRecipe","loadRecipe called");
        if(!isNetworkAvailable()){
            showSnackbar();
            showMessage("No Internet Connection");
        }else{
            String server_url = TempHolder.HOST_ADDRESS+"/get_data.php";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response!=null){
                                try{
                                    Log.wtf("onResponse","Response:" +response);
                                    //clear the list in the UI

                                    JSONObject object = new JSONObject(response);
                                    JSONArray Jarray  = object.getJSONArray("mydata");

                                    if(Jarray.length()>0) {
                                        Log.wtf("onResponse","Result count: "+Jarray.length());
                                        for (int i = 0; i < Jarray.length(); i++) {
                                            JSONObject Jasonobject = Jarray.getJSONObject(i);

                                        }
                                        showResults();
                                    }else{
                                        showMessage("No Recipes to Display");
                                    }
                                }catch (Exception ee)
                                {
                                    showMessage("An Error Occurred");
                                    Log.wtf("loadRecipe ERROR (onResponse)",ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            showMessage(message);
                            Log.wtf("loadRecipe: onErrorResponse","Volley Error \n"+message);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String query = "SELECT re.*,count(id)reviews,coalesce(avg(rating),0) as ratings from tbl_recipes re LEFT JOIN tbl_ratings ra ON re.recipe_id = ra.recipe_id WHERE category = '"+TempHolder.selectedCategory+"' GROUP BY ra.recipe_id ;";
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


    protected void showResults(){
        messageLayout.setVisibility(View.INVISIBLE);
        resultLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
    }
    protected void showMessage(String message){
        messageLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.INVISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
        messagelayout_textview.setText(message);
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
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
