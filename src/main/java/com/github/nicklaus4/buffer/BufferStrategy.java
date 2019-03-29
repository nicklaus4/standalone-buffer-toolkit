package com.github.nicklaus4.buffer;

/**
 * buffer full strategy
 *
 * @author weishibai
 * @date 2019/03/28 5:35 PM
 */
public enum BufferStrategy {

    BLOCK,    //wait for buffer is available, this may block business thread be careful

    OVERRIDE, //override buffer

    IF_POSSIBLE, //add if possible or discard, warning if consume ability is too weak may cause data loss
}
