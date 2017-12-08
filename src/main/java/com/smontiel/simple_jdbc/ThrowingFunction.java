package com.smontiel.simple_jdbc;

/**
 * Implementations of this interface convert T objects into R objects.
 *
 * @param <T> the target type the result will be converted from.
 * @param <R> the target type the input will be converted to.
 *
 * Created by Salvador Montiel on 01/december/2017.
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {

    /**
     * Turn the <code>T</code> object into an <code>R</code> object.
     *
     * @param t The <code>T</code> to convert. It has not been touched
     * before being passed to this method.
     *
     * @return An <code>R</code> object initialized with <code>T</code> data. It is
     * legal for implementations to return <code>null</code> if the
     * <code>T</code> is <code>null</code> too.
     *
     * @throws Exception if a error occurs
     */
    R apply(T t) throws Exception;
}
