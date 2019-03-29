package com.github.nicklaus4.buffer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.nicklaus4.buffer.listener.BufferBlockingListener;

/**
 * buffer entity
 *
 * @author weishibai
 * @date 2019/03/28 5:46 PM
 */
public class Buffer<T> {

    private int size;

    private volatile Object[] elements;

    private volatile BufferStrategy strategy;

    private List<BufferBlockingListener<T>> listeners;

    private RecycleAtomicInteger counter;

    Buffer(int bufferSize, BufferStrategy strategy) {
        this.size = bufferSize;
        this.strategy = strategy;
        this.elements = new Object[bufferSize];
        this.listeners = new ArrayList<>();
        this.counter = new RecycleAtomicInteger(0, size);
    }

    Buffer(int bufferSize) {
        this(bufferSize, BufferStrategy.IF_POSSIBLE);
    }

    void addListener(BufferBlockingListener<T> listener) {
        listeners.add(listener);
    }

    void updateStrategy(BufferStrategy strategy) {
        this.strategy = strategy;
    }

    public int bufferSize() {
        return size;
    }

    boolean insert(T data) {
        final int index = counter.getAndIncrement();
        if (null != elements[index]) {
            if (strategy == BufferStrategy.BLOCK) {
                boolean notify = false;
                while (null != elements[index]) {
                    if (!notify) {
                        listeners.forEach(listener -> listener.notify(data));
                        notify = true;
                    }

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {}
                }
            } else if (strategy == BufferStrategy.IF_POSSIBLE) {
                return false;
            }
        }
        elements[index] = data;
        return true;
    }

    public LinkedList<T> obtain(int start, int end) {
        LinkedList<T> result = new LinkedList<>();
        for (int i = start; i < end; i++) {
            if (elements[i] != null) {
                //noinspection unchecked
                result.add((T) elements[i]);
                elements[i] = null;
            }
        }
        return result;
    }
}
