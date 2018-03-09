package com.smontiel.simple_jdbc.internal;

import com.smontiel.simple_jdbc.ThrowingFunction;

/**
 * Created by Salvador Montiel on 08/mar/2018.
 */
public interface Handlerable<S, T> {

    void setHandler(ThrowingFunction<S, T> handler);
}
