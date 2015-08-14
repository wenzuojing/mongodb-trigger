package org.wzj.mongodb.trigger;

import com.mongodb.*;
import org.apache.log4j.Logger;
import org.bson.types.BSONTimestamp;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by wens on 15/8/13.
 */
public class ReadOplog extends Thread {

    private final static Logger logger = Logger.getLogger(ReadOplog.class);
    private final static boolean debug = logger.isDebugEnabled();

    private BlockingQueue<Stream> streamQueue;
    private Map<String, Config.Event> eventMap;

    private MongoClient mongo;
    private DB oplogDb;
    private DBCollection oplogCollection;


    public ReadOplog(BlockingQueue<Stream> streamQueue, Config config) {
        super("read-oplog-thread");
        this.streamQueue = streamQueue;
        mongo = new MongoClient(config.getMongoServers(), config.getMongoOptions());
        oplogDb = mongo.getDB("local");
        oplogCollection = oplogDb.getCollection("oplog.rs");

        eventMap = new HashMap<String, Config.Event>();
        for (Config.Event e : config.getEvents()) {
            eventMap.put(String.format("%s.%s", e.getDb(), e.getCollection()), e);
        }

    }


    @Override
    public void run() {

        while (true) {

            try {

                BSONTimestamp startTimestamp = ReadPosition.INSTANCE.readStartTimestamp();

                if (debug) {
                    logger.debug("Start read oplog from timestamp [" + startTimestamp + "]");
                }

                BasicDBObject filter = new BasicDBObject("ts", new BasicDBObject(QueryOperators.GT, startTimestamp));
                int options = Bytes.QUERYOPTION_TAILABLE | Bytes.QUERYOPTION_AWAITDATA | Bytes.QUERYOPTION_NOTIMEOUT;
                DBCursor cursor = oplogCollection.find(filter).setOptions(options);

                while (cursor.hasNext()) {
                    DBObject dbObject = cursor.next();

                    if (debug) {
                        logger.debug("Read oplog [" + dbObject + "]");
                    }

                    String op = dbObject.get("op").toString();

                    Operation operation = Operation.fromString(op);

                    String namespace = dbObject.get("ns").toString();

                    ReadPosition.INSTANCE.writeLastTimestamp((BSONTimestamp) dbObject.get("ts"));

                    Config.Event matchEvent = eventMap.get(namespace);

                    if (matchEvent != null && (Operation.INSERT == operation || Operation.UPDATE == operation || Operation.DELETE == operation)) {

                        if (Operation.UPDATE == operation) {
                            DBObject update = (DBObject) dbObject.get("o");

                            if (!containField(update, eventMap.get(namespace).getFields())) {
                                continue;
                            }
                        }

                        if (debug) {
                            logger.debug(" Match trigger [  namespace = " + namespace + " , fields =  " + Arrays.toString(matchEvent.getFields().toArray()) + "]");
                        }
                        addToStreamQueue(operation, namespace, matchEvent.getHandler(), dbObject);
                    }
                }

            } catch (Exception e) {
                logger.error(e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    //
                }

            }
        }
    }

    private boolean containField(DBObject update, List<String> fields) {

        if (fields == null || fields.size() == 0) {
            return true;
        }

        for (String field : fields) {
            if (contain(update, field)) {
                return true;
            }
        }

        return false;
    }

    private boolean contain(DBObject update, String field) {
        Set<String> keySet = update.keySet();
        if (keySet.contains(field)) {
            return true;
        }
        for (String tmp : keySet) {
            if (tmp.startsWith("$")) {
                return contain((DBObject) update.get(tmp), field);
            }
        }
        return false;
    }

    private void addToStreamQueue(Operation operation, String namespace, String handler, DBObject dbObject) {

        BSONTimestamp timestamp = (BSONTimestamp) dbObject.get("ts");

        DBObject data = null;

        if (Operation.UPDATE == operation) {
            int indexOf = namespace.lastIndexOf(".");
            String db = namespace.substring(0, indexOf);
            String collection = namespace.substring(indexOf + 1);
            DBCollection srcCollection = mongo.getDB(db).getCollection(collection);
            data = srcCollection.findOne((DBObject) dbObject.get("o2"));
        } else {
            data = (DBObject) dbObject.get("o");
        }

        try {

            streamQueue.put(new Stream(data, operation, timestamp, namespace, handler));
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

    }

}
