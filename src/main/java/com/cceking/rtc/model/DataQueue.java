package com.cceking.rtc.model;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DataQueue {
    private final Map<Integer, ByteBuffer> buffer;
    //    private final FileChannel fc;
    private AtomicInteger index;

    public DataQueue() {
        index = new AtomicInteger(0);
        buffer = new HashMap<>();
        String file = UUID.randomUUID() + "'.webm'";
//        FileInputStream fis = new FileInputStream(file);
//        fc = new FileChannel(fis);
    }

    public void append(ByteBuffer src) {
        buffer.put(index.getAndIncrement(), src);
    }

    public Map<Integer, ByteBuffer> get() {
        return buffer;
    }

    public int getLength() {
        return index.get();
    }

    public void clear() {
        index.set(0);
        buffer.clear();
    }

}
