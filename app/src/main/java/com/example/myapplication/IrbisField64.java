package com.example.myapplication;


import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usr
 * Date: 29.11.2014
 * Time: 6:41:00
 * ������������ ���� ������ ������ (���������).
 */
public class IrbisField64 {
    /** ����� ���� */
    private String tag = null;

    /** ������ ���� (���� ��� ��������). */
    private String data = null;

    /** ������ �������� (���� ��� ����). */
    public final SubfieldList subfields = new SubfieldList();

    /**
     *
     */
    public IrbisField64() {
        //No-op.
    } // IrbisField64

    /**
     *
     * @return
     */
    public String getTag() {
        return tag;
    }

    /**
     *
     * @param tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     *
     * @return
     */
    public String getData() {
        return data;
    }

    /**
     *
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * ������ ������ ������ ���� � ������������ �� �� ��������, ���� ����
     * ����������� ��������.
     *
     * @param fieldData
     * @return
     */
    public static IrbisField64 parse(String fieldData) {
        IrbisField64 field64 = new IrbisField64();

        int idx = fieldData.indexOf('^');

        // ���� � ���� ��� ��������
        if (idx == -1) {
            field64.data = fieldData;
        }
        // ���� � ���� ���� �������.
        else {
            if (idx > 0) {
                field64.data = fieldData.substring(0, idx);
            }

            String s = fieldData.substring(idx + 1);

            int len = s.length();

            // ��������� ��������:
            // 0 - ������ ����
            // 1 - �������� ����������� ��������
            int state = 1;

            StringBuilder buf = new StringBuilder(100);

            char subfieldMarker = '\0';

            for (int i = 0; i < len; i++) {
                char c = s.charAt(i);

                switch (state) {
                    // ��������� - ���������������� ������.
                    case 0:
                        // �������� ����������� ��������
                        if (c == '^') {
                            if (buf.length() > 0) {
                                if (subfieldMarker == '\0') {
                                    field64.data = buf.toString();
                                }
                                else {
                                    field64.subfields.add(new IrbisSubField64(subfieldMarker, buf.toString()));
                                }

                                state = 1;

                                buf = new StringBuilder(100);
                            }
                            else {
                                state = 1;
                            }
                        }
                        else {
                            buf.append(c);
                        }
                        break;

                    // ��������� - �������� ����������� �������� (^)
                    case 1:
                        // �������� ������ �������
                        subfieldMarker = c;
                        state = 0;
                        break;

                    case 2:
                } // switch
            } // for

            // ���� ��������� ����� ������, �������� ��������� �������
            if (buf.length() > 0) {
                if (subfieldMarker == '\0') {
                    field64.data = buf.toString();
                }
                else {
                    field64.subfields.add(new IrbisSubField64(subfieldMarker, buf.toString()));
                }
            } // if
        } // for

        return field64;
    } // parse

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(tag).append('#');

        if (data != null) {
            buf.append(data);
        }

        List<IrbisSubField64> sfList = subfields.getSubFields();

        for (IrbisSubField64 sf : sfList) {
            buf.append(sf.toString());
        } // for

        return buf.toString();
    } // toString

    /**
     * ���������� �������� ������ ��������� �������� ����.
     *
     * @return
     */
    public Iterator<IrbisSubField64> iterator() {
        return subfields.getSubFields().iterator();
    } // iterator

    /**
     *
     * @return
     */
    public SubfieldList getSubFieldList() {
        return subfields;
    } // getSubFieldList
} // IrbisField64
