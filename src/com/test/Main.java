package com.test;

import com.test.pool.BoundedBlockingPool;
import com.test.pool.Pool;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        Pool<Connection> pool = new BoundedBlockingPool<Connection>(10,
                new JDBCConnectionValidator(),
                new JDBCConnectionFactory("", "", "", ""));

        //insert magic here
    }
}
