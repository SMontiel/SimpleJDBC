package com.smontiel.simple_jdbc.internal.strategies;

import com.smontiel.simple_jdbc.Executor;
import com.smontiel.simple_jdbc.ThrowingFunction;
import com.smontiel.simple_jdbc.Utils;
import com.smontiel.simple_jdbc.internal.Handlerable;
import com.smontiel.simple_jdbc.internal.Strategy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
class TransactionStrategy<R> implements Strategy<R>, Handlerable<Executor, R> {
    private static TransactionStrategy ourInstance = new TransactionStrategy<>();

    public static <T> TransactionStrategy<T> getInstance() {
        return ourInstance;
    }

    private ThrowingFunction<Executor, R> handler;

    private TransactionStrategy() {}

    @Override
    public void setHandler(ThrowingFunction<Executor, R> handler) {
        this.handler = handler;
    }

    @Override
    public R execute(Connection connection) {
        Connection noCloseConnection = new NoCloseConnection(connection);
        R instance;
        try {
            noCloseConnection.setAutoCommit(false);
            instance = handler.apply(new TransactionExecutor(noCloseConnection));
            noCloseConnection.commit();
        } catch (Exception e ) {
            Utils.printSQLException((SQLException) e);
            if (noCloseConnection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    noCloseConnection.rollback();
                } catch(SQLException excep) {
                    Utils.printSQLException(excep);
                    throw new RuntimeException(excep);
                }
            }
            throw new RuntimeException(e);
        } finally {
            try {
                noCloseConnection.setAutoCommit(true);

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Utils.printSQLException(e);
                throw new RuntimeException(e);
            }
            connection = null;
        }

        return instance;
    }

    private class TransactionExecutor implements Executor {
        private Connection connection;

        private TransactionExecutor(Connection connection) {
            this.connection = connection;
        }

        @Override
        public <T> List<T> any(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            return manyOrNone(sqlQuery, handler);
        }

        @Override
        public <T> List<T> many(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            try {
                Strategy<List<T>> s = StrategyFactory.many(sqlQuery, handler);
                return s.execute(connection);
            } catch (Exception e) {
                Utils.printSQLException((SQLException) e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> List<T> manyOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            try {
                Strategy<List<T>> s = StrategyFactory.manyOrNone(sqlQuery, handler);
                return s.execute(connection);
            } catch (Exception e) {
                Utils.printSQLException((SQLException) e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public Integer none(String sqlQuery) {
            try {
                Strategy<Integer> s = StrategyFactory.none(sqlQuery);
                return s.execute(connection);
            } catch (Exception e) {
                Utils.printSQLException((SQLException) e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> T one(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            try {
                Strategy<T> s = StrategyFactory.one(sqlQuery, handler);
                return s.execute(connection);
            } catch (Exception e) {
                Utils.printSQLException((SQLException) e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> T oneOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            try {
                Strategy<T> s = StrategyFactory.oneOrNone(sqlQuery, handler);
                return s.execute(connection);
            } catch (Exception e) {
                Utils.printSQLException((SQLException) e);
                throw new RuntimeException(e);
            }
        }
    }
}
