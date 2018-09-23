package com.test;

import com.test.pool.Pool;
import com.test.pool.Pool.Validator;

import java.sql.Connection;
import java.sql.SQLException;

public final class JDBCConnectionValidator implements Validator<Connection> {

    public boolean isValid(Connection connection) {
        if (connection == null) {
            return false;
        }

        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void inValidate(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            // do something
        }
    }
}
