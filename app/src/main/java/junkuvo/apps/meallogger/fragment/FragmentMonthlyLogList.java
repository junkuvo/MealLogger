package junkuvo.apps.meallogger.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.adapter.MonthlyRecyclerViewAdapter;
import junkuvo.apps.meallogger.entity.MonthlyMealLog;
import junkuvo.apps.meallogger.view.EllipseTextView;

public class FragmentMonthlyLogList extends Fragment {
    private Realm realm;

    private Context mContext = null;
    private View mView;

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private MonthlyRecyclerViewAdapter mAdapter = null;

    private EllipseTextView mEllipseTextView;

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
        mEllipseTextView = (EllipseTextView) getActivity().findViewById(R.id.txtSumPrice);
        mView = inflater.inflate(R.layout.fragment_log_list, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        realm = Realm.getDefaultInstance();

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
        realm = null;
    }

    public void setUpRecyclerView() {
        RealmResults monthlyLogs = realm.where(MonthlyMealLog.class).findAllAsync();
        mAdapter = new MonthlyRecyclerViewAdapter(mContext, monthlyLogs);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    public MonthlyRecyclerViewAdapter getmAdapter() {
        return mAdapter;
    }
}
