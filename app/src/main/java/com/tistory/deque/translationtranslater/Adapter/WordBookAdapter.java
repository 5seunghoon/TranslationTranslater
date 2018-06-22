package com.tistory.deque.translationtranslater.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tistory.deque.translationtranslater.Model.ExcludingMember;
import com.tistory.deque.translationtranslater.R;

import java.util.ArrayList;

/**
 * Created by hyunmoahn on 2018. 6. 3..
 */

public class WordBookAdapter extends ArrayAdapter<ExcludingMember> {
  public WordBookAdapter(Context context, ArrayList<ExcludingMember> list) {
    super(context, 0, list);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ExcludingMember member = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.word_book_item, parent, false);
    }

    TextView origin = (TextView)convertView.findViewById(R.id.word_book_key);
    TextView value = (TextView)convertView.findViewById(R.id.word_book_value);

    origin.setText(member.getOrigin());
    value.setText(member.getValue());

    return convertView;
  }
}
