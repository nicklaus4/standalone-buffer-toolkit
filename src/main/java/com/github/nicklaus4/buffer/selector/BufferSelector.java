package com.github.nicklaus4.buffer.selector;

import com.github.nicklaus4.buffer.BufferStrategy;

/**
 * buffer selector
 *
 * @author weishibai
 * @date 2019/03/28 7:25 PM
 */
public interface BufferSelector<T> {

    int select(int total, T data);

    /**
     * @return an integer represents how many times should retry when {@link BufferStrategy#IF_POSSIBLE}.
     * less or equal 1, means not support retry.
     */
    int retryCount();

}
