package junkuvo.apps.meallogger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.view.ListRowViewHolder;

public class RecyclerViewAdapter extends RealmRecyclerViewAdapter<MealLogs, ListRowViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private final RecyclerViewAdapter self = this;

    private Context mContext;
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private SimpleDateFormat mDateFormat;

    public RecyclerViewAdapter(Context context, OrderedRealmCollection<MealLogs> data) {
        super(context ,data, true);
        this.mContext = context;

        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    @Override
    public ListRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View rowView = inflater.inflate(R.layout.list_row, null);

        // Return a new holder instance
        ListRowViewHolder viewHolder = new ListRowViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListRowViewHolder holder, int position) {
        MealLogs mealLogs = getData().get(position);
        holder.getTxtMealMenu().setText(mealLogs.getMenuName());
        if(mealLogs.getCreatedAt() != null) {
            holder.getTxtMealDate().setText(mDateFormat.format(mealLogs.getCreatedAt()));
        }
    }

//    /*
//    表示の上限ぽい
//     */
//    @Override
//    public int getItemCount() {
//        return mItemList.size();
//    }
}
