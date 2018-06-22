package com.tistory.deque.translationtranslater.Activity.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tistory.deque.translationtranslater.Activity.HistoryActivity;
import com.tistory.deque.translationtranslater.Model.HistoryItem;
import com.tistory.deque.translationtranslater.R;

import java.util.ArrayList;

/**
 * Created by jkuot on 2018-06-22.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder>{

    ArrayList<HistoryItem> historyItems;
    HistoryActivity historyActivity;

    public HistoryAdapter(ArrayList<HistoryItem> items, HistoryActivity historyActivity){
        historyItems = items;
        this.historyActivity = historyActivity;
    }

    // 새로운 View Holder 생성
    @Override
    public HistoryAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent,false);
        return new ItemViewHolder(view);
    }

    // View의 내용을 해당 포지션의 데이터로 바꿈
    @Override
    public void onBindViewHolder(HistoryAdapter.ItemViewHolder holder, final int position) {
        holder.OriginalPhrase.setText(historyItems.get(position).getOriginalPhrase());
        holder.TranslatedPhrase.setText(historyItems.get(position).getTranslatedPhrase());
        holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickHistoryDeleteBtn(position);
            }
        });
    }

    public void clickHistoryDeleteBtn(int position){
        historyActivity.historyDelete(position);
    }

    // 데이터 셋의 크기를 리턴
    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    // 커스텀 뷰홀더
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView OriginalPhrase;
        private TextView TranslatedPhrase;
        private Button DeleteButton;
        private Button ShareButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            OriginalPhrase = (TextView) itemView.findViewById(R.id.OriginalPhrase);
            TranslatedPhrase= (TextView) itemView.findViewById(R.id.TranslatedPhrase);
            DeleteButton = itemView.findViewById(R.id.historyDelete_btn);
        }

    }

}