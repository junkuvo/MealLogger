package junkuvo.apps.meallogger.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import junkuvo.apps.meallogger.R;

public class ListRowViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ListRowViewHolder.class.getSimpleName();
    private final ListRowViewHolder self = this;

    protected ImageView imvThumbnail;
    protected TextView txtMealMenu;
    protected TextView txtMealDate;
    protected TextView txtPrice;

    public ListRowViewHolder(View view) {
        super(view);
        this.imvThumbnail = (ImageView) view.findViewById(R.id.imvThumbnail);
        this.txtMealMenu = (TextView) view.findViewById(R.id.txtMealMenu);
        this.txtMealDate = (TextView) view.findViewById(R.id.txtMealDate);
        this.txtPrice = (TextView) view.findViewById(R.id.txtPrice);
    }

    public ImageView getImvThumbnail() {
        return imvThumbnail;
    }

    public TextView getTxtMealMenu() {
        return txtMealMenu;
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

}
