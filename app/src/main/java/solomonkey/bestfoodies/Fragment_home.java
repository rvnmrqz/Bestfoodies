package solomonkey.bestfoodies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Fragment_home extends Fragment {

    Context context;
    TextView bouncingText;

    public Fragment_home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bouncingText = (TextView) getActivity().findViewById(R.id.bouncingText);
        //Animation shake = AnimationUtils.loadAnimation(context,R.anim.shake_anim);
        //bouncingText.setAnimation(shake);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.homeIsShown=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.wtf("HOME","onResume()");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");
        MainActivity.homeIsShown=true;
    }
}
