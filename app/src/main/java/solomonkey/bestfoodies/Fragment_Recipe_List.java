package solomonkey.bestfoodies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class Fragment_Recipe_List extends Fragment {

    Context context;
    public Fragment_Recipe_List() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }


}
