package com.fujielectricmeter.blemeter;

import android.graphics.Color;

public class SampleListItem {
    private String mTitle = null;
    private String mContents = null;
    private String mKey = null;
    private int mColor = Color.BLACK;

    /**
     * 空のコンストラクタ
     */
    public SampleListItem() {};

    public SampleListItem(final String title, final String contents, final String key, final int color) {
        mTitle = title;
        mContents = contents;
        mKey = key;
        mColor = color;
    }
    /**
     * タイトルを取得
     * @return タイトル
     */
    public String getTitle() {
        return mTitle;
    }
    public String getContents() {
        return mContents;
    }
    public String getKey() {
        return mKey;
    }
    public int getColor() {
        return mColor;
    }
    public void setColor(final int color){
        mColor = color;
    }
    public void setTitle(final String title){
        mTitle = title;
    }
    public void setContents(final String contents){
        mContents = contents;
    }
    public void setKey(final String key){
        mKey = key;
    }
}
