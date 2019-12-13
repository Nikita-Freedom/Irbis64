package com.example.myapplication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextRecordReader {
    /** */
    private BufferedReader in = null;

    /**
     *
     * @param filename
     */
    public TextRecordReader(String filename) throws IOException {
        in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "windows-1251"));
    }

    /**
     *
     * @param filename
     * @param encoding
     */
    public TextRecordReader(String filename, String encoding) throws IOException {
        in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encoding));
    }

    /**
     *
     * @return
     */
    public IrbisRecord64 readNext() throws IOException {
        IrbisRecord64 record = new IrbisRecord64();

        String s;

        while ((s = in.readLine()) != null) {
            if ("*****".equals(s)) {
                break;
            }

            int idx = s.indexOf(": ");

            String tag = s.substring(1, idx);
            String data = s.substring(idx + 1);

            IrbisField64 fld = IrbisField64.parse(data);
            fld.setTag(tag);

            record.addField(fld);
        } // while

        return record;
    } // readNext
} // TextRecordReader
