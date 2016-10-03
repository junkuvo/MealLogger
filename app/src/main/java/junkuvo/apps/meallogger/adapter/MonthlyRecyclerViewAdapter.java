package junkuvo.apps.meallogger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.MonthlyMealLog;
import junkuvo.apps.meallogger.util.PriceUtil;
import junkuvo.apps.meallogger.view.MonthlyListRowViewHolder;

public class MonthlyRecyclerViewAdapter extends RealmRecyclerViewAdapter<MonthlyMealLog, MonthlyListRowViewHolder> {
    private static final String TAG = MonthlyRecyclerViewAdapter.class.getSimpleName();
    private final MonthlyRecyclerViewAdapter self = this;

    private Context mContext;
    private RealmResults<MonthlyMealLog> mMonthlySumData;
    private View mMealLogsRowView;
    private int mLastPosition;

    public MonthlyRecyclerViewAdapter(Context context, RealmResults<MonthlyMealLog> data) {
        super(context, data, true);
        this.mContext = context;
        mMonthlySumData = data;
    }

    @Override
    public MonthlyListRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        // Inflate the custom layout
        mMealLogsRowView = inflater.inflate(R.layout.monthly_list_row, null);

        return new MonthlyListRowViewHolder(mContext, mMealLogsRowView);
    }

    @Override
    public void onBindViewHolder(MonthlyListRowViewHolder holder, int position) {
        MonthlyMealLog monthlyMealLog = mMonthlySumData.get(position);
        holder.getTxtMonth().setText(monthlyMealLog.getYear() + "/" + monthlyMealLog.getMonth());
        holder.getTxtMonthlyPrice().setText(PriceUtil.parseLongToPrice(monthlyMealLog.getSumPrice(), "¥"));
        if(position == mLastPosition - 1) {
            holder.getTxtFooterSpace().setVisibility(View.VISIBLE);
        }else{
            holder.getTxtFooterSpace().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mMonthlySumData != null) {
            return mMonthlySumData.size();
        } else {
            return 0;
        }
    }

    public void setmLastPosition(int mLastPosition) {
        this.mLastPosition = mLastPosition;
    }

}
