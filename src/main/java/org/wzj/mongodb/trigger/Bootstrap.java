package org.wzj.mongodb.trigger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Created by wens on 15/8/13.
 */
public class Bootstrap {

    private static final Logger logger = Logger.getLogger(Bootstrap.class);

    public static void main(String[] args) {

        InputStream log4jConfig = Bootstrap.class.getResourceAsStream("/log4j.properties");
        PropertyConfigurator.configure(log4jConfig);

        logger.info("Start server .....");

        Config config = Config.parser("/config.json");

        BlockingQueue<Stream> streamQueue = new ArrayBlockingQueue<Stream>(100000);


        logger.info("Start ReadOplog .....");
        ReadOplog readOplog = new ReadOplog(streamQueue, config);
        readOplog.start();

        logger.info("Start SendEvent .....");
        SendEvent sendEvent = new SendEvent(streamQueue, config);
        sendEvent.start();

        logger.info("Start OK .....");

    }


}
