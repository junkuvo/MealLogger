package junkuvo.apps.meallogger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import junkuvo.apps.meallogger.adapter.RecyclerViewAdapter;
import junkuvo.apps.meallogger.entity.MealLogs;

public class ActivityLogRegister extends AppCompatActivity implements RecyclerView.OnItemTouchListener {
//    private CardViewAdapter mAdapter;

//    private ArrayList<String> mItems;


    private Realm realm;

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private RecyclerViewAdapter mAdapter = null;
    private RealmResults<MealLogs> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_log_list);

        realm = Realm.getDefaultInstance();
        mItems = realm.where(MealLogs.class).findAllAsync();

        mAdapter = new RecyclerViewAdapter(this, mItems);//new CardViewAdapter(mItems, itemTouchListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(this);

//        SwipeableRecyclerViewTouchListener swipeTouchListener =
//                new SwipeableRecyclerViewTouchListener(recyclerView,
//                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
//                            @Override
//                            public boolean canSwipeLeft(int position) {
//                                return true;
//                            }
//
//                            @Override
//                            public boolean canSwipeRight(int position) {
//                                return true;
//                            }
//
//                            @Override
//                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
//                                for (int position : reverseSortedPositions) {
////                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped left", Toast.LENGTH_SHORT).show();
//                                    mItems.remove(position);
//                                    mAdapter.notifyItemRemoved(position);
//                                }
//                                mAdapter.notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
//                                for (int position : reverseSortedPositions) {
////                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped right", Toast.LENGTH_SHORT).show();
//                                    mItems.remove(position);
//                                    mAdapter.notifyItemRemoved(position);
//                                }
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        });
//
//        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        Toast.makeText(getApplicationContext(),"test", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
