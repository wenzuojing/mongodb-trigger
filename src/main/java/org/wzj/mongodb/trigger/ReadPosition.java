package org.wzj.mongodb.trigger;

import org.apache.log4j.Logger;
import org.bson.types.BSONTimestamp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wens on 15/8/13.
 */
public class ReadPosition {

    private static final Logger logger = Logger.getLogger(ReadPosition.class);

    public static ReadPosition INSTANCE = new ReadPosition();

    private FileChannel channel;
    private MappedByteBuffer mappedByteBuffer;


    private ReadPosition() {
        File file = new File(FileUtils.getHomeDir() + File.separator + ".mongodb");
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = file.getPath() + File.separator + "oplogs-timestamp";

        try {
            channel = new RandomAccessFile(path, "rw").getChannel();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Open file error [path= " + path + "]", e);
        }
        try {
            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8);
        } catch (IOException e) {
            throw new RuntimeException("Map file error ", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    channel.force(true);
                } catch (IOException e) {
                    logger.error("Fail to invoke fileChannel.force() method ", e);
                }

                try {
                    channel.close();
                } catch (IOException e) {
                    logger.error("Fail to invoke fileChannel.close() method ", e);
                }
            }
        });

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                flushLastTimestamp();
            }
        }, 10, 1, TimeUnit.SECONDS);
    }

    public void writeLastTimestamp(BSONTimestamp lastTimestamp) {
        synchronized (mappedByteBuffer) {
            mappedByteBuffer.putInt(lastTimestamp.getTime());
            mappedByteBuffer.putInt(lastTimestamp.getInc());
            mappedByteBuffer.flip();
        }

    }

    public BSONTimestamp readStartTimestamp() {
        int time = 0;
        int inc = 0;
        synchronized (mappedByteBuffer) {
            time = mappedByteBuffer.getInt();
            inc = mappedByteBuffer.getInt();
            mappedByteBuffer.flip();
        }
        return new BSONTimestamp(time, inc);
    }

    private void flushLastTimestamp() {
        try {
            channel.force(false);
        } catch (IOException e) {
            logger.error("Fail to invoke fileChannel.force() method ", e);
        }
    }


}
