package com.github.nicklaus4.buffer.consumer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;

import com.github.nicklaus4.buffer.Buffer;

/**
 * consumer thread
 *
 * @author weishibai
 * @date 2019/03/28 7:47 PM
 */
public class ConsumerThread<T> extends Thread {

    private volatile boolean running;

    private List<ConsumeBufferPuller> works;

    private long consumeInterval;

    private final BufferConsumer<T> consumer;

    ConsumerThread(String name, BufferConsumer<T> consumer, long interval) {
        super(name);
        this.consumeInterval = interval;
        this.consumer = consumer;
        this.works = new LinkedList<>();
    }

    /**
     * add work
     */
    public void addWork(Buffer<T> buffer, int start, int end) {
        works.add(new ConsumeBufferPuller(buffer, start, end));
    }

    public void addWork(Buffer<T> buffer) {
        addWork(buffer, 0, buffer.bufferSize());
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            consume0();

            try {
                TimeUnit.SECONDS.sleep(consumeInterval);
            } catch (InterruptedException e) {
            }
        }
        consume0();
        consumer.onTerminate();
    }

    private List<T> collect() {
        List<T> msg = new LinkedList<>();
        works.forEach(puller -> msg.addAll(puller.pull()));
        return msg;
    }

    private void consume0() {
        List<T> msg = collect();
        if (CollectionUtils.isNotEmpty(msg)) {
            try {
                consumer.consume(msg);
            } catch (Exception e) {
                consumer.onError(msg, e);
            }
        }
    }

    void shutdown() {
        running = false;
    }

    class ConsumeBufferPuller {
        private Buffer<T> buffer;
        private int start;
        private int end;

        ConsumeBufferPuller(Buffer<T> buffer, int start, int end) {
            this.buffer = buffer;
            this.start = start;
            this.end = end;
        }

        LinkedList<T> pull() {
            return buffer.obtain(start, end);
        }
    }

}
