package junkuvo.apps.meallogger.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import junkuvo.apps.meallogger.R;

public class MonthlyListRowViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = MonthlyListRowViewHolder.class.getSimpleName();
    private final MonthlyListRowViewHolder self = this;

    private Context mContext;

    protected TextView txtMonthlyPrice;
    protected TextView txtId;
    protected TextView txtMonth;
    protected TextView txtFooterSpace;

    public MonthlyListRowViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        this.txtMonthlyPrice = (TextView) view.findViewById(R.id.txtMonthlyPrice);
        this.txtId = (TextView)view.findViewById(R.id.txtId);
        this.txtMonth = (TextView)view.findViewById(R.id.txtMonth);
        this.txtFooterSpace = (TextView)view.findViewById(R.id.txtFooterSpace);

    }

    public TextView getTxtId() {
        return txtId;
    }

    public void setTxtId(TextView txtId) {
        this.txtId = txtId;
    }

    public TextView getTxtMonthlyPrice() {
        return txtMonthlyPrice;
    }

    public void setTxtMonthlyPrice(TextView txtMonthlyPrice) {
        this.txtMonthlyPrice = txtMonthlyPrice;
    }

    public TextView getTxtMonth() {
        return txtMonth;
    }

    public void setTxtMonth(TextView txtMonth) {
        this.txtMonth = txtMonth;
    }

    public TextView getTxtFooterSpace() {
        return txtFooterSpace;
    }

    public void setTxtFooterSpace(TextView txtFooterSpace) {
        this.txtFooterSpace = txtFooterSpace;
    }
}
