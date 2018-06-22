package com.tistory.deque.translationtranslater.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import com.tistory.deque.translationtranslater.Activity.Adapter.HistoryAdapter;
import com.tistory.deque.translationtranslater.Model.DB.DBOpenHelper;
import com.tistory.deque.translationtranslater.Model.HistoryItem;
import com.tistory.deque.translationtranslater.R;

/**
 * Created by jkuot on 2018-06-22.
 */

public class HistoryActivity extends Activity{

    private RecyclerView mHistoryRecyclerView;
    private HistoryAdapter mHistoryAdapter;
    private ArrayList<HistoryItem> mHistoryArray;
    private LinearLayoutManager mLayoutManger;
    private DBOpenHelper dbOpenHelper;


    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbOpenHelper = DBOpenHelper.getDbOpenHelper(
                getApplicationContext(),
                DBOpenHelper.TABLE_HISTORY,
                null, DBOpenHelper.dbVersion
        );
        dbOpenHelper.dbOpen();
        init();
        dataInit();


    }

    private void init() {
        mHistoryRecyclerView = findViewById(R.id.history_recyclerView);
    }

    private void dataInit() {
        mHistoryArray = dbOpenHelper.getHistory();

        mLayoutManger = new LinearLayoutManager(HistoryActivity.this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);

        mHistoryAdapter = new HistoryAdapter(mHistoryArray, this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);
        mHistoryRecyclerView.setLayoutManager(mLayoutManger);
        mHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void historyDelete(int position) {
        mHistoryArray.remove(position);
        mHistoryAdapter.notifyItemRemoved(position);
        mHistoryAdapter.notifyItemRangeChanged(0,mHistoryArray.size());
        

        //db.deleteHistory();
    }
}
