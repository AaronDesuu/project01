package com.fujielectricmeter.blemeter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Trail {
    private File mOperation;
    private File mError;
    private File mResult;
    private File mSystem;
    private DateFormat mdf;
    Trail() {
        mOperation = new File(MainActivity.folderExternal, "operation.txt");
        mError = new File(MainActivity.folderExternal, "error.txt");
        mResult = new File(MainActivity.folderExternal, "result.txt");
        mSystem = new File(MainActivity.folderCache, "system.txt");
        mdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    }

    private void writeFile(final String message, final File file){
        final Date date = new Date();
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(mdf.format(date) + ":" + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void system(final String message) {
        writeFile(message, mSystem);
    }

    public void operation(final String message) {
        writeFile(message, mOperation);
        system(message);
    }

    public void result(final String message) {
        writeFile(message, mResult);
        system(message);
    }

    public void error(final String message) {
        writeFile(message, mError);
        system(message);
    }
}
