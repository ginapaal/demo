package com.test.pool;

public interface ObjectFactory<T> {

    public abstract T createNew();
}
