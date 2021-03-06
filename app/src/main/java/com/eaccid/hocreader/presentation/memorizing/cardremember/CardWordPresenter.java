package com.eaccid.hocreader.presentation.memorizing.cardremember;

import android.util.Log;

import com.eaccid.hocreader.data.local.AppDatabaseManager;
import com.eaccid.hocreader.data.local.db.entity.Word;
import com.eaccid.hocreader.App;
import com.eaccid.hocreader.presentation.BasePresenter;
import com.eaccid.hocreader.provider.db.words.WordItem;
import com.eaccid.hocreader.provider.db.words.WordItemImpl;

import javax.inject.Inject;

public class CardWordPresenter implements BasePresenter<CardWordActivity> {
    private final String logTAG = "CardWordPresenter";
    private CardWordActivity mView;

    @Inject
    AppDatabaseManager databaseManager;

    @Override
    public void attachView(CardWordActivity cardWordActivity) {
        mView = cardWordActivity;
        App.get(mView).getAppComponent().inject(this);
        Log.i(logTAG, "CardWordActivity has been attached.");
    }

    @Override
    public void detachView() {
        Log.i(logTAG, "CardWordActivity has been detached.");
        mView = null;
    }

    public void onViewCreate() {
        mView.setDataToView(getWordItem());
    }

    private WordItem getWordItem() {
        Word word = databaseManager.getWord(mView.getWord());
        if (word == null)
            word = new Word();
        return new WordItemImpl(word);
    }

}
