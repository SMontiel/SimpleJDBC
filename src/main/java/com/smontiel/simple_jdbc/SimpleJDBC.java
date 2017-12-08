package com.smontiel.simple_jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Salvador Montiel on 28/november/2017.
 */
public final class SimpleJDBC {
    public static SimpleJDBC from(DataSource dataSource) {
        return new SimpleJDBC(dataSource);
    }

    private Connection connection;

    private SimpleJDBC(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            Utils.printSQLException(e);
            throw new RuntimeException(e);
        }
    }

    public boolean isConnectionReady() {
        return connection != null;
    }

    public void close() {
        Utils.close(connection);
    }

    public synchronized Query query(String sqlQuery) {
        if (!isConnectionReady())
            throw new RuntimeException("Some DataSource values may be wrong");

        return new Query(connection, sqlQuery);
    }
}
