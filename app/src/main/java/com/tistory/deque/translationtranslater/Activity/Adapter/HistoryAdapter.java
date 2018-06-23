package com.tistory.deque.translationtranslater.Activity.Adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    public void onBindViewHolder(final HistoryAdapter.ItemViewHolder holder, final int position) {
        holder.OriginalPhrase.setText(historyItems.get(position).getOriginalPhrase());
        holder.TranslatedPhrase.setText(historyItems.get(position).getTranslatedPhrase());
        String tempRegisterTime = historyItems.get(position).getRegisterTime();
        holder.RegisterTime.setText(tempRegisterTime.substring(5,16));
        holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickHistoryDeleteBtn(position);
            }
        });
        holder.ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickHistoryShareBtn(position);
            }
        });
        holder.historyItemMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(historyItems.get(position).getOpened() == false) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                    holder.historyItemMainLayout.setLayoutParams(lp);
                    holder.OriginalPhrase.setMaxLines(5);
                    holder.TranslatedPhrase.setMaxLines(2);
                    historyItems.get(position).setOpened(true);
                }
                else{
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 270);
                    holder.historyItemMainLayout.setLayoutParams(lp);
                    holder.OriginalPhrase.setMaxLines(2);
                    holder.TranslatedPhrase.setMaxLines(1);
                    historyItems.get(position).setOpened(false);
                }
            }
        });
    }


    private void clickHistoryShareBtn(int position) {
        historyActivity.historyShare(position);
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
        private TextView RegisterTime;
        private Button DeleteButton;
        private Button ShareButton;
        private LinearLayout historyItemMainLayout;
        private LinearLayout historyItemTextLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            OriginalPhrase = (TextView) itemView.findViewById(R.id.OriginalPhrase);
            TranslatedPhrase= (TextView) itemView.findViewById(R.id.TranslatedPhrase);
            RegisterTime = (TextView) itemView.findViewById(R.id.RegisterTime);
            DeleteButton = itemView.findViewById(R.id.historyDelete_btn);
            ShareButton = itemView.findViewById(R.id.historyShare_btn);
            historyItemMainLayout = itemView.findViewById(R.id.history_item_main_layout);
            historyItemTextLayout = itemView.findViewById(R.id.history_item_text_layout);
        }

    }

}