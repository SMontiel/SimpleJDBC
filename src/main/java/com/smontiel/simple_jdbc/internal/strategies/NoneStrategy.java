package com.smontiel.simple_jdbc.internal.strategies;

import com.smontiel.simple_jdbc.Utils;
import com.smontiel.simple_jdbc.internal.Sqlable;
import com.smontiel.simple_jdbc.internal.Strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
class NoneStrategy implements Strategy<Integer>, Sqlable {
    private static NoneStrategy ourInstance = new NoneStrategy();

    public static NoneStrategy getInstance() {
        return ourInstance;
    }

    private String sqlQuery;

    private NoneStrategy() {}

    @Override
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    @Override
    public Integer execute(Connection connection) {
        try (Connection conn = connection;
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                throw new IllegalStateException("No return data was expected.");
            }

            Integer instance = stmt.getUpdateCount();
            return instance;
        } catch (SQLException e) {
            Utils.printSQLException((SQLException) e);
            throw new RuntimeException(e);
        }
    }
}
