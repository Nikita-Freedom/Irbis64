package com.example.myapplication;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usr
 * Date: 29.11.2014
 * Time: 6:40:46
 * ������������ ���� ������ ������ (��������)
 */
public class IrbisRecord64 {
    /**
     * ��������� ��������� ������
     */
    public static final int BIT_LOG_DEL = 1;

    /**
     * ��������� ��������� ������
     */
    public static final int BIT_PHYS_DEL = 2;

    /**
     * �������������� ������
     */
    public static final int BIT_ABSENT = 4;

    /**
     * ������������������� ������
     */
    public static final int BIT_NOTACT_REC = 8;

    /**
     * ��������� ��������� ������
     */
    public static final int BIT_LAST_REC = 32;

    /**
     * ������ ��������������
     */
    public static final int BIT_LOCK_REC = 64;

    /**
     * ����� ������
     */
    private int mfn;

    /**
     * ��, �� ������� ���� ��������� ������.
     */
    private String database;

    /**
     * ������ ������.
     */
    private int version;

    /**
     * ������ ������
     */
    private int status;

    /**
     * ������ ����� ������.
     */
    private List<IrbisField64> fields = new ArrayList<IrbisField64>(20);

    /**
     * @return
     */
    public boolean isLogicalyDeleted() {
        return (status & BIT_LOG_DEL) == BIT_LOG_DEL;
    }

    /**
     * @return
     */
    public boolean isPhysicalyDeleted() {
        return (status & BIT_PHYS_DEL) == BIT_PHYS_DEL;
    }

    /**
     * @return
     */
    public boolean isAbsent() {
        return (status & BIT_ABSENT) == BIT_ABSENT;
    }

    /**
     * @return
     */
    public boolean isActualized() {
        return (status & BIT_NOTACT_REC) != BIT_NOTACT_REC;
    }

    /**
     * @return
     */
    public boolean isLastInstanceOfRecord() {
        return (status & BIT_LAST_REC) == BIT_LAST_REC;
    }

    /**
     * ������� �� ��������� ������ ������ ���� IrbisRecord64.
     *
     * @param body
     * @param startIndex
     * @return
     */
    public static IrbisRecord64 parse(List<String> body, int startIndex) {
        IrbisRecord64 result = new IrbisRecord64();

        String s = body.get(startIndex++);

        // ��������� ������� ������ � �� MFN
        int idx = s.indexOf('#');
        result.mfn = Integer.parseInt(s.substring(0, idx));

        if (idx < s.length()) {
            int idx1 = s.lastIndexOf('#');

            if (idx1 != -1) {
                if (idx != idx1) {
                    result.status = Integer.parseInt(s.substring(idx + 1, idx1));
                }
                else {
                    result.status = 0;
                }
            } else {
                result.status = Integer.parseInt(s.substring(idx + 1));
            } // if
        } // if

        // ������ ������ ������
        s = body.get(startIndex++);
        idx = s.indexOf('#');
        result.version = Integer.parseInt(s.substring(idx + 1));

        // ������ ����� ������
        final int n = body.size();

        for (int i = startIndex; i < n; i++) {
            s = body.get(i);

            idx = s.indexOf('#');

            if (idx == -1) {
                continue;
            }

            String tag = s.substring(0, idx);
            String data = idx == (s.length() - 1) ? "" : s.substring(idx + 1);

            IrbisField64 fld = IrbisField64.parse(data);
            fld.setTag(tag);

            result.fields.add(fld);
        } // for

        return result;
    } // parse

    /**
     * ���������� ������ ����� ����� �� ������ (��� ���������� ���� � ��������� ��������).
     *
     * @param tag ������ ����
     * @return
     */
    public List<IrbisField64> getFieldSet(String tag) {
        List<IrbisField64> result = new ArrayList<IrbisField64>(5);

        for (IrbisField64 f : fields) {
            if (tag.equals(f.getTag())) {
                result.add(f);
            }
        } // for

        return result;
    } // getFieldSet

    /**
     * ��������� ������ �� ��������� ���� �����.
     *
     * @return
     */
    public List<IrbisField64> getFieldSet() {
        return fields;
    } // getFieldSet

    /**
     * ���������� �������� �� ��������� ���� ����� ������.
     *
     * @return
     */
    public Iterator<IrbisField64> getFieldIterator() {
        return fields.iterator();
    } // getFieldIterator

    /**
     * ���������� ������ ����� ����� �� ������ (��� ���������� ���� � ��������� ��������).
     *
     * @param tag
     * @return
     */
    public Iterator<IrbisField64> getFieldIterator(String tag) {
        List<IrbisField64> result = new ArrayList<IrbisField64>(5);

        for (IrbisField64 f : fields) {
            if (tag.equals(f.getTag())) {
                result.add(f);
            }
        } // for

        return result.iterator();
    } // getFieldIterator

    /**
     * ���������� �������� ���� (������ ��� ������������� ����������).
     *
     * @param tag
     * @return
     */
    public String getData(String tag) {
        Iterator<IrbisField64> iter = fields.iterator();

        while (iter.hasNext()) {
            IrbisField64 f = iter.next();

            if (f.getTag().equals(tag)) {
                return f.getData();
            }
        } // while

        return null;
    } // getData

    /**
     * ���������� �������� �������� ������, ������������ �������� ���� � ��������
     * ������� �����.
     *
     * @param tag
     * @param subFieldMarker
     * @return
     */
    public String getData(String tag, char subFieldMarker) {
        Iterator<IrbisField64> iter = fields.iterator();

        while (iter.hasNext()) {
            IrbisField64 f = iter.next();

            if (f.getTag().equals(tag)) {
                IrbisSubField64 sf = f.getSubFieldList().get(subFieldMarker);

                if (sf != null) {
                    return sf.getText();
                }
            }
        } // while

        return null;
    } // getData

    /**
     * @return
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return
     */
    public int getMfn() {
        return mfn;
    }

    /**
     * @param mfn
     */
    public void setMfn(int mfn) {
        this.mfn = mfn;
    }

    /**
     * @return
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(String.valueOf(mfn));

        buf.append('#').append(status).append("\r\n")
                .append("0#")
                .append(version)
                .append("\r\n");

        for (IrbisField64 f : fields) {
            buf.append(f.toString()).append("\r\n");
        } // for

        return buf.toString();
    } // toString

    /**
     * ������� ��������� ������������� ������, ������ � ������� ��������� �����
     * �������� � ������ #30#31.
     *
     * @param lineDelimiter
     * @return
     */
    public String toString(String lineDelimiter) {
        StringBuilder buf = new StringBuilder(String.valueOf(mfn));

        buf.append('#').append(status).append(lineDelimiter)
                .append("0#")
                .append(version)
                .append(lineDelimiter);

        for (IrbisField64 f : fields) {
            buf.append(f.toString()).append(lineDelimiter);
        } // for

        return buf.toString();
    } // toString

    /**
     *
     * @return
     */
    public String exportToCommunicationTextFormat() {
        StringBuilder buf = new StringBuilder();

        for (IrbisField64 f : fields) {
            buf.append('#').append(f.getTag()).append(": ");
            buf.append(f.toString()).append("\r\n");
        } // for

        buf.append("*****");

        return buf.toString();
    } // exportToCommunicationTextFormat

    /**
     *
     * @param field
     */
    public void addField(IrbisField64 field) {
        fields.add(field);
    } // addField

    /**
     * ���������� ���� � ������.
     *
     * @param tag
     * @param data
     */
    public void addField(String tag, String data) {
        IrbisField64 fld = IrbisField64.parse(data);
        fld.setTag(tag);

        fields.add(fld);
    } // addField
} // class IrbisRecord64
