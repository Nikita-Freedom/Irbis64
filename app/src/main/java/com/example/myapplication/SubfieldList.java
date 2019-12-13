package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class SubfieldList {
    /** */
    private final List<IrbisSubField64> subfields = new ArrayList<IrbisSubField64>(5);

    /**
     *
     */
    public SubfieldList() {
        // No-op.
    } // SubfieldList

    /**
     *
     * @param marker
     * @param data
     */
    public void add(char marker, String data) {
        subfields.add(new IrbisSubField64(marker, data));
    } // add

    /**
     *
     * @param subfield
     */
    public void add(IrbisSubField64 subfield) {
        subfields.add(subfield);
    } // add

    /**
     *
     * @param subfield
     */
    public void remove(IrbisSubField64 subfield) {
        subfields.remove(subfield);
    } // renove

    /**
     *
     * @param marker
     */
    public void remove(char marker) {
        for (IrbisSubField64 sf : subfields) {
            if (sf.getMarker() == marker) {
                subfields.remove(sf);

                break;
            }
        }
    } // remove

    /**
     *
     * @param marker
     * @return
     */
    public IrbisSubField64 get(char marker) {
        marker = Character.toUpperCase(marker);

        for (IrbisSubField64 sf : subfields) {
            if (Character.toUpperCase(sf.getMarker()) == marker) {
                return sf;
            } // if
        } // for

        return null;
    } // get

    /**
     *
     * @param marker
     * @return
     */
    public String getText(char marker) {
        marker = Character.toUpperCase(marker);

        for (IrbisSubField64 sf : subfields) {
            if (Character.toUpperCase(sf.getMarker()) == marker) {
                return sf.getText();
            } // if
        } // for

        return null;
    } // getText

    /**
     *
     * @return
     */
    public List<IrbisSubField64> getSubFields() {
        return subfields;
    } // getSubFields
} // class SubfieldList
