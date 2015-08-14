package org.wzj.mongodb.trigger;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by wens on 15/8/14.
 */
public class TestBootstrap {


    @Test
    public void test_0() throws InterruptedException {

        Bootstrap.main(null);
        TimeUnit.MINUTES.sleep(10);

    }

}
