package org.wzj.mongodb.trigger;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wens on 15/8/13.
 */
public class TestConfig {

    @Test
    public void test() {
        Config config = Config.parser("/config.json");
        Assert.assertNotNull(config);
    }
}
