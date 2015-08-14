package org.wzj.mongodb.trigger;

import com.mongodb.DBObject;
import org.bson.types.BSONTimestamp;

/**
 * Created by wens on 15/8/13.
 */
public class Stream {

    private final DBObject data;
    private final Operation operation;
    private final BSONTimestamp oplogTimestamp;
    private final String namespace;
    private final String handler;


    public Stream(DBObject data, Operation operation, BSONTimestamp oplogTimestamp, String namespace, String handler) {
        this.data = data;
        this.operation = operation;
        this.oplogTimestamp = oplogTimestamp;
        this.namespace = namespace;
        this.handler = handler;
    }

    public DBObject getData() {
        return data;
    }

    public Operation getOperation() {
        return operation;
    }

    public BSONTimestamp getOplogTimestamp() {
        return oplogTimestamp;
    }

    public String getNamespace() {
        return namespace;
    }


    public String getHandler() {
        return handler;
    }

    @Override
    public String toString() {
        return "Stream{" +
                "data=" + data +
                ", operation=" + operation +
                ", oplogTimestamp=" + oplogTimestamp +
                ", namespace='" + namespace + '\'' +
                ", handler='" + handler + '\'' +
                '}';
    }
}
