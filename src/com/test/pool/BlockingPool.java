package com.test.pool;

import java.util.concurrent.TimeUnit;

/**
 *
 * Makes the requesting thread wait if no object is available
 * @param <T> the type of objects to pool
 */

public interface BlockingPool<T> extends Pool<T> {

    T get();

    T get(long time, TimeUnit unit) throws InterruptedException;
}
