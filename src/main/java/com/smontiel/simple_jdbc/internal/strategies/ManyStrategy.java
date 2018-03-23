package com.smontiel.simple_jdbc.internal.strategies;

import com.smontiel.simple_jdbc.ThrowingFunction;
import com.smontiel.simple_jdbc.Utils;
import com.smontiel.simple_jdbc.internal.Handlerable;
import com.smontiel.simple_jdbc.internal.Sqlable;
import com.smontiel.simple_jdbc.internal.Strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
class ManyStrategy<T> implements Strategy<List<T>>, Sqlable, Handlerable<ResultSet, T> {
    private static ManyStrategy ourInstance = new ManyStrategy<>();

    public static <T> ManyStrategy<T> getInstance() {
        return ourInstance;
    }

    private String sqlQuery;
    private ThrowingFunction<ResultSet, T> handler;

    private ManyStrategy() {}

    @Override
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    @Override
    public void setHandler(ThrowingFunction<ResultSet, T> handler) {
        this.handler = handler;
    }

    @Override
    public List<T> execute(Connection connection) {
        try (Connection conn = connection;
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet resultSet = stmt.executeQuery()) {
            List<T> list = new ArrayList<>();

            while (resultSet.next()) {
                T instance = handler.apply(resultSet);
                list.add(instance);
            }
            if (list.size() == 0) {
                throw new IllegalStateException("No data returned from the query.");
            } else return list;
        } catch (Exception e) {
            Utils.printSQLException((SQLException) e);
            throw new RuntimeException(e);
        }
    }
}
