package com.tistory.deque.translationtranslater.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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
    private int dbVersion = 1;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbOpenHelper = DBOpenHelper.getDbOpenHelper(
                getApplicationContext(),
                DBOpenHelper.TABLE_HISTORY,
                null, dbVersion
        );
        dbOpenHelper.dbOpen();
        init();
        dataInit();
    }

    private void init() {
        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.history_recyclerView);
    }

    private void dataInit() {
        mHistoryArray = dbOpenHelper.getHistory();

        mLayoutManger = new LinearLayoutManager(HistoryActivity.this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);

        mHistoryAdapter = new HistoryAdapter(mHistoryArray);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);
        mHistoryRecyclerView.setLayoutManager(mLayoutManger);
        mHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
