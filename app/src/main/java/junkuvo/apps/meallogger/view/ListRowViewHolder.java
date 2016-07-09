package junkuvo.apps.meallogger.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import junkuvo.apps.meallogger.R;

public class ListRowViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ListRowViewHolder.class.getSimpleName();
    private final ListRowViewHolder self = this;


    protected ImageView imageView;
    protected TextView textView;

    public ListRowViewHolder(View view) {
        super(view);
        this.imageView = (ImageView) view.findViewById(R.id.imvThumbnail);
        this.textView = (TextView) view.findViewById(R.id.txtMealMenu);
    }
}
