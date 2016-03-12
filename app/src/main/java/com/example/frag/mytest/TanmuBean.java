package com.example.frag.mytest;

import android.graphics.Color;

/**
 * Created by frag on 2015/12/1.
 */
public class TanmuBean {
    private String[] items;
    private int[] color=new int[8];
    private int minTextSize;
    private float range;
    public TanmuBean() {
        //init default value
        color[0]=Color.parseColor("#800080");
        color[1]=Color.parseColor("#FFFF00");
        color[2]=Color.parseColor("#000000");
        color[3]=Color.parseColor("#FF0000");
        color[4]=Color.parseColor("#FFC0CB");
        color[5]=Color.parseColor("#FFFFFF");
        minTextSize = 16;
        range = 0.5f;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public int getColor(int i) {
        return color[i];
    }


    /**
     * min textSize, in dp.
     */
    public int getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}
