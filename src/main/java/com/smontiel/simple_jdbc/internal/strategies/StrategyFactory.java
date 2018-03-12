package com.smontiel.simple_jdbc.internal.strategies;

import com.smontiel.simple_jdbc.Executor;
import com.smontiel.simple_jdbc.ThrowingFunction;
import com.smontiel.simple_jdbc.internal.Strategy;

import java.sql.ResultSet;
import java.util.List;

import static com.smontiel.simple_jdbc.Utils.checkNotNull;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
public final class StrategyFactory {

    public static <T> Strategy<List<T>> many(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
        ManyStrategy<T> s = ManyStrategy.getInstance();
        s.setSqlQuery(checkNotNull(sqlQuery));
        s.setHandler(checkNotNull(handler));
        return s;
    }

    public static <T> Strategy<List<T>> manyOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
        ManyOrNoneStrategy<T> s = ManyOrNoneStrategy.getInstance();
        s.setSqlQuery(checkNotNull(sqlQuery));
        s.setHandler(checkNotNull(handler));
        return s;
    }

    public static Strategy<Integer> none(String sqlQuery) {
        NoneStrategy s = NoneStrategy.getInstance();
        s.setSqlQuery(checkNotNull(sqlQuery));
        return s;
    }

    public static <T> Strategy<T> one(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
        OneStrategy<T> s = OneStrategy.getInstance();
        s.setSqlQuery(checkNotNull(sqlQuery));
        s.setHandler(checkNotNull(handler));
        return s;
    }

    public static <T> Strategy<T> oneOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler) {
        OneOrNoneStrategy<T> s = OneOrNoneStrategy.getInstance();
        s.setSqlQuery(checkNotNull(sqlQuery));
        s.setHandler(checkNotNull(handler));
        return s;
    }

    public static <T> Strategy<T> transaction(ThrowingFunction<Executor, T> handler) {
        TransactionStrategy<T> s = TransactionStrategy.getInstance();
        s.setHandler(checkNotNull(handler));
        return s;
    }
}
