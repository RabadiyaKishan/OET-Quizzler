package com.oet.quiz.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oet.quiz.Activity.ActivityExamList;
import com.oet.quiz.Activity.ActivityExamScheduleList;
import com.oet.quiz.Activity.ActivityFeedback;
import com.oet.quiz.Activity.ActivityPasswordChangeAfterLogin;
import com.oet.quiz.Activity.ActivityProfile;
import com.oet.quiz.Activity.ActivityResultList;
import com.oet.quiz.R;

public class HomeAdapter extends BaseAdapter {

    private final String[] values;
    private final int[] images;
    Context context;
    Activity mActivity;
    LayoutInflater layoutInflater;

    public HomeAdapter(Activity activity, Context context, String[] values, int[] images) {
        this.context = context;
        this.mActivity = activity;
        this.values = values;
        this.images = images;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Holder holder = new Holder();
        View rowView;

        rowView = layoutInflater.inflate(R.layout.home, null);
        holder.tv = rowView.findViewById(R.id.Menu_Name);
        holder.img = rowView.findViewById(R.id.Menu_Logo);

        holder.tv.setText(values[position]);
        holder.img.setImageResource(images[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    context.startActivity(new Intent(context, ActivityExamScheduleList.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (position == 1) {
                    context.startActivity(new Intent(context, ActivityExamList.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (position == 2) {
                    context.startActivity(new Intent(context, ActivityResultList.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (position == 3) {
                    context.startActivity(new Intent(context, ActivityProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (position == 4) {
                    context.startActivity(new Intent(context, ActivityPasswordChangeAfterLogin.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (position == 5) {
                    context.startActivity(new Intent(context, ActivityFeedback.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            }
        });
        return rowView;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

}