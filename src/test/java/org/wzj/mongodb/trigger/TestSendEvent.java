package org.wzj.mongodb.trigger;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by wens on 15/8/13.
 */
public class TestSendEvent {

    private BlockingQueue<Stream> streamQueue;

    private SendEvent sendEvent;

    @Before
    public void before() {
        streamQueue = new ArrayBlockingQueue<Stream>(100000);
        sendEvent = new SendEvent(streamQueue, null);
    }

    @Test
    public void test_0() throws InterruptedException {
        sendEvent.start();
        try {
            streamQueue.put(new Stream(null, Operation.DELETE, null, "test_db.user", "org.wzj.mongodb.trigger.TestHandler"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(30);
    }

}
