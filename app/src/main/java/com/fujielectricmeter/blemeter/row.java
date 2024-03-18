package com.fujielectricmeter.blemeter;

import java.util.ArrayList;

public class row {
    public ArrayList<String> value;

    row() {
        value = new ArrayList<String>();
    }
    row(final String in, final int column) {
        value = new ArrayList<String>();
        String[] data = in.split(",");
        for (int i = 0; i < column; i++) {
            if (i < data.length) {
                value.add(data[i]);
            } else {
                value.add("");
            }
        }
    }
    row(final ArrayList<String> list, final int column) {
        value = new ArrayList<String>();
        value.addAll(0,list.subList(0,column));
    }
    row(final ArrayList<String> list, final int column, final int offset) {
        value = new ArrayList<String>();
        value.addAll(0,list.subList(offset,offset+column));
    }
    String[] get() {
        String[] ret = new String[value.size()];
        for (int i = 0; i < value.size(); i++) {
            ret[i] = value.get(i);
        }
        return ret;
    }
    void Del(final int i){
        value.remove(i);
    }
}
