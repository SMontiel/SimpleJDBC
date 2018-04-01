package com.smontiel.simple_jdbc.internal.strategies;

import com.smontiel.simple_jdbc.Executor;
import com.smontiel.simple_jdbc.ThrowingFunction;
import com.smontiel.simple_jdbc.Utils;
import com.smontiel.simple_jdbc.internal.Handlerable;
import com.smontiel.simple_jdbc.internal.Strategy;

import java.sql.Connection;
import java.sql.ResultSet;
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
            Utils.printSQLException(e);
            if (noCloseConnection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    noCloseConnection.rollback();
                } catch(Exception excep) {
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
            } catch (Exception e) {
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
            return execute(StrategyFactory.many(sqlQuery, handler));
        }

        @Override
        public <T> List<T> manyOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            return execute(StrategyFactory.manyOrNone(sqlQuery, handler));
        }

        @Override
        public Integer none(String sqlQuery) {
            return execute(StrategyFactory.none(sqlQuery));
        }

        @Override
        public <T> T one(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            return execute(StrategyFactory.one(sqlQuery, handler));
        }

        @Override
        public <T> T oneOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
            return execute(StrategyFactory.oneOrNone(sqlQuery, handler));
        }

        private <S> S execute(Strategy<S> s) {
            try {
                return s.execute(connection);
            } catch (Exception e) {
                Utils.printSQLException(e);
                throw new RuntimeException(e);
            }
        }
    }
}
