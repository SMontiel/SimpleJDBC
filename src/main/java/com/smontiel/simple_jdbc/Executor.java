package com.smontiel.simple_jdbc;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
public interface Executor {

    <T> List<T> any(String sqlQuery, ThrowingFunction<ResultSet, T> handler);

    <T> List<T> many(String sqlQuery, ThrowingFunction<ResultSet, T> handler);

    <T> List<T> manyOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler);

    Integer none(String sqlQuery);

    <T> T one(String sqlQuery, ThrowingFunction<ResultSet, T> handler);

    <T> T oneOrNone(String sqlQuery, ThrowingFunction<ResultSet, T> handler);
}
