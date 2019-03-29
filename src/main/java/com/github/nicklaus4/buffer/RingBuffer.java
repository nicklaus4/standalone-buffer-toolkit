package com.github.nicklaus4.buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ring buffer impl
 * thread not safe so far
 *
 * @author weishibai
 * @date 2019/03/29 2:14 PM
 */
public class RingBuffer<T> {

    private static final int DEFAULT_SIZE = 1024;

    private final Object[] elements;

    private int head;

    private int tail;

    private int bufferSize;

    public RingBuffer() {
        this(DEFAULT_SIZE + 1);
    }

    public RingBuffer(int bufferSize) {
        this.bufferSize = bufferSize + 1;
        elements = new Object[this.bufferSize];
    }

    public boolean empty() {
        return head == tail;
    }

    public boolean full() {
        return (tail + 1) % bufferSize == head;
    }

    public void clear() {
        Arrays.fill(elements, null);
        this.head = 0;
        this.tail = 0;
    }

    public boolean insert(T data) {
        if (full())
            return false;

        elements[tail] = data;
        tail = (tail + 1) % bufferSize;
        return true;
    }

    public T get() {
        if (empty())
            return null;

        @SuppressWarnings("unchecked")
        T obj = (T) elements[head];
        head = (head + 1) % bufferSize;
        return obj;
    }

    public T peek() {
        if (empty())
            return null;

        //noinspection unchecked
        return (T) elements[head];
    }

    public int count() {
        if (empty())
            return 0;
        return head < tail ? tail - head : tail + bufferSize - head;
    }

    public int bufferSize() {
        return bufferSize - 1;
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        int total = count();
        if (0 == total) {
            return null;
        }
        List<T> result = new ArrayList<>(total);

        if (head < tail) {
            for (int i = head; i < tail; i++) {
                result.add((T) elements[i]);
            }
        } else {
            for (int i = head; i < bufferSize; i++) {
                result.add((T) elements[i]);
            }

            for (int i = 0; i < tail; i++) {
                result.add((T) elements[i]);
            }
        }
        /* update head */
        this.head = tail;
        return result;
    }
}
