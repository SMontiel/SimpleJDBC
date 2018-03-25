package com.smontiel.simple_jdbc.internal.strategies;

import com.smontiel.simple_jdbc.ThrowingFunction;
import com.smontiel.simple_jdbc.Utils;
import com.smontiel.simple_jdbc.internal.Handlerable;
import com.smontiel.simple_jdbc.internal.Sqlable;
import com.smontiel.simple_jdbc.internal.Strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
class OneStrategy<T> implements Strategy<T>, Sqlable, Handlerable<ResultSet, T> {
    private static OneStrategy ourInstance = new OneStrategy<>();

    public static <T> OneStrategy<T> getInstance() {
        return ourInstance;
    }

    private String sqlQuery;
    private ThrowingFunction<ResultSet, T> handler;

    private OneStrategy() {}

    @Override
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    @Override
    public void setHandler(ThrowingFunction<ResultSet, T> handler) {
        this.handler = handler;
    }

    @Override
    public T execute(Connection connection) {
        try (Connection conn = connection;
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet resultSet = stmt.executeQuery()) {
            T instance;

            if (resultSet.next()) {
                instance = handler.apply(resultSet);
            } else {
                throw new IllegalStateException("No data returned from the query.");
            }
            while (resultSet.next()) {
                throw new IllegalStateException("Multiple rows were not expected.");
            }
            return instance;
        } catch (Exception e) {
            Utils.printSQLException(e);
            throw new RuntimeException(e);
        }
    }
}
