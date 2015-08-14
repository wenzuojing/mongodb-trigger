package org.wzj.mongodb.trigger;

import org.bson.types.BSONTimestamp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Random;

/**
 * Created by wens on 15/8/13.
 */
public class TestReadPosition {

    private ReadPosition readPosition;

    @Before
    public void before() {

        File file = new File(FileUtils.getHomeDir() + File.separator + ".mongodb" + File.separator + "oplogs-timestamp");
        if (file.exists()) {
            file.delete();
        }
        readPosition = ReadPosition.INSTANCE;
    }

    @Test
    public void test_0() {
        BSONTimestamp bsonTimestamp = readPosition.readStartTimestamp();
        Assert.assertEquals(new BSONTimestamp(0, 0), bsonTimestamp);
        Random r = new Random(System.currentTimeMillis());
        int time = 0;
        int inc = 0;
        for (int i = 0; i < 1000; i++) {
            time = r.nextInt(1000);
            inc = r.nextInt(1000);
            readPosition.writeLastTimestamp(new BSONTimestamp(time, inc));
        }
        Assert.assertEquals(new BSONTimestamp(time, inc), readPosition.readStartTimestamp());
    }
}
