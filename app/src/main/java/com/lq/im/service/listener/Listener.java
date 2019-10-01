package com.lq.im.service.listener;

public interface Listener<T> {
    void process(T t);
    boolean shouldProcess(T t);
    void clear();
}
