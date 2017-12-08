package com.smontiel.simple_jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * Created by Salvador Montiel on 28/november/2017.
 */
class Utils {

    static void printSQLException(SQLException ex) {

        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (!ignoreSQLException(
                        ((SQLException) e).
                                getSQLState())) {

                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " +
                            ((SQLException)e).getSQLState());

                    System.err.println("Error Code: " +
                            ((SQLException)e).getErrorCode());

                    System.err.println("Message: " + e.getMessage());

                    Throwable t = ex.getCause();
                    while(t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }

    static boolean ignoreSQLException(String sqlState) {

        if (sqlState == null) {
            System.out.println("The SQL state is not defined!");
            return false;
        }

        // X0Y32: Jar file already exists in schema
        if (sqlState.equalsIgnoreCase("X0Y32"))
            return true;

        // 42Y55: Table already exists in schema
        if (sqlState.equalsIgnoreCase("42Y55"))
            return true;

        return false;
    }

    static void getWarningsFromResultSet(ResultSet rs)
            throws SQLException {
        printWarnings(rs.getWarnings());
    }

    public static void getWarningsFromStatement(Statement stmt)
            throws SQLException {
        printWarnings(stmt.getWarnings());
    }

    public static void printWarnings(SQLWarning warning)
            throws SQLException {

        if (warning != null)
            System.out.println("\n---Warning---\n");

        while (warning != null) {
            System.out.println("Message: " + warning.getMessage());
            System.out.println("SQLState: " + warning.getSQLState());
            System.out.print("Vendor error code: ");
            System.out.println(warning.getErrorCode());
            System.out.println("");
            warning = warning.getNextWarning();
        }
    }

    static void close(AutoCloseable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (Exception e) {
            closeable = null;
        }
    }

    private Utils() {}
}
