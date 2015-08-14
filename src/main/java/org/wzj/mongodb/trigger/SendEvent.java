package org.wzj.mongodb.trigger;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wens on 15/8/13.
 */
public class SendEvent extends Thread {

    private static final Logger logger = Logger.getLogger(SendEvent.class);

    private BlockingQueue<Stream> streamQueue;

    private Map<String, Handler> handlers;

    private ExecutorService executorService;

    public SendEvent(BlockingQueue<Stream> streamQueue, Config config) {
        super("send-trigger-thread");
        this.streamQueue = streamQueue;
        handlers = new ConcurrentHashMap<String, Handler>();
        executorService = Executors.newFixedThreadPool(5, Threads.newThreadFactory("send-event-thread"));
    }

    @Override
    public void run() {

        while (true) {
            try {
                final Stream stream = streamQueue.take();
                final String handlerKey = stream.getHandler();
                Handler handler = handlers.get(handlerKey);
                if (handler == null) {
                    try {
                        handler = ClassUtils.newInstance(handlerKey);
                        handlers.put(handlerKey, handler);
                    } catch (ClassNotFoundException e) {
                        logger.error("Not found handler : " + handlerKey);
                    } catch (IllegalAccessException e) {
                        logger.error("Instance handler[" + handlerKey + "] fail", e);
                    } catch (InstantiationException e) {
                        logger.error("Instance handler[" + handlerKey + "] fail", e);
                    }
                }

                if (handler != null) {
                    final Handler h = handler;
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                h.process(stream);
                            } catch (Exception e) {
                                logger.error("Execute handler fail , [handler=" + handlerKey + " , stream=" + stream + "]");
                            }

                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
    }

}
