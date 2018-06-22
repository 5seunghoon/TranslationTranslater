package com.tistory.deque.translationtranslater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jkuot on 2018-06-22.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder>{

    ArrayList<HistoryItem> historyItems;

    public HistoryAdapter(ArrayList<HistoryItem> items){
        historyItems = items;
    }

    // 새로운 View Holder 생성
    @Override
    public HistoryAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent,false);
        return new ItemViewHolder(view);
    }

    // View의 내용을 해당 포지션의 데이터로 바꿈
    @Override
    public void onBindViewHolder(HistoryAdapter.ItemViewHolder holder, int position) {
        holder.OriginalPhrase.setText(historyItems.get(position).getOriginalPhrase());
        holder.TranslatedPhrase.setText(historyItems.get(position).getTranslatedPhrase());
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            OriginalPhrase = (TextView) itemView.findViewById(R.id.OriginalPhrase);
            TranslatedPhrase= (TextView) itemView.findViewById(R.id.TranslatedPhrase);
        }
    }

}