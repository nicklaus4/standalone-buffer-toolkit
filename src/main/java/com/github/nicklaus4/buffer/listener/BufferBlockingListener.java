package com.github.nicklaus4.buffer.listener;

/**
 * notify while buffer entry is full and block mode is on
 *
 * @author weishibai
 * @date 2019/03/28 5:54 PM
 */
public interface BufferBlockingListener<T> {

    void notify(T message);
}
