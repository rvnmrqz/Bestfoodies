package solomonkey.bestfoodies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;


public class Fragment_Review extends Fragment {

    LinearLayout layout_writereview;
    RatingBar review_ratingbar;
    TextView txt_review_ratingValue;
    EditText txt_review_title,txt_review_description;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout_writereview = (LinearLayout) getActivity().findViewById(R.id.layout_writereview);
        review_ratingbar = (RatingBar) getActivity().findViewById(R.id.review_ratingbar);
        txt_review_ratingValue = (TextView) getActivity().findViewById(R.id.review_ratingValue);
        txt_review_title = (EditText) getActivity().findViewById(R.id.edittxt_reviewTitle);
        txt_review_description = (EditText) getActivity().findViewById(R.id.edittxt_reviewTitle);

        if(!TempHolder.writeReviewMode){
            layout_writereview.setVisibility(View.GONE);
        }
    }


    public Fragment_Review() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recipe Reviews");
    }
}
