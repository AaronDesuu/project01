package com.fujielectricmeter.blemeter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Trail {
    private File mOperation;
    private File mInformation;
    private File mResult;
    private File mSystem;
    private DateFormat mdf;
    Trail() {
        mOperation = new File(MainActivity.folderExternal, "operation.txt");
        mInformation = new File(MainActivity.folderExternal, "information.txt");
        mResult = new File(MainActivity.folderDocument, "result.txt");
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

    private void system(final String message) {
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

    public void information(final String message) {
        writeFile(message, mInformation);
        system(message);
    }
}
