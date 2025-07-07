package com.blemeterkai.meterkai.utils;
public class LevelUtils {
    public static String Level;

    public static int getLevel() {
        int ret = -1;

        if (Level != null) {
            ret = Integer.parseInt(Level);
        }
        return ret;
    }

    public static void setLevel(final int level) {

        Level = String.format("%d", level);
    }
}
