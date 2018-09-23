package com.test.pool;

import jdk.nashorn.internal.codegen.CompilerConstants;

import java.util.concurrent.*;

public final class BoundedBlockingPool<T> extends AbstractPool<T> implements BlockingPool<T> {

    private int size;

    private BlockingQueue<T> objects;

    private Validator<T> validator;

    private ObjectFactory<T> objectFactory;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private volatile boolean shutDownCalled;

    public BoundedBlockingPool(int size,
                               Validator<T> validator,
                               ObjectFactory<T> objectFactory) {
        super();
        this.size = size;
        this.validator = validator;
        this.objectFactory = objectFactory;
        objects = new LinkedBlockingQueue<T>(size);
        initializeObjects();
        shutDownCalled = false;
    }

    public T get(long timeOut, TimeUnit unit) {
        if (!shutDownCalled) {
            T t = null;

            try {
                t = objects.poll(timeOut, unit);
                return t;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return t;
        }

        throw new IllegalStateException("Object pool is already shutdown");

    }

    public T get() {
        if (!shutDownCalled) {
            T t = null;

            try {
                t = objects.take();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return t;
        }

        throw new IllegalStateException("Object pool is already shutdown");
    }

    public void shutDown() {
        shutDownCalled = true;
        executor.shutdownNow();
        clearResources();
    }

    private void clearResources() {
        for (T t : objects) {
            validator.inValidate(t);
        }
    }

    @Override
    protected void returnToPool(T t) {
        if (validator.isValid(t)) {
            executor.submit(new ObjectReturner(objects, t));
        }
    }

    @Override
    protected void handleInvalidReturn(T t) {

    }

    @Override
    protected  boolean isValid(T t) {
        return validator.isValid(t);
    }

    private void initializeObjects() {
        for (int i = 0; i < size; i++) {
            objects.add(objectFactory.createNew());
        }
    }


    private class ObjectReturner<E> implements Callable<Void> {

        private BlockingQueue<E> objects;

        private E e;

        public ObjectReturner(BlockingQueue<E> objects, E e) {
            this.objects = objects;
            this.e = e;
        }

        public Void call() {
            while (true) {
                try {
                    objects.put(e);
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return null;
        }
    }
}
