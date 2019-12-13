package com.example.myapplication;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: usr
 * Date: 29.11.2014
 * Time: 6:14:40
 * ?????, ?????????????? ?????????????? ? ???????? ?????64
 */
public class IrbisClient64 {

    public static String SYS_PATH = "0";
    public static String DATAI_PATH = "1";
    public static String MASTER_FILE = "2";
    public static String DICTIONARY_FILE = "3";
    public static String IN_DB_PATH = "10";
    private static boolean isValidIpV4(String input) {
        String[] octets = input.split("\\.");
        int n = octets.length;
        if (n != 3) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            int octet;
            try {
                octet = Integer.valueOf(octets[i]);
            }
            catch (NumberFormatException e) {
                return false;
            }
            if (octet < 0 || octet > 255) {
                return false;
            }
        } // for (int i = 0; i < 3; i++)

        return true;
    } // isValidIpV4

    private static final char LINE_BREAK = '\n';


    class BufferFragment {
        byte[] buffer;
        int length;

        BufferFragment(byte[] buffer, int length) {
            this.buffer = buffer;
            this.length = length;
        } // BufferFragment
    } // BufferFragment

    public static final String ENCODING_ANSI = "windows-1251";
    private static final String ENCODING_UTF8 = "utf-8";
    public static final char SERVER_COMMAND_REGISTRATION = 'A';
    public static final char SERVER_COMMAND_BUY = 'B';
    public static final char SERVER_COMMAND_CLIENT_ALIVE = 'N';
    public static char SERVER_COMMAND_WRITE_RECORD = 'D';
    public static final char SERVER_COMMAND_SERVER_VERSION = '1';
    public static final char SERVER_COMMAND_READ_RECORD = 'C';
    public static final char SERVER_COMMAND_MAX_MFN = 'O';
    public static final char SERVER_COMMAND_SEARCH = 'K';
    public static final char SERVER_COMMAND_FORMAT_RECPRD_BY_MFN = 'G';
    public static final char SERVER_COMMAND_LOAD_TEXT_RESOURCE = 'L';
    public static final char SERVER_COMMAND_SEQUENCE_SEARCH = 'K';
    public static final char ERVER_COMMAND_GET_TERMIN_LIST = 'H';
    public static final char ERVER_COMMAND_UNLOCK_RECORD = 'Q';
    private String server;
    private int port;
    private int timeOutMs = 2000;
    private String username;
    private String password;
    private String database;
    private String userId;
    private int commandNo = 1;
    private String alivaTime = "60";
    private ClientConfiguration configuration;
    private char clientType = 'C';
    private static Random rnd = new Random(System.currentTimeMillis());
    private int socketTimeout = 60;
    private static String generateUserId() {
        String s = String.valueOf(rnd.nextLong());

        int idx = s.length() - 6;

        if (idx < 0) {
            idx = 0;
        }

        return "1" + s.substring(idx);
    } // generateUserId
    public IrbisClient64() {
        server = "127.0.0.1";
        port = 6666;
        username = "master";
        password = "MASTERKEY";
        database = "IBIS";
        userId = generateUserId();
    } // IrbisClient64

    public IrbisClient64(String server, int port, String username, String password, String database) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        userId = generateUserId();
    }

    private String convertBooleanToString(boolean b) {
        return b ? "1" : "0";
    } // convertBooleanToInt
    public String getUserId() {
        return userId;
    } // getUserId
    private void checkReturnCode(String code) throws IrbisClient64Exception {
        if (code.startsWith("-")) {
            if (!"-202;-203;-204".contains(code)) {
                // ???? ?????? ?????? ?????? ? ????????? ??? ????????? ?????????? ??????,
                // ?? ??????????????? ?????? ??????, ????? - ????????? ??????????.
                if (IrbisClient64Exception.REC_DELETE.equals(code) || IrbisClient64Exception.REC_PHYS_DELETE.equals(code)) {
                    return;
                }

                throw new IrbisClient64Exception(code);
            }
        } // if
    } // checkReturnCode
    private boolean successReturn(String code) {
        return IrbisClient64Exception.ERROR_ZERO.equals(code);
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeOutMs() {
        return timeOutMs;
    }

    public void setTimeOutMs(int timeOutMs) {
        this.timeOutMs = timeOutMs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public char getClientType() {
        return clientType;
    }

    public void setClientType(char clientType) {
        this.clientType = clientType;
    }

    private Socket tcpConnect() throws IOException {
        if (!isValidIpV4(server)) {
            InetAddress host = InetAddress.getByName(server);
            server = host.getHostAddress();
        } // if

        final Socket socket = new Socket();

        socket.connect(new InetSocketAddress(server, port), timeOutMs);

        return socket;
    } // TcpConnect

    private void tcpDisconect(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e) {
            // No-op.
        }
    } // tcpDisconect

    /**
     * ????????? ????? ??? ???????? ??????? ??? ??????. ???????? ?????? ?
     * ???????? ????????? ?????????? ???????.
     *
     * @param command              ??? ???????
     * @param userData             ?????? ???????????? (????????, ????? ? ??????)/
     * @param userDataEncoding     ????????? ????? ???????????????? ??????.
     * @param serverAnswerEncoding ????????? ?????? ???????.
     * @return ?????? ????? ?? ????????? ?????? ??????? (?????? 10 ????? ?????????)/
     * @throws IrbisClient64Exception
     */
    public List<String> executeCommand(char command, String userData, String userDataEncoding, String serverAnswerEncoding)
            throws IrbisClient64Exception {
        Socket socket = null;

        if (userData == null) {
            userData = "";
        }

        try {
            StringBuilder queryHeader = new StringBuilder(100);

            queryHeader.append(command).                    // ??? ???????
                    append("\n").                   // ??????????? ?????
                    append(clientType).             // ??? ???? ??????? (?????? ?????????????)
                    append("\n").
                    append(command).                // ?????????? ???? ???????
                    append("\n").
                    append(userId).                 // ????????????? ????????????
                    append("\n").
                    append(commandNo++).              // ?????????? ????? ???????
                    append("\n").
                    append(password).               // ??????
                    append("\n").
                    append(username).               // ??? ????????????
                    append("\n\n\n\n");               // ?????? ?????? (?????????? ?????????).

            // ?????????????? ?????? ? ???????? ????? (????????? ANSI).
            byte[] b2 = queryHeader.toString().getBytes("windows-1251");

            // ????????????? ???????????????? ?????? ? ???????? ????? (????????? UTF-8)?
            byte[] b3 = userData.getBytes(userDataEncoding);

            // ?????, ??????????? ????????? ????????????? ????? ??????.
            byte[] b1 = String.valueOf(b2.length + b3.length).getBytes("windows-1251");

            // ??????? ?????, ??????? ????? ????????? ?? ??????.
            byte[] bts = new byte[b1.length + b2.length + b3.length + 1];

            // ???????????? ???????? ??????
            // 1. ????????? ????????????? ????? ??????
            System.arraycopy(b1, 0, bts, 0, b1.length);
            // 2. ??????? ??????? \n ????? ?????????? ????????????? ????? ??????
            bts[b1.length] = 10;
            int offset = b1.length + 1;
            // 3. ??????????? ????????? ??????
            System.arraycopy(b2, 0, bts, offset, b2.length);
            // 4. ??????????? ????????????????? ??????.
            offset += b2.length;
            System.arraycopy(b3, 0, bts, offset, b3.length);

            // ???????? ??????
            // ????????? TCP-?????????? ? ????????
            (socket = tcpConnect()).getOutputStream().write(bts);

            // ?????? ?????????? ?????? ?????????? ??????
            List<BufferFragment> bufList = new LinkedList<BufferFragment>();
            int totalBufSize = 0;

            final int BUFFER_SIZE = 5120;
            InputStream is = socket.getInputStream();

            while (true) {
                byte[] tmpBuf = new byte[BUFFER_SIZE];

                // ????????? ?????? ?????? ?? ???????
                int nBytesRead = is.read(tmpBuf);

                // ???? ?????? ?? ?????????, ?? ??????? ? ?????? ??????
                if (nBytesRead == -1) {
                    break;
                }

                // ????????? ??????? ????? ????? ??????
                totalBufSize += nBytesRead;

                // ???????? ???????? ?????? ? ??????
                bufList.add(new BufferFragment(tmpBuf, nBytesRead));
            }
            Iterator<BufferFragment> iter = bufList.iterator();

            byte[] packetBuffer = new byte[totalBufSize];

            offset = 0;

            while (iter.hasNext()) {
                BufferFragment bf = iter.next();

                java.lang.System.arraycopy(bf.buffer, 0, packetBuffer, offset, bf.length);

                offset += bf.length;
            } /// while (iter.hasNext())

            // ?????? ???? ?????? - ??? ???????, ???????????? ????????.
            // ?? ????? ??? ?????????, ? ????? ????????? ????????? ??????????????
            // ????????? ?????? ? ??????: ???? ??????? A. ?? ????????????
            // windows-1251, ????? - urf-8.
            char cmd = (char) packetBuffer[0];

//            if (cmd == 'D') {
//                writeByteArrayToFile(packetBuffer, "c:\\tmp\\buf.txt");
//            }

            BufferedReader in = new BufferedReader(new StringReader(
                    new String(packetBuffer, 0, totalBufSize, serverAnswerEncoding)
            ));

            // ?????????? ?????? 10 ????? ?????? (??? ?? ???????? ??????? ???????? ??????????)
            for (int i = 0; i < 10; i++) {
                in.readLine();
            }

            List<String> result = new ArrayList<String>(15);

            // ????????? ?????? - ???? ??????
            String s;
            while ((s = in.readLine()) != null) {
                result.add(s);
            } // while

            return result;
        } // try
        catch (IOException e) {
            throw new IrbisClient64Exception(e, IrbisClient64Exception.OTHER_ERROR, e.getMessage());
        }
        finally {
            tcpDisconect(socket);
        }
    } // executeCommand

    public void connect() throws IrbisClient64Exception {
        userId = generateUserId();

        StringBuilder regData = new StringBuilder(20);

        // ???????????? ??????????????? ??????
        regData.append(username).append(LINE_BREAK).append(password);

        // ?????????? ??????? ?? ??????? ? ????????? ??????
        List<String> answer = executeCommand(SERVER_COMMAND_REGISTRATION, regData.toString(), ENCODING_ANSI, ENCODING_ANSI);

        // ?????? ?????? - ??? ??????? ?????????? ???????
        IrbisClient64Exception.checkReturnCode(answer.get(0));

        // ?????? ?????? - ????? ????? ??????? ??? ?????????????
        alivaTime = answer.get(1);

        // ????? ??????? ???-???? ????????????
        configuration = new ClientConfiguration(answer, 2);
    } // connect

    public void disconnect() {
        try {
            executeCommand(SERVER_COMMAND_BUY, null, ENCODING_ANSI, ENCODING_ANSI);
        }
        catch (IrbisClient64Exception e) {
            // No-op.
        }
    } // disconnect

    public int getMaxMfn() throws IrbisClient64Exception {
        List<String> answer = executeCommand(SERVER_COMMAND_MAX_MFN, database, ENCODING_ANSI, ENCODING_UTF8);

        final String s = answer.get(0);

        IrbisClient64Exception.checkReturnCode(s);

        return Integer.parseInt(s);
    } // getMaxMfn


    public List<Integer> search(String searchExpr) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(database);

        userData.append(LINE_BREAK)
                .append(searchExpr).append(LINE_BREAK)
                .append('0').append(LINE_BREAK)
                .append('1').append(LINE_BREAK)
                .append(LINE_BREAK);

        List<String> answer = executeCommand(SERVER_COMMAND_SEARCH,
                userData.toString(),
                ENCODING_UTF8,
                ENCODING_UTF8
        );

        checkReturnCode(answer.get(0));

        // ?????????? ????? ????????? ???????.
        int recordsCount = Integer.valueOf(answer.get(1));

        if (recordsCount == 0) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<Integer>(recordsCount);

        final int n = answer.size();

        // ????????? ? ?????????????? ?????? ???? ????????? ???????.
        for (int i = 2; i < n; i++) {
            result.add(Integer.valueOf(answer.get(i)));
        } // for

        return result;
    }
    public List<Integer> search(String searchExpr, int returnedRecordCount, int firstReturnedRecordNum) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(database); // ��� ��

        userData.append(LINE_BREAK) // �����������
                .append(searchExpr) // ��������� ���������
                .append(LINE_BREAK) // �����������
                .append(returnedRecordCount) // ����� ������������ �������
                .append(LINE_BREAK)        // �����������
                .append(firstReturnedRecordNum) // ����� ������ ������������ ������
                .append(LINE_BREAK) // �����������
                .append(LINE_BREAK); // �����������

        List<String> answer = executeCommand(SERVER_COMMAND_SEARCH,
                userData.toString(),
                ENCODING_UTF8,
                ENCODING_UTF8
        );

        checkReturnCode(answer.get(0));

        // ?????????? ????? ????????? ???????.
        int recordsCount = Integer.valueOf(answer.get(1));

        if (recordsCount == 0) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<Integer>(recordsCount);

        final int n = answer.size();

        // ????????? ? ?????????????? ?????? ???? ????????? ???????.
        for (int i = 2; i < n; i++) {
            result.add(Integer.valueOf(answer.get(i)));
        } // for

        return result;
    } // search

    /**
     * ��������� ���������� ��������� �������.
     *
     * @param searchExpr ��������� ���������.
     * @return ���-�� �����������.
     * @throws IrbisClient64Exception
     */
    public int getSearchResultSize(String searchExpr) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(database);

        userData.append(LINE_BREAK)
                .append(searchExpr).append(LINE_BREAK)
                .append('0').append(LINE_BREAK)
                .append('0').append(LINE_BREAK)
                .append(LINE_BREAK);

        List<String> answer = executeCommand(SERVER_COMMAND_SEARCH, userData.toString(), ENCODING_UTF8, ENCODING_UTF8);

        checkReturnCode(answer.get(0));

        return Integer.parseInt(answer.get(1));
    } // getSearchResultSize

    /**
     * ������ ������.
     *
     * @param mfn      ����� ������ � ��.
     * @param needLock ����� �� ����������?
     * @return ������, ����������� ������� ������.
     * @throws IrbisClient64Exception
     */
    public IrbisRecord64 readRecord(int mfn, boolean needLock) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(20);

        userData.append(database).
                append(LINE_BREAK).append(mfn).
                append(LINE_BREAK).append(convertBooleanToString(needLock));

        List<String> answer = executeCommand(SERVER_COMMAND_READ_RECORD, userData.toString(), ENCODING_UTF8, ENCODING_UTF8);

        checkReturnCode(answer.get(0));

        // ????????? ? ?????????? ?????????? ??????
        IrbisRecord64 rec = IrbisRecord64.parse(answer, 1);

        rec.setDatabase(database);

        return rec;
    } // readRecord

    /**
     * �������� ������ �������.
     *
     * @return
     * @throws IrbisClient64Exception
     */
    public ServerVersion getServerVersion() throws IrbisClient64Exception {
        // TODO: ???????? ????????? ??? ?????????????? ?????? ? ??????.
        List<String> answer = executeCommand(SERVER_COMMAND_SERVER_VERSION, null, ENCODING_ANSI, ENCODING_ANSI);

        checkReturnCode(answer.get(0));

        ServerVersion sv = new ServerVersion();

        sv.version = answer.get(1);
        sv.activeClientsCount = Integer.parseInt(answer.get(2));
        sv.maxConnections = Integer.parseInt(answer.get(3));

        return sv;
    } // getServerVersion

    /**
     * ������ �������. ������ ������ �� ������. ������ ������ ��������, ��� �� ��� "�����".
     *
     * @throws IrbisClient64Exception
     */
    public void noOp() throws IrbisClient64Exception {
        List<String> answer = executeCommand(SERVER_COMMAND_CLIENT_ALIVE, null, ENCODING_ANSI, ENCODING_ANSI);

        checkReturnCode(answer.get(0));
    } // noOp

    /**
     * ���������� ������ � ��.
     *
     * @param record        ������.
     * @param needLock      ����� �� ���������� ������?
     * @param needActualize ����� �� ������������ �������?
     * @throws IrbisClient64Exception
     */
    public void writeRecord(IrbisRecord64 record, boolean needLock, boolean needActualize) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(database);
        userData.append(LINE_BREAK).append(convertBooleanToString(needLock))
                .append(LINE_BREAK).append(convertBooleanToString(needActualize))
                .append(LINE_BREAK);

        // ???????? ????????????? ?????? ? ???? ??????, ???? ? ??????? ?????????
        // ????? ???????? ? ?????? #30#31.
        StringBuilder buf = new StringBuilder(record.toString("\u001F\u001E"));

        userData.append(buf);

        List<String> answer = executeCommand(SERVER_COMMAND_WRITE_RECORD, userData.toString(), ENCODING_UTF8, ENCODING_UTF8);

        // ������ ������ ��� ������
        if (answer.size() == 1) {
            throw new IrbisClient64Exception(answer.get(0));
        }

        // ????????? ?????? ?????? ? ?? ???????
        String s = answer.get(1);

        int idx = s.indexOf('#');

        record.setMfn(Integer.parseInt(s.substring(0, idx)));
        record.setStatus(Integer.parseInt(s.substring(idx + 1)));

        // ????????? ?????? ??????
        if (answer.size() > 2) {
            s = answer.get(2);

            idx = s.indexOf('#');
            int idx1 = s.indexOf('\u001E');

            record.setVersion(Integer.parseInt(s.substring(idx + 1, idx1)));
        }
        else {
            record.setVersion(1);
        }
    } // writeRecord

    /**
     * ������ � ����������������� ����� ������.
     *
     * @param mfn    ����� ������ � ��.
     * @param format ������.
     * @return ������ �����, �������������� ������.
     * @throws IrbisClient64Exception
     */
    public String readFormatedRecord(int mfn, String format) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(20);

        userData.append(database)
                .append(LINE_BREAK).append(format)
                .append(LINE_BREAK).append(1)
                .append(LINE_BREAK).append(mfn);

        List<String> answer = executeCommand(SERVER_COMMAND_FORMAT_RECPRD_BY_MFN,
                userData.toString(), ENCODING_UTF8, ENCODING_UTF8);

        checkReturnCode(answer.get(0));

        StringBuilder buf = new StringBuilder(100);

        final int n = answer.size();

        for (int i = 1; i < n; i++) {
            buf.append(answer.get(i)).append(LINE_BREAK);
        } // for

        return buf.toString();
    } // readFormatedRecord

    /**
     * ����� � �� �� �������.
     *
     * @param searchExpr ��������� ���������
     * @param format     ������ ��� ������������� ����������� ������.
     * @return ������ ������������������ �������.
     * @throws IrbisClient64Exception
     */
    public List<String> search(String searchExpr, String format) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(database);

        userData.append(LINE_BREAK)
                .append(searchExpr)
                .append(LINE_BREAK).append('0')
                .append(LINE_BREAK).append('1')
                .append(LINE_BREAK).append(format)
                .append(LINE_BREAK);

        List<String> answer = executeCommand(SERVER_COMMAND_SEARCH,
                userData.toString(),
                ENCODING_ANSI,
                ENCODING_UTF8
        );

        checkReturnCode(answer.get(0));

        // ??????? ?????? ? ??????????? ????????? ?? ?????????? ????????? ???????.
        final int capacity = Integer.parseInt(answer.get(1));

        List<String> result = capacity > 0 ? new ArrayList<String>(capacity) : Collections.<String>emptyList();

        final int n = answer.size();

        for (int i = 2; i < n; i++) {
            final String s = answer.get(i);

            int idx = s.indexOf('#');

            result.add(s.replace('\u001F', '\n').substring(idx + 1));
        } // for

        return result;
    } // search

    /**
     * ������ ������� � ����������� �� �����������������.
     *
     * @param mfnList ?????? ???????.
     * @param format  ?????? ?? ????? ISIS.
     * @return ?????? ?????. ?????? ?????? - ????????? ?????????????? ????? ??????.
     * @throws IrbisClient64Exception
     */
    public List<String> readFormattedRecords(List<Integer> mfnList, String format) throws IrbisClient64Exception {
        StringBuilder userdata = new StringBuilder(database);

        int n = mfnList.size();

        userdata.append(LINE_BREAK)
                .append(format)
                .append(LINE_BREAK)
                .append(n)
                .append(LINE_BREAK);

        // ???????????? ??????? ??????? ??????? ??? ???????? ?? ??????.
        for (int mfn : mfnList) {
            userdata.append(mfn).append(LINE_BREAK);
        } // for

        List<String> answer = executeCommand(SERVER_COMMAND_FORMAT_RECPRD_BY_MFN,
                userdata.toString(),
                ENCODING_UTF8,
                ENCODING_UTF8
        );

        checkReturnCode(answer.get(0));

        n = answer.size();

        List<String> result = new ArrayList<String>(n);

        for (int i = 1; i < n; i++) {
            String s = answer.get(i);

            int idx = s.indexOf('#');

            result.add(s.replace('\u001F', '\n').substring(idx + 1));
        } // for

        return result;
    } // readFormattedRecords

    /** */
    private static String DELIMITER = "\u001F\u001E";

    /** */
    private static int DELIMITER_LEN = DELIMITER.length();

    /**
     * �������� ���������� ������� �� ��.
     *
     * @param filename ��� ����� ������ ��.
     * @return ������ ����� ���������� �����.
     * @throws IrbisClient64Exception
     */
    public List<String> loadTextResource(String filename, String pathCode) throws IrbisClient64Exception {
        StringBuilder userData = new StringBuilder(pathCode);

        userData.append('.')
                .append(pathCode.equals("0") || pathCode.equals("1") ? "" : database)
                .append('.')
                .append(filename);

        List<String> answer = executeCommand(SERVER_COMMAND_LOAD_TEXT_RESOURCE,
                userData.toString(), ENCODING_ANSI, ENCODING_ANSI);

        if (answer.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<String>(10);

        String[] lines = answer.get(0).split(DELIMITER);

        for (String s : lines) {
            result.add(s);
        }

//        String src = answer.get(0);
//        int startIdx = 0;
//
//        while (true) {
//            int idx = src.indexOf(DELIMITER, startIdx);
//            if (idx != -1) {
//                result.add(src.substring(startIdx, idx));
//                startIdx += DELIMITER_LEN;
//                if (startIdx < idx) {
//                    startIdx++;
//                }
//            }
//            else {
//                result.add(src.substring(startIdx));
//                break;
//            }
//        } // while (true)
//
        return result;
    } // loadTextResource

    /**
     * ���������������� ����� � ��.
     * �� ��������!
     *
     * @param searchExpr      ????????? ?????????.
     * @param numRecords      ?????????? ??????????? ???????.
     * @param firstRecord      ????? ?????? ???????? ??????.
     * @param minMfn          ???????? ?????? (?????? ???????)
     * @param maxMfn          ???????? ??????? (??????? ???????)
     * @param sequenceExpr    ????????? ??? ????????????????? ??????.
     * @return                ?????? VFN ???????? ???????.
     * @throws IrbisClient64Exception
     */
    public List<Integer> sequenceSearch(String searchExpr, int numRecords, int firstRecord, int minMfn, int maxMfn, String sequenceExpr) throws IrbisClient64Exception {
        StringBuilder userdata = new StringBuilder(database).append(LINE_BREAK);

        userdata.append(searchExpr).append(LINE_BREAK)
                .append(numRecords).append(LINE_BREAK)
                .append(firstRecord).append(LINE_BREAK).append(LINE_BREAK)
                .append(minMfn).append(LINE_BREAK)
                .append(maxMfn).append(LINE_BREAK)
                .append(sequenceExpr);

        List<String> answer = executeCommand(SERVER_COMMAND_SEQUENCE_SEARCH,
                userdata.toString(),
                ENCODING_UTF8,
                ENCODING_UTF8);

        checkReturnCode(answer.get(0));

        // ?????????? ????? ????????? ???????.
        int recordsCount = Integer.parseInt(answer.get(1));

        if (recordsCount == 0) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<Integer>(recordsCount);

        final int n = answer.size();

        // ????????? ? ?????????????? ?????? ???? ????????? ???????.
        for (int i = 2; i < n; i++) {
            result.add(Integer.parseInt(answer.get(i)));
        } // for

        return result;
    } // sequenceSearch

    /**
     * ���������������� ����� � ��.
     * �� ��������!
     * @param sequenceExpr ????????? ????????? ??? ????????????????? ??????.
     * @return ?????? MFN ????????? ???????.
     * @throws IrbisClient64Exception
     */
    public List<Integer> sequenceSearch(String sequenceExpr) throws IrbisClient64Exception {
        return sequenceSearch("", Integer.MAX_VALUE, 1, 0, 0, sequenceExpr);
    } // sequenceSearch

    /**
     * ���������������� ����� � ��.
     * �� ��������!
     *
     * @param searchExpr      ????????? ?????????.
     * @param numRecords      ?????????? ??????????? ???????.
     * @param firstRecord     ????? ?????? ???????? ??????.
     * @param minMfn          ???????? ?????? (?????? ???????)
     * @param maxMfn          ???????? ??????? (??????? ???????)
     * @param sequenceExpr    ????????? ??? ????????????????? ??????.
     * @param format          ?????? ??? ????????????? ???????.
     * @return                ?????? ????? ? ????????????? ??????.
     * @throws IrbisClient64Exception
     */
    public List<String> sequenceSearech(String searchExpr, int numRecords, int firstRecord, int minMfn, int maxMfn, String sequenceExpr, String format) throws IrbisClient64Exception {
        StringBuilder userdata = new StringBuilder(database).append(LINE_BREAK);

        userdata.append(searchExpr).append(LINE_BREAK)
                .append(numRecords).append(LINE_BREAK)
                .append(firstRecord).append(LINE_BREAK)
                .append(LINE_BREAK).append(format)
                .append(minMfn).append(LINE_BREAK)
                .append(maxMfn).append(LINE_BREAK)
                .append(sequenceExpr);

        List<String> answer = executeCommand(SERVER_COMMAND_SEQUENCE_SEARCH,
                userdata.toString(),
                ENCODING_UTF8,
                ENCODING_UTF8);

        checkReturnCode(answer.get(0));

        // ?????????? ????? ????????? ???????.
        int recordsCount = Integer.parseInt(answer.get(1));

        if (recordsCount == 0) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<String>(recordsCount);

        final int n = answer.size();

        for (int i = 2; i < n; i++) {
            final String s = answer.get(i);

            int idx = s.indexOf('#');

            result.add(s.replace('\u001F', '\n').substring(idx + 1));
        } // for

        return result;
    } // sequenceSearech

    /**
     *
     * @param startTermin
     * @param numTermins
     * @return
     * @throws IrbisClient64Exception
     */
    public List<Pair<Integer, String>> getTerminListAdv(String startTermin, int numTermins) throws IrbisClient64Exception {
        StringBuilder userdata = new StringBuilder(database).append(LINE_BREAK);

        userdata.append(startTermin)
                .append(LINE_BREAK)
                .append(String.valueOf(numTermins));

        List<String> answer = executeCommand(ERVER_COMMAND_GET_TERMIN_LIST,
                userdata.toString(),
                ENCODING_UTF8,
                ENCODING_UTF8);

        checkReturnCode(answer.get(0));

        int n = answer.size();

        List<Pair<Integer, String>> result = new ArrayList<Pair<Integer, String>>(n);

        for (int i = 1; i < n; i++) {
            String s = answer.get(i);

            int idx = s.indexOf('#');

            result.add(new Pair<>(Integer.valueOf(s.substring(0, idx)), s.substring(idx + 1)));
        } // for

        return result;
    } // getTerminList

    /**
     * ��������� ������ �������� �������, ������� � ���������.
     *
     * @param startTermin ��������� ������
     * @param numTermins ���������� ��������
     * @return ������ ��������
     * @throws IrbisClient64Exception
     */
    public List<String> getTerminList(String startTermin, int numTermins) throws IrbisClient64Exception {
        StringBuilder userdata = new StringBuilder(database).append(LINE_BREAK);

        userdata.append(startTermin)
                .append(LINE_BREAK)
                .append(String.valueOf(numTermins));

        List<String> answer = executeCommand(ERVER_COMMAND_GET_TERMIN_LIST,
                userdata.toString(),
                ENCODING_UTF8,
                ENCODING_UTF8);

        checkReturnCode(answer.get(0));

        int n = answer.size();

        List<String> result = new ArrayList<String>(n);

        for (int i = 1; i < n; i++) {
            String s = answer.get(i);

            int idx = s.indexOf('#');

            result.add(s.substring(idx + 1));
        } // for

        return result;
    } // getTerminList

    /**
     * �� ��������!
     * ���������������� ����� � ��.
     * @param sequenceExpr    ????????? ????????? ??? ????????????????? ??????.
     * @param format          ?????? ????????????? ???????.
     * @return                ?????? ??????????????????? ???????.
     * @throws IrbisClient64Exception
     */
    public List<String> sequenceSearech(String sequenceExpr, String format) throws IrbisClient64Exception { {
        return sequenceSearech("", Integer.MAX_VALUE, 1, 0, 0, sequenceExpr, format);
    }}

    /**
     * ?????????????????????????????
     * @param src
     * @param filename
     */
    private void writeByteArrayToFile(byte[] src, String filename) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(filename);
            fos.write(src);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } // try
            catch (Exception e) {}
        } // finally
    } // writeByteArrayToFile

    /**
     * ���������� ������������ �������.
     * @return
     */
    public ClientConfiguration getConfiguration() {
        return configuration;
    } // getConfiguration


} // class IrbisClient64
