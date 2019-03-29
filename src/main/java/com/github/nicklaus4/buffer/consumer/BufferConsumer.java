package com.github.nicklaus4.buffer.consumer;

import java.util.List;

/**
 * consumer
 *
 * @author weishibai
 * @date 2019/03/28 8:27 PM
 */
public interface BufferConsumer<T> {

    void onStart();

    void consume(List<T> data);

    void onError(List<T> data, Throwable e);

    void onTerminate();

}
