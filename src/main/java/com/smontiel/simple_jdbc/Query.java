package com.smontiel.simple_jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Salvador Montiel on 28/november/2017.
 */
public class Query {
    private final Connection connection;
    private final String sqlQuery;

    Query(Connection connection, String sqlQuery) {
        this.connection = connection;
        this.sqlQuery = sqlQuery;
    }

    public <T> List<T> any(ThrowingFunction<ResultSet, T> handler) {
        return manyOrNone(handler);
    }

    public <T> List<T> manyOrNone(ThrowingFunction<ResultSet, T> handler) {
        Connection conn = connection;
        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                ResultSet resultSet = stmt.executeQuery()) {
            List<T> list = new ArrayList<>();

            while (resultSet.next()) {
                T instance = handler.apply(resultSet);
                list.add(instance);
            }
            return list;
        } catch (Exception e) {
            Utils.printSQLException((SQLException) e);
            throw new RuntimeException(e);
        }
    }

    public <T> T one(ThrowingFunction<ResultSet, T> handler) {
        Connection conn = connection;
        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                ResultSet resultSet = stmt.executeQuery()) {
            T instance = null;

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
            Utils.printSQLException((SQLException) e);
            throw new RuntimeException(e);
        }
    }

    public <T> T oneOrNone(ThrowingFunction<ResultSet, T> handler) {
        Connection conn = connection;
        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                ResultSet resultSet = stmt.executeQuery()) {
            T instance = null;

            if (resultSet.next()) {
                instance = handler.apply(resultSet);
            }
            while (resultSet.next()) {
                throw new IllegalStateException("Multiple rows were not expected.");
            }
            return instance;
        } catch (Exception e) {
            Utils.printSQLException((SQLException) e);
            throw new RuntimeException(e);
        }
    }

    public Integer none() {
        Connection conn = connection;
        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery);
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

    public <T> List<T> many(ThrowingFunction<ResultSet, T> handler) {
        Connection conn = connection;
        try (PreparedStatement stmt = conn.prepareStatement(sqlQuery);
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
