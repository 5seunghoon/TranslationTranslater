package com.tistory.deque.translationtranslater.Model;

/**
 * Created by jkuot on 2018-06-22.
 */

public class HistoryItem {
    private String OriginalPhrase;
    private String TranslatedPhrase;

    public HistoryItem(String OriginalPhrase, String TranslatedPhrase){
        this.OriginalPhrase = OriginalPhrase;
        this.TranslatedPhrase = TranslatedPhrase;
    }
    public String getOriginalPhrase(){
        return OriginalPhrase;
    }
    public String getTranslatedPhrase(){
        return TranslatedPhrase;
    }
}
