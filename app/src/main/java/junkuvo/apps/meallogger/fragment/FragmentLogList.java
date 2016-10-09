package junkuvo.apps.meallogger.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.adapter.RecyclerViewAdapter;
import junkuvo.apps.meallogger.entity.MealLogs;
import junkuvo.apps.meallogger.util.PriceUtil;
import junkuvo.apps.meallogger.view.EllipseTextView;

public class FragmentLogList extends Fragment {
    private Realm realm;

    private Context mContext = null;
    private View mView;

    private RecyclerView mRecyclerView = null;
    private RecyclerViewAdapter mAdapter = null;
    private RealmResults<MealLogs> mItems;

    private EllipseTextView mEllipseTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEllipseTextView = (EllipseTextView) getActivity().findViewById(R.id.txtSumPrice);
        mView = inflater.inflate(R.layout.fragment_log_list, container, false);

        realm = Realm.getDefaultInstance();
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        selectMealLogsFromRealm();
        setUpRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_menu_search_view));
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setQueryHint(getString(R.string.app_search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != "") {
                    selectMealLogsFromRealm(newText);
                    setUpRecyclerView();
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mItems.removeChangeListeners();
        realm.close();
        realm = null;
    }

    private void selectMealLogsFromRealm(String searchWord) {
        mItems = realm.where(MealLogs.class)
                .contains("notificationName", searchWord)
                .or()
                .contains("menuName", searchWord)
                .findAllSorted("createdAt", Sort.DESCENDING);
    }

    private void selectMealLogsFromRealm() {
        mItems = realm.where(MealLogs.class).findAllSorted("createdAt", Sort.DESCENDING);
    }

    private void setUpRecyclerView() {
        mItems.addChangeListener(new RealmChangeListener<RealmResults<MealLogs>>() {
            @Override
            public void onChange(RealmResults<MealLogs> element) {
                // 合計金額を常に最新化
                long sum = element.sum("price").longValue();
                mEllipseTextView.setText(PriceUtil.parseLongToPrice(sum, "¥"));
                if (mAdapter != null) {
                    mAdapter.setmLastPosition(mItems.size());
                }
            }
        });
        long sum = mItems.sum("price").longValue();
        mEllipseTextView.setText(PriceUtil.parseLongToPrice(sum, "¥"));

        if (mAdapter != null) {
            mAdapter = null;
        }
        mAdapter = new RecyclerViewAdapter(mContext, mItems);//new CardViewAdapter(mItems, itemTouchListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }
}
