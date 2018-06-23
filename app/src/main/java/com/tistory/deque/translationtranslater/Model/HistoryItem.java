package com.tistory.deque.translationtranslater.Model;

/**
 * Created by jkuot on 2018-06-22.
 */

public class HistoryItem {
    private int id;    
    private String OriginalPhrase;
    private String TranslatedPhrase;
    private String RegisterTime;
    private boolean opened = false;

    public HistoryItem(int id, String OriginalPhrase, String TranslatedPhrase, String RegisterTime){
        this.id = id;
        this.OriginalPhrase = OriginalPhrase;
        this.TranslatedPhrase = TranslatedPhrase;
        this.RegisterTime = RegisterTime;
    }
    public String getOriginalPhrase(){
        return OriginalPhrase;
    }
    public String getTranslatedPhrase(){
        return TranslatedPhrase;
    }
    public String getRegisterTime(){
        return RegisterTime;
    }

    public int getId() {
        return id;
    }

    public boolean getOpened() {
        return opened;
    }
    public void setOpened(boolean newState){
        opened = newState;
    }
}
