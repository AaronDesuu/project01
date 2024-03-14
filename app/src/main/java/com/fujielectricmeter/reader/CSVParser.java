package com.fujielectricmeter.blemeter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamBleMeter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CSVParser {
    private File mFolder;
    private String mLabel;
    private int mKey;
    private int mCur;
    private String mCsv;
    private ArrayList<String> mCol;
    private ArrayList<row> mRow;

    CSVParser() {
        mLabel = null;
        mKey = -1;
        mCsv = null;
        mCol = new ArrayList<String>();
        mRow = new ArrayList<row>();
    }

    CSVParser(final File folder) {
        mLabel = null;
        mKey = -1;
        mCsv = null;
        mCol = new ArrayList<String>();
        mRow = new ArrayList<row>();
        mFolder = folder;
    }

    CSVParser(final String csvFile, final File folder) {
        mLabel = null;
        mKey = -1;
        mCsv = csvFile;
        mCol = new ArrayList<String>();
        mRow = new ArrayList<row>();
        mFolder = folder;
        readFile(null);
    }

    ArrayList<String> Columns() {
        return mCol;
    }

    ArrayList<row> Rows() {
        return mRow;
    }

    void DelCols(final int i) {
        mCol.remove(i);
        for (int pos = 0; pos < mRow.size(); pos++) {
            mRow.get(pos).Del(i);
        }
    }

    public void clear() {
        mCol.clear();
        mRow.clear();
        Reset();
    }

    void Copy(CSVParser from) {
        clear();
        mCol.addAll(from.Columns());
        mRow.addAll(from.Rows());
    }

    public String Present() {
        return mCsv;
    }

    public row Line(final int line) {
        if (line < mRow.size()) {
            return mRow.get(line);
        } else {
            return null;
        }
    }

    public int size() {
        return mRow.size();
    }

    private int index(String label) {
        int ret = -1;
        if (!mCol.isEmpty()) {
            for (int i = 0; i < mCol.size(); i++) {
                if (mCol.get(i).equals(label)) {
                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    public void Reset() {
        mLabel = null;
        mKey = -1;
    }

    public String Row(final String label) {
        if (mLabel == null) {
            mLabel = label;
            mKey = index(mLabel);
            mCur = -1;
        }
        if (!mLabel.equals(label)) {
            mLabel = label;
            mKey = index(mLabel);
            mCur = -1;
        }
        if (mKey < 0) {
            return null;
        }
        if (mCur < 0) {
            mCur = 0;
        } else {
            mCur++;
        }

        if (mCur < mRow.size()) {
            if (mKey < mRow.get(mCur).value.size()) {
                return mRow.get(mCur).value.get(mKey);
            } else {
                return new String();
            }
        } else {
            mCur = -1;
            return null;
        }
    }

    public String Column(final String label) {
        String ret = null;
        int column;
        if(mCur >=0) {
            column = index(label);
            if (column >= 0) {
                ret = mRow.get(mCur).value.get(column);
            }
        }
        return ret;
    }

    public String Cell(final int row, final String label) {
        row line = Line(row);
        if (line != null) {
            int column = index(label);
            if (column >= 0) {
                mCur = row;
                return line.value.get(column);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void New(final String[] column) {
        clear();
        for (int i = 0; i < column.length; i++) {
            mCol.add(column[i]);
        }
    }

    public void New(final String column) {
        clear();
        String[] data = column.split(",");
        for (int i = 0; i < data.length; i++) {
            mCol.add(data[i]);
        }
    }

    public boolean Update(final String value, final String label) {
        int column;
        boolean ret;

        column = index(label);
        if (column >= 0) {
            mRow.get(mCur).value.set(column, value);
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    public boolean Update(final CSVParser in, final String key) {

        while (true) {
            String val = in.Row(key);
            if (val != null) {
                if (Find(key, val)) {
                    for (int i = 0; i < in.Columns().size(); i++) {
                        String label = in.Columns().get(i);
                        if (!key.equals(label)) {
                            Update(in.Column(label), label);
                        }
                    }
                }
            } else {
                break;
            }
        }
        return true;
    }

    public void Extract(CSVParser out, final String key) {

        while (true) {
            String val = Row(key);
            if (val != null) {
                if (!out.Find(key, val)) {
                    out.Add("");
                    out.Update(val, key);
                }
                for (int i = 0; i < out.Columns().size(); i++) {
                    String label = out.Columns().get(i);
                    String value = Column(label);
                    if(!value.isEmpty()) {
                        out.Update(value, label);
                    }
                }
            } else {
                break;
            }
        }
    }

    public void Add(final String row) {
        boolean ret;

        String dat;
        if (row == null || row.isEmpty()) {
            dat = new String("");
            for (int i = 0; i < mCol.size(); i++) {
                dat += ",";
            }
        } else {
            dat = row;
        }
        mRow.add(new row(dat, mCol.size()));
        mCur = mRow.size() - 1;
    }

    public void Add(final ArrayList<String> list) {
        boolean ret;
        String dat;

        mRow.add(new row(list, mCol.size()));
        mCur = mRow.size() - 1;
    }

    public boolean Find(final String label, final String value) {
        boolean find = false;
        if (!value.isEmpty()) {
            Reset();
            while (true) {
                String val = Row(label);
                if (val == null) {
                    break;
                }
                if (value.equals(val)) {
                    find = true;
                    break;
                }
            }
        }
        return find;
    }

    public boolean FindNext(final String label, final String value) {
        boolean find = false;
        if (!value.isEmpty()) {
            while (true) {
                String val = Row(label);
                if (val == null) {
                    break;
                }
                if (value.equals(val)) {
                    find = true;
                    break;
                }
            }
        }
        return find;
    }

    public boolean readFile(final String csvFile) {

        boolean ret = false;

        if (csvFile != null) {
            if (mCsv == null) {
                mCsv = csvFile;
            } else {
                if (!mCsv.equals(csvFile)) {
                    mCsv = csvFile;
                }
            }
        }
        if (mCsv != null) {
            clear();
            File file = new File(mFolder, mCsv);
            try (BufferedReader br = new BufferedReader(new InputStreamBleMeter(new FileInputStream(file), "Shift-JIS"))) {
                while (true) {
                    String read = br.readLine();
                    if (read != null) {
                        if (mCol.isEmpty()) {
                            String[] data = read.split(",");
                            for (int i = 0; i < data.length; i++) {
                                mCol.add(data[i]);
                            }
                        } else {
                            mRow.add(new row(read, mCol.size()));
                        }
                    } else {
                        ret = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public boolean writeFile() {
        boolean ok = true;
        File file = new File(mFolder, mCsv);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "SHIFT_JIS"))) {
            for (int i = 0; i < mCol.size(); i++) {
                if (i != 0) {
                    bw.write(",");
                }
                bw.write(mCol.get(i));
            }
            for (int i = 0; i < mRow.size(); i++) {
                bw.write("\r\n");
                for (int j = 0; j < mRow.get(i).value.size(); j++) {
                    if (j != 0) {
                        bw.write(",");
                    }
                    bw.write(mRow.get(i).value.get(j));
                }
            }
            bw.close();
        } catch (Exception e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }
}
