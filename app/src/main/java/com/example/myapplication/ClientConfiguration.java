package com.example.myapplication;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: usr
 * Date: 07.12.2014
 * Time: 14:48:40
 * ������������ �������
 */
public class ClientConfiguration {
    /** */
    private Map<String, Map<String, String>> sections = new HashMap<String, Map<String, String>>(50);

    /**
     * @param lines
     * @param startIndex
     */
    public ClientConfiguration(List<String> lines, int startIndex) {
        parse(lines, startIndex);
    } // ClientConfiguration

    /**
     * @param lines
     * @param startIndex
     */
    private void parse(List<String> lines, int startIndex) {
        Map<String, String> currentSection = new HashMap<String, String>(10);

        // ������ � ������ ������.
        sections.put("", currentSection);

        final int n = lines.size();

        for (int i = startIndex; i < n; i++) {
            String s = lines.get(i);

            // ��� ������...
            if (s.startsWith("[")) {
                int idx1 = s.indexOf(']');

                if (idx1 == -1) {
                    idx1 = s.length() - 1;
                }

                String sectionName = s.substring(1, idx1).toUpperCase();

                Map<String, String> sectionContents = sections.get(sectionName);

                if (sectionContents == null) {
                    sectionContents = new HashMap<String, String>(10);

                    sections.put(sectionName, sectionContents);

                    currentSection = sectionContents;
                }
            } // if
            else {
                int idx = s.indexOf('=');

                if (idx == -1) {
                    continue;
                }

                String name = s.substring(0, idx).toUpperCase();

                String value = "";

                if (idx < s.length()) {
                    value = s.substring(idx + 1);
                }

                currentSection.put(name, value);
            } // else
        } // for
    } // parse

    /**
     * ��������� �������� ���������.
     *
     * @param section      ��� ������
     * @param name         ��� ���������
     * @param defaultValue �������� �� ��������� (����� ���������� ���, ���� �������� � ������ ������ � ������ �� ������).
     * @return �������� ���������.
     */
    public String getParameter(String section, String name, String defaultValue) {
        Map<String, String> sectionContents = sections.get(section.toUpperCase());

        if (sectionContents == null) {
            return defaultValue;
        }

        String result = sectionContents.get(name.toUpperCase());

        return result != null ? result : defaultValue;
    } // getParameter
} // class ClientConfiguration