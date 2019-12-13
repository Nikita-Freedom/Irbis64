package com.example.myapplication;
import java.util.List;

/**
 * ���������������� �����.
 */
public class ThreadSafeIrbis64Client {
    /** */
    private IrbisClient64 client;

    /**
     *
     * @param host
     * @param port
     * @param username
     * @param password
     * @param dbnam
     */
    public ThreadSafeIrbis64Client(String host, int port, String username, String password, String dbnam) {
        this.client = new IrbisClient64(host, port, username, password, dbnam);
    } // ThreadSafeIrbis64Client

    /**
     *
     * @return
     */
    public synchronized char getCLientType() {
        return client.getClientType();
    }

    /**
     *
     * @param type
     */
    public synchronized void setClientType(char type) {
        client.setClientType(type);
    }

    /**
     *
     * @return
     */
    public synchronized String getdatabase() {
        return client.getDatabase();
    }

    /**
     *
     * @param database
     */
    public synchronized void setDatabase(String database) {
        client.setDatabase(database);
    }

    /**
     *
     * @return
     */
    public synchronized ClientConfiguration getConfiguration() {
        return client.getConfiguration();
    }
    /**
     *
     * @return
     */
    public synchronized String getServer() {
        return client.getServer();
    }

    /**
     *
     * @param server
     */
    public synchronized void serServer(String server) {
        client.setServer(server);
    }

    /**
     *
     * @return
     */
    public synchronized int getPort() {
        return client.getPort();
    }

    /**
     *
     * @param port
     */
    public synchronized void setPort(int port) {
        client.setPort(port);
    }

    /**
     *
     */
    public synchronized String getUsername() {
        return client.getUsername();
    }

    /**
     *
     * @param username
     */
    public synchronized void setUsername(String username) {
        client.setUsername(username);
    }

    /**
     *
     * @return
     */
    public synchronized String getPassword() {
        return client.getPassword();
    }

    /**
     *
     * @param password
     */
    public synchronized void setPassword(String password) {
        client.setPassword(password);
    }

    /**
     *
     * @throws IrbisClient64Exception
     */
    public synchronized void connect() throws IrbisClient64Exception {
        client.connect();
    }

    /**
     *
     */
    public synchronized void disconnect() {
        client.disconnect();
    }

    /**
     *
     * @return
     * @throws IrbisClient64Exception
     */
    public synchronized void setTimeOutMs(int timeout) {
        client.setTimeOutMs(timeout);
    }

    /**
     *
     * @return
     */
    public synchronized int getTimeOutMs() {
        return client.getTimeOutMs();
    }

    /**
     *
     * @param timeout
     */
    public synchronized void setTimeoutMills(int timeout) {
        client.setTimeOutMs(timeout);
    }

    /**
     *
     * @param filename
     * @param pathCode
     * @return
     * @throws IrbisClient64Exception
     */
    public synchronized List<String> loadTextResource(String filename, String pathCode) throws IrbisClient64Exception {
        return client.loadTextResource(filename, pathCode);
    }

    /**
     *
     * @param searchExpression
     * @return
     * @throws IrbisClient64Exception
     */
    public List<Integer> search(String searchExpression) throws IrbisClient64Exception {
        return client.search(searchExpression);
    }

    /**
     *
     * @param mfn
     * @param format
     * @return
     * @throws IrbisClient64Exception
     */
    public synchronized String readFormatedRecord(int mfn, String format) throws IrbisClient64Exception {
        return client.readFormatedRecord(mfn, format);
    }

    /**
     *
     * @param startTermin
     * @param count
     * @return
     * @throws IrbisClient64Exception
     */
    public synchronized List<String> getTerminList(String startTermin, int count) throws IrbisClient64Exception {
        return client.getTerminList(startTermin, count);
    }

    /**
     *
     * @param startTermin
     * @param count
     * @return
     * @throws IrbisClient64Exception
     */
    public synchronized List<Pair<Integer, String>> getTerminListAdv(String startTermin, int count) throws IrbisClient64Exception {
        return client.getTerminListAdv(startTermin, count);
    }

    /**
     *
     * @throws IrbisClient64Exception
     */
    public synchronized void noOp() throws IrbisClient64Exception {
        client.noOp();
    }

    /**
     *
     * @param mfn
     * @param needLock
     * @return
     * @throws IrbisClient64Exception
     */
    public synchronized IrbisRecord64 readRecord(int mfn, boolean needLock) throws IrbisClient64Exception {
        return client.readRecord(mfn, needLock);
    }

    /**
     *
     * @param record
     * @param needLock
     * @param actualize
     * @throws IrbisClient64Exception
     */
    public synchronized void writeRecord(IrbisRecord64 record, boolean needLock, boolean actualize) throws IrbisClient64Exception {
        client.writeRecord(record, needLock, actualize);
    }
} // ThreadSafeIrbis64Client
