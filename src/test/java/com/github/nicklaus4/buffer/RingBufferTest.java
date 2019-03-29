package com.github.nicklaus4.buffer;

import org.junit.Test;

/**
 * ring buffer test
 *
 * @author weishibai
 * @date 2019/03/29 2:33 PM
 */
public class RingBufferTest {

    @Test
    public void test1() {
        RingBuffer<Integer> buffer = new RingBuffer<>(5);
        buffer.insert(1);
        buffer.insert(2);
        buffer.insert(3);

        System.out.println(buffer.get());
        System.out.println(buffer.get());
        assert buffer.peek() == 3;

        buffer.insert(4);
        System.out.println(buffer.getAll());  //head < tail case
    }

    @Test
    public void test2() {
        RingBuffer<Integer> buffer = new RingBuffer<>(5);
        buffer.insert(1);
        buffer.insert(2);
        buffer.insert(3);

        System.out.println(buffer.get());
        System.out.println(buffer.get());

        buffer.insert(4);
        buffer.insert(5);
        buffer.insert(6);
        buffer.insert(7);
        buffer.insert(8);

        System.out.println(buffer.getAll());
    }
}
