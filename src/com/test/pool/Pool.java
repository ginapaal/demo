package com.test.pool;

public interface Pool<T> {

    T get();

    void release(T t);

    void shutDown();

    public static interface Validator<T> {

        public boolean isValid(T t);

        public void inValidate(T t);
    }
}
