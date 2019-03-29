package com.github.nicklaus4.buffer;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.nicklaus4.buffer.consumer.BufferConsumeExecutor;
import com.github.nicklaus4.buffer.consumer.BufferConsumer;
import com.github.nicklaus4.buffer.selector.BufferSelector;
import com.google.common.hash.Hashing;

/**
 * buffer executor test
 *
 * @author weishibai
 * @date 2019/03/29 1:50 PM
 */
public class BufferExecutorTest {

//    @Test
    public void testExecutor() {
        final BufferPool<String> bufferPool = new BufferPool<>(2, 10, new BufferSelector<String>() {
            @Override
            public int select(int total, String data) {
                return ((Hashing.murmur3_128()
                        .hashString(data, Charset.defaultCharset()).asInt() % total) + total) % total;
            }

            @Override
            public int retryCount() {
                return 10;
            }
        }, BufferStrategy.IF_POSSIBLE);

        final BufferConsumeExecutor<String> executor = BufferConsumeExecutors.newConsumeExecutor(1, new BufferConsumer<String>() {
            @Override
            public void onStart() {
                System.out.println("start executor");
            }

            @Override
            public void consume(List<String> data) {
                System.out.println("consume " + data);
            }

            @Override
            public void onError(List<String> data, Throwable e) {
                System.out.println("consume error " + data);
                System.err.println(e);
            }

            @Override
            public void onTerminate() {
                System.out.println("consume executor complete");
            }
        }, bufferPool);

        executor.start();

        for (int i = 0; i < 200; i++) {
            System.out.println(i + " insert " + bufferPool.insert("data " + i));
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
            }
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
        }

        if (executor.running()) {
            executor.closeQuietly();
        }
    }

}
