package com.github.nicklaus4.buffer.consumer;

import java.util.Arrays;
import java.util.List;

import com.github.nicklaus4.buffer.Buffer;
import com.github.nicklaus4.buffer.BufferPool;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * consumer pool
 *
 * @author weishibai
 * @date 2019/03/28 7:47 PM
 */
public class BufferConsumeExecutor<T> {

    private ConsumerThread<T>[] consumerPool;

    private BufferPool<T> bufferPool;

    private volatile boolean running;

    /**
     * @param consumerCount consume thread count
     * @param consumerFunction real consume function
     * @param consumeIntervalSec consume thread wait interval to execute next consume
     * @param bufferPool buffer pool to store works
     */
    public BufferConsumeExecutor(int consumerCount, BufferConsumer<T> consumerFunction
            , long consumeIntervalSec, BufferPool<T> bufferPool) {
        this.bufferPool = bufferPool;
        //noinspection unchecked
        consumerPool = new ConsumerThread[consumerCount];
        for (int i = 0; i < consumerCount; i++) {
            consumerPool[i] = new ConsumerThread<>("consumer-thread-" + i, consumerFunction, consumeIntervalSec);
        }
    }

    /**
     * executor running state
     */
    public boolean running() {
        return running;
    }

    /**
     * fire to consume
     */
    public void start() {
        if (running) {
            return;
        }

        synchronized (this) {
            /* allocate buffers to threads */
            allocateBuffer2Consumer();

            /* start consume */
            Arrays.stream(consumerPool).forEach(Thread::start);
            running = true;
        }
    }

    private void allocateBuffer2Consumer() {
        final int bufferCount = bufferPool.bufferCount();
        final int consumerCount = consumerPool.length;

        if (consumerCount > bufferCount) {
            /* split buffer to consumers */
            ListMultimap<Integer, Integer> mapping = ArrayListMultimap.create();
            for (int consumerIndex = 0; consumerIndex < consumerCount; consumerIndex++) {
                mapping.put(consumerIndex % bufferCount, consumerIndex);
            }

            for (int bufferIndex = 0; bufferIndex < bufferCount; bufferIndex++) {
                final List<Integer> consumers = mapping.get(bufferIndex);
                final Buffer<T> buffer = bufferPool.buffer(bufferIndex);
                int cs = consumers.size();
                int step = buffer.bufferSize() / cs;
                for (int i = 0; i < cs; i++) {
                    final Integer consumerIndex = consumers.get(i);
                    consumerPool[consumerIndex].addWork(buffer, i * step
                            , i == cs - 1 ? buffer.bufferSize() : (i + 1) * step);
                }
            }
        } else {
            for (int bufferIndex = 0; bufferIndex < bufferCount; bufferIndex++) {
                consumerPool[bufferIndex % consumerCount].addWork(bufferPool.buffer(bufferIndex));
            }
        }
    }

    public void closeQuietly() {
        synchronized (this) {
            try {
                Arrays.stream(consumerPool).forEach(ConsumerThread::shutdown);
            } catch (Exception e) {}
        }
    }
}
