package junkuvo.apps.meallogger.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import junkuvo.apps.meallogger.R;

public class ListRowViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ListRowViewHolder.class.getSimpleName();
    private final ListRowViewHolder self = this;

    private Context mContext;

    protected ImageView imvThumbnail;
    protected TextView txtMealMenu;
    protected TextView txtMealDate;
    protected TextView txtPrice;
    protected TextView txtId;
    protected TextView txtFooterSpace;
    protected ImageView imgNotificationIcon;
    protected TextView txtNotificationName;

    protected TextView txtYear;
    protected TextView txtMonth;

    public ListRowViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        this.imvThumbnail = (ImageView) view.findViewById(R.id.imvThumbnail);
        this.txtMealMenu = (TextView) view.findViewById(R.id.txtMealMenu);
        this.txtMealDate = (TextView) view.findViewById(R.id.txtMealDate);
        this.txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        this.txtId = (TextView) view.findViewById(R.id.txtId);
        this.txtFooterSpace = (TextView) view.findViewById(R.id.txtFooterSpace);
        this.imgNotificationIcon = (ImageView) view.findViewById(R.id.imgNotificationIcon);
        this.txtYear = (TextView) view.findViewById(R.id.txtYear);
        this.txtMonth = (TextView) view.findViewById(R.id.txtMonth);
        this.txtNotificationName = (TextView) view.findViewById(R.id.txtNotificationName);
    }

    public TextView getTxtId() {
        return txtId;
    }

    public void setTxtId(TextView txtId) {
        this.txtId = txtId;
    }

    public ImageView getImvThumbnail() {
        return imvThumbnail;
    }

    public TextView getTxtMealMenu() {
        return txtMealMenu;
    }

    public TextView getTxtNotificationName() {
        return txtNotificationName;
    }

    public void setTxtNotificationName(TextView txtNotificationIcon) {
        this.txtNotificationName = txtNotificationIcon;
    }

    public void setImvThumbnail(ImageView imvThumbnail) {
        this.imvThumbnail = imvThumbnail;
    }

    public void setTxtMealMenu(TextView txtMealMenu) {
        this.txtMealMenu = txtMealMenu;
    }

    public TextView getTxtMealDate() {
        return txtMealDate;
    }

    public void setTxtMealDate(TextView txtMealDate) {
        this.txtMealDate = txtMealDate;
    }

    public TextView getTxtPrice() {
        return txtPrice;
    }

    public void setTxtPrice(TextView txtPrice) {
        this.txtPrice = txtPrice;
    }

    public TextView getTxtFooterSpace() {
        return txtFooterSpace;
    }

    public void setTxtFooterSpace(TextView txtFooterSpace) {
        this.txtFooterSpace = txtFooterSpace;
    }

    public ImageView getImgNotificationIcon() {
        return imgNotificationIcon;
    }

    public void setImgNotificationIcon(ImageView imgNotificationIcon) {
        this.imgNotificationIcon = imgNotificationIcon;
    }

    public TextView getTxtYear() {
        return txtYear;
    }

    public void setTxtYear(TextView txtYear) {
        this.txtYear = txtYear;
    }

    public TextView getTxtMonth() {
        return txtMonth;
    }

    public void setTxtMonth(TextView txtMonth) {
        this.txtMonth = txtMonth;
    }
}
