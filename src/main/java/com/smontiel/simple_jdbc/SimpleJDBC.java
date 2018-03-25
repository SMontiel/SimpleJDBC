package com.smontiel.simple_jdbc;

import com.smontiel.simple_jdbc.internal.Strategy;
import com.smontiel.simple_jdbc.internal.strategies.StrategyFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Salvador Montiel on 28/november/2017.
 */
public final class SimpleJDBC implements Executor {
    public static SimpleJDBC from(DataSource dataSource) {
        return new SimpleJDBC(dataSource);
    }

    private DataSource dataSource;

    private SimpleJDBC(DataSource dataSource) {
        this.dataSource = Utils.checkNotNull(dataSource);
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

    public <T> T transaction(ThrowingFunction<Executor, T> handler) {
        return execute(StrategyFactory.transaction(handler));
    }

    public <T> T tx(ThrowingFunction<Executor, T> handler) {
        return transaction(handler);
    }

    private <S> S execute(Strategy<S> s) {
        try {
            return s.execute(dataSource.getConnection());
        } catch (SQLException e) {
            Utils.printSQLException(e);
            throw new RuntimeException(e);
        }
    }
}
