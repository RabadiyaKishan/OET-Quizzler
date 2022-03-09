package com.oet.quiz.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.oet.quiz.Adapter.HomeAdapter;
import com.oet.quiz.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private View mView;
    private Context mContext;
    private GridView GridMenu;
    private Intent mIntent;
    private Activity mActivity;
    private CircleImageView user_photo;
    private TextView user_name, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = container.getContext();
        mActivity = (Activity) container.getContext();
        FindViewByID();
        Body();
        return mView;
    }

    private void FindViewByID() {
        GridMenu = mView.findViewById(R.id.GridMenu);
    }

    private void Body() {
        String[] Name = {
                "Scheduler", "Exam List", "Result", "Profile", "Password", "Feedback"
        };
        int[] Images = {
                R.drawable.scheduler, R.drawable.exam, R.drawable.result, R.drawable.profile, R.drawable.password, R.drawable.feedback
        };

        HomeAdapter gridAdapter = new HomeAdapter(mActivity, mContext, Name, Images);
        GridMenu.setAdapter(gridAdapter);
    }
}