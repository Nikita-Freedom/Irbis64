package com.example.myapplication;


/**
 * Created by IntelliJ IDEA.
 * User: usr
 * Date: 29.11.2014
 * Time: 6:41:12
 * ������������ ������� ���� ��������� �����
 */
public class IrbisSubField64 {
    /** ������ �������. */
    private char marker;

    /** ����� �������. */
    private String text;

    /**
     *
     */
    public IrbisSubField64() {
        // No-op.
    }
    /**
     * �����������.
     *
     * @param marker
     * @param text
     */
    public IrbisSubField64(char marker, String text) {
        this.marker = marker;
        this.text = text;
    }

    /**
     *
     * @return
     */
    public char getMarker() {
        return marker;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param marker
     */
    public void setMarker(char marker) {
        this.marker = marker;
    }

    /**
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("^");
        buf.append(marker).append(text);
        return buf.toString();
    } // toString
} // IrbisSubField64
