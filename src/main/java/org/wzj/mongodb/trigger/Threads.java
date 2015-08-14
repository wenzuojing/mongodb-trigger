package org.wzj.mongodb.trigger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wens on 15/8/13.
 */
public class Threads {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactory() {
            AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("%s-%d", name, counter.incrementAndGet()));
            }
        };
    }
}
