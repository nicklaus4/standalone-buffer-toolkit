package com.github.nicklaus4.buffer;

import static com.github.nicklaus4.buffer.BufferStrategy.IF_POSSIBLE;

import java.util.Arrays;

import com.github.nicklaus4.buffer.listener.BufferBlockingListener;
import com.github.nicklaus4.buffer.selector.BufferSelector;
import com.google.common.base.Preconditions;

/**
 * buffer pool
 *
 * @author weishibai
 * @date 2019/03/28 6:47 PM
 */
public class BufferPool<T> {

    private final Buffer<T>[] buffers;

    private final BufferSelector<T> bufferSelector;

    private volatile BufferStrategy strategy;

    private int bufferCount;

    private int bufferSize;

    public static <T> BufferPool<T> of(int bufferCount, int bufferSize, BufferSelector<T> bufferSelector) {
        return new BufferPool<>(bufferCount, bufferSize, bufferSelector);
    }

    public static <T> BufferPool<T> of(int bufferCount, int bufferSize, BufferSelector<T> bufferSelector
            , BufferStrategy strategy) {
        return new BufferPool<>(bufferCount, bufferSize, bufferSelector, strategy);
    }

    public BufferPool(int bufferCount, int bufferSize, BufferSelector<T> bufferSelector) {
        this(bufferCount, bufferSize, bufferSelector, IF_POSSIBLE);
    }

    public BufferPool(int bufferCount, int bufferSize, BufferSelector<T> bufferSelector, BufferStrategy strategy) {
        this.bufferCount = bufferCount;
        this.buffers = new Buffer[bufferCount];
        this.strategy = strategy;
        this.bufferSize = bufferSize;
        this.bufferSelector = Preconditions.checkNotNull(bufferSelector);

        for (int i = 0; i < bufferCount; i++) {
            buffers[i] = new Buffer<>(bufferSize, strategy);
        }
    }

    public int bufferCount() {
        return bufferCount;
    }

    public int totalBufferSlot() {
        return bufferCount * bufferSize;
    }

    public BufferStrategy strategy() {
        return strategy;
    }

    public boolean insert(T data) {
        if (null == data) {
            return false;
        }

        final int bufferIndex = bufferSelector.select(bufferCount, data);
        final int retryCount = bufferSelector.retryCount();

        if (strategy == IF_POSSIBLE && retryCount > 0) {
            for (int i = 0; i < retryCount; i++) {
                if (buffers[bufferIndex].insert(data)) {
                    return true;
                }
            }
        } else {
            return buffers[bufferIndex].insert(data);
        }
        return false;
    }

    public Buffer<T> buffer(int index) {
        return buffers[index];
    }

    /**
     * this may cause inconsistency
     * @param strategy buffer strategy
     */
    public void updateStrategy(BufferStrategy strategy) {
        this.strategy = strategy;
        Arrays.stream(buffers).forEach(buffer -> buffer.updateStrategy(strategy));
    }

    /* add listener */
    public void addListener(BufferBlockingListener<T> listener) {
        Arrays.stream(buffers).forEach(buffer -> buffer.addListener(listener));
    }

}
