package solomonkey.bestfoodies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


public class Fragment_Categories extends Fragment {

    Context context;
    ImageButton btnBreakfast,btnLunch,btnDinner,btnDessert;

    public Fragment_Categories() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.wtf("onAttach","Categories");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initialize here

        btnBreakfast = (ImageButton) getActivity().findViewById(R.id.imageButton2);
        btnLunch = (ImageButton) getActivity().findViewById(R.id.imageButton3);
        btnDinner = (ImageButton) getActivity().findViewById(R.id.imageButton4);
        btnDessert = (ImageButton) getActivity().findViewById(R.id.imageButton5);
        buttonListener();
    }


    protected void buttonListener(){
        btnBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempHolder.selectedCategory = "Breakfast";
                TempHolder.listLoaderWhereClause=" WHERE category = '"+TempHolder.selectedCategory+"'";
                MainActivity.changeBackstack(true,new Fragment_Recipe_List(),"Breakfast");
            }
        });
        btnLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempHolder.selectedCategory = "Lunch";
                TempHolder.listLoaderWhereClause=" WHERE category = '"+TempHolder.selectedCategory+"'";
                MainActivity.changeBackstack(true,new Fragment_Recipe_List(),"Lunch");
            }
        });
        btnDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempHolder.selectedCategory = "Dinner";
                TempHolder.listLoaderWhereClause=" WHERE category = '"+TempHolder.selectedCategory+"'";
                MainActivity.changeBackstack(true,new Fragment_Recipe_List(),"Dinner");
            }
        });
        btnDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempHolder.selectedCategory = "Dessert";
                TempHolder.listLoaderWhereClause=" WHERE category = '"+TempHolder.selectedCategory+"'";
                MainActivity.changeBackstack(true,new Fragment_Recipe_List(),"Dessert");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Categories");
    }
}
