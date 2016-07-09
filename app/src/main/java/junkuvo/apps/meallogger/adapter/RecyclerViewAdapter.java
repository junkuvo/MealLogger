package junkuvo.apps.meallogger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import junkuvo.apps.meallogger.view.ListRow;
import junkuvo.apps.meallogger.view.ListRowViewHolder;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ListRowViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private final RecyclerViewAdapter self = this;

    private List<ListRow> feedItemList;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<ListRow> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ListRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ListRowViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
