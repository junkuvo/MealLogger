package junkuvo.apps.meallogger.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import junkuvo.apps.meallogger.R;

public class TimerListRowViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = TimerListRowViewHolder.class.getSimpleName();
    private final TimerListRowViewHolder self = this;

    private Context mContext;

    protected TextView txtId;
    protected TextView txtTitle;
    protected TextView txtTime;
    protected TextView txtDays;
    protected ImageView imgNotificationIcon;

    public TimerListRowViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        this.txtTitle = (TextView)view.findViewById(R.id.txtTitle);
        this.txtTime = (TextView)view.findViewById(R.id.txtTime);
        this.txtId = (TextView)view.findViewById(R.id.txtId);
        this.txtDays = (TextView)view.findViewById(R.id.txtDays);
        this.imgNotificationIcon = (ImageView)view.findViewById(R.id.imgNotificationIcon);
    }

    public TextView getTxtId() {
        return txtId;
    }

    public void setTxtId(TextView txtId) {
        this.txtId = txtId;
    }

    public TextView getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(TextView txtTitle) {
        this.txtTitle = txtTitle;
    }

    public TextView getTxtTime() {
        return txtTime;
    }

    public void setTxtTime(TextView txtTime) {
        this.txtTime = txtTime;
    }

    public TextView getTxtDays() {
        return txtDays;
    }

    public void setTxtDays(TextView txtDays) {
        this.txtDays = txtDays;
    }

    public ImageView getImgNotificationIcon() {
        return imgNotificationIcon;
    }

    public void setImgNotificationIcon(ImageView imgNotificationIcon) {
        this.imgNotificationIcon = imgNotificationIcon;
    }
}
