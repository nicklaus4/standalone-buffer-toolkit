package com.github.nicklaus4.buffer;

import com.github.nicklaus4.buffer.consumer.BufferConsumeExecutor;
import com.github.nicklaus4.buffer.consumer.BufferConsumer;

/**
 * buffer consume executors
 * factory class
 * @author weishibai
 * @date 2019/03/29 2:09 PM
 */
public class BufferConsumeExecutors {

    private static final int DEFAULT_INTERVAL = 1;

    /**
     * new buffer consume executor
     * @param consumerCount consume thread count
     * @param function consume function
     */
    public static <T> BufferConsumeExecutor<T> newConsumeExecutor(int consumerCount, BufferConsumer<T> function
            , BufferPool<T> bufferPool) {
        return newConsumeExecutor(consumerCount, DEFAULT_INTERVAL, function, bufferPool);
    }

    /**
     * new buffer consume executor
     * @param consumerCount consume thread count
     * @param consumeInterval second level consume interval
     * @param function consume function
     */
    public static <T> BufferConsumeExecutor<T> newConsumeExecutor(int consumerCount, int consumeInterval, BufferConsumer<T> function
            , BufferPool<T> bufferPool) {
        return new BufferConsumeExecutor<>(consumerCount, function, consumeInterval, bufferPool);
    }

}
