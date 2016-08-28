package junkuvo.apps.meallogger.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.adapter.RecyclerViewAdapter;
import junkuvo.apps.meallogger.entity.MealLogs;

public class FragmentLogList extends Fragment {
    private Realm realm;

    private Context mContext = null;
    private View mView;
    private RecyclerFragmentListener mFragmentListener = null;

    private AlertDialog.Builder mAlertDialog;

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private RecyclerViewAdapter mAdapter = null;
    private OrderedRealmCollection<MealLogs> mItems;

    public interface RecyclerFragmentListener {
        void onRecyclerEvent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_log_list, container, false);

        realm = Realm.getDefaultInstance();
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpRecyclerView();
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ((RealmBaseAdapter<?>) getListAdapter()).updateRealmResults(null);
//        realm.close();
//        realm = null;
//    }
//
//    @NonNull
//    protected RealmResults<MealLogs> buildTweetList(Realm realm) {
//        return realm.allObjectsSorted(MealLogs.class, "createdAt", Sort.DESCENDING);
//    }

    private void setUpRecyclerView() {
        mItems = realm.where(MealLogs.class).findAllAsync();
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new RecyclerViewAdapter(mContext, mItems);//new CardViewAdapter(mItems, itemTouchListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

    }

}
