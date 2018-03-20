package com.smontiel.simple_jdbc.internal;

import java.sql.Connection;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
public interface Strategy<S> {

    S execute(Connection connection);
}
