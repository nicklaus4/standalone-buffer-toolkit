package com.github.nicklaus4.buffer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * recyclable atomic integer
 *
 * @author weishibai
 * @date 2019/03/28 6:34 PM
 */
public class RecycleAtomicInteger extends Number {

    private AtomicInteger value;
    private int startValue;
    private int endValue;

    public RecycleAtomicInteger(int startValue, int maxValue) {
        this.value = new AtomicInteger(startValue);
        this.startValue = startValue;
        this.endValue = maxValue - 1;
    }

    public final int getAndIncrement() {
        int current;
        int next;
        do {
            current = this.value.get();
            next = current >= this.endValue ? this.startValue : current + 1;
        }
        while (!this.value.compareAndSet(current, next));

        return current;
    }

    public final int get() {
        return this.value.get();
    }

    public int intValue() {
        return this.value.intValue();
    }

    public long longValue() {
        return this.value.longValue();
    }

    public float floatValue() {
        return this.value.floatValue();
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

}
