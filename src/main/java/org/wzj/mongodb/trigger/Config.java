package org.wzj.mongodb.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by wens on 15/8/13.
 */
public class Config {

    private String id;

    private List<ServerAddress> mongoServers;

    private MongoClientOptions mongoOptions;

    private List<Event> events;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ServerAddress> getMongoServers() {
        return mongoServers;
    }

    public void setMongoServers(List<ServerAddress> mongoServers) {
        this.mongoServers = mongoServers;
    }

    public MongoClientOptions getMongoOptions() {
        return mongoOptions;
    }

    public void setMongoOptions(MongoClientOptions mongoOptions) {
        this.mongoOptions = mongoOptions;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public static Config parser(String confName) {

        InputStream inputStream = null;

        try {
            inputStream = Config.class.getResourceAsStream(confName);
        } catch (Exception e) {
            throw new ConfigException("Fail to read config [confName = " + confName + "]", e);
        }

        JsonParser jsonParser = new JsonParser();

        JsonElement json = null;

        try {
            json = jsonParser.parse(new InputStreamReader(inputStream));
        } catch (Exception e) {
            throw new ConfigException("Fail to parser config ", e);
        }

        Config config = new Config();

        JsonObject root = json.getAsJsonObject();

        JsonElement id = root.get("id");
        if (id == null) {
            throw new ConfigException("Must has id");
        }
        config.setId(id.getAsString());

        JsonObject mongodb = root.getAsJsonObject("mongodb");
        if (mongodb == null) {
            throw new ConfigException("Must has mongodb");
        }

        config.setMongoServers(parserMongodb(mongodb));
        config.setMongoOptions(parserOptions(mongodb));
        JsonArray events = root.getAsJsonArray("events");

        if (events != null && events.size() > 0) {
            config.setEvents(parseEvents(events));
        }

        return config;

    }

    private static List<Event> parseEvents(JsonArray events) {


        if (events != null && events.size() != 0) {
            Set<String> set = new HashSet<String>();
            List<Event> list = new ArrayList<Event>(events.size());

            Iterator<JsonElement> iterator = events.iterator();

            while (iterator.hasNext()) {
                JsonObject event = (JsonObject) iterator.next();
                JsonElement db = event.get("db");
                JsonElement collection = event.get("collection");
                JsonElement handler = event.get("handler");
                JsonArray fields = event.getAsJsonArray("fields");

                if (db == null) {
                    throw new ConfigException("Missing db config");
                }
                if (collection == null) {
                    throw new ConfigException("Missing collection config");
                }
                if (handler == null) {
                    throw new ConfigException("Missing handler config");
                }


                List<String> ff = null;
                if (fields != null) {
                    ff = new ArrayList<String>(fields.size());
                    for (JsonElement e : fields) {
                        ff.add(e.getAsString());
                    }
                }
                String key = String.format("%s.%s", db.getAsString(), collection.getAsString());
                if (!set.contains(key)) {
                    set.add(key);
                } else {
                    throw new ConfigException(String.format("Duplicate event config [db:%s,collection:%s]", db, collection));
                }

                list.add(new Event(db.getAsString(), collection.getAsString(), ff, handler.getAsString()));
            }

            return list;

        }
        return Collections.EMPTY_LIST;
    }

    private static MongoClientOptions parserOptions(JsonObject mongodb) {

        Builder builder = new MongoClientOptions.Builder();
        JsonObject options = mongodb.getAsJsonObject("options");

        if (options != null) {
            JsonElement acceptableLatencyDifference = options.get("acceptableLatencyDifference");
            JsonElement alwaysUseMBeans = options.get("alwaysUseMBeans");
            JsonElement autoConnectRetry = options.get("autoConnectRetry");
            JsonElement connectionsPerHost = options.get("connectionsPerHost");
            JsonElement connectTimeout = options.get("connectTimeout");
            JsonElement cursorFinalizerEnabled = options.get("cursorFinalizerEnabled");
            JsonElement description = options.get("description");
            JsonElement heartbeatConnectRetryFrequency = options.get("heartbeatConnectRetryFrequency");
            JsonElement heartbeatConnectTimeout = options.get("heartbeatConnectTimeout");
            JsonElement heartbeatFrequency = options.get("heartbeatFrequency");
            JsonElement heartbeatSocketTimeout = options.get("heartbeatSocketTimeout");
            JsonElement heartbeatThreadCount = options.get("heartbeatThreadCount");
            JsonElement maxAutoConnectRetryTime = options.get("maxAutoConnectRetryTime");
            JsonElement maxConnectionIdleTime = options.get("maxConnectionIdleTime");
            JsonElement maxConnectionLifeTime = options.get("maxConnectionLifeTime");
            JsonElement maxWaitTime = options.get("maxWaitTime");
            JsonElement minConnectionsPerHost = options.get("minConnectionsPerHost");
            JsonElement requiredReplicaSetName = options.get("requiredReplicaSetName");
            JsonElement socketKeepAlive = options.get("socketKeepAlive");
            JsonElement socketTimeout = options.get("socketTimeout");

            if (acceptableLatencyDifference != null) {
                builder.acceptableLatencyDifference(acceptableLatencyDifference.getAsInt());
            }

            if (alwaysUseMBeans != null) {
                builder.alwaysUseMBeans(alwaysUseMBeans.getAsBoolean());
            }

            if (autoConnectRetry != null) {
                builder.autoConnectRetry(autoConnectRetry.getAsBoolean());
            }
            if (connectionsPerHost != null) {
                builder.connectionsPerHost(connectionsPerHost.getAsInt());
            }

            if (connectTimeout != null) {
                builder.connectTimeout(connectTimeout.getAsInt());
            }

            if (cursorFinalizerEnabled != null) {
                builder.cursorFinalizerEnabled(cursorFinalizerEnabled.getAsBoolean());
            }
            if (description != null) {
                builder.description(description.getAsString());
            }
            if (heartbeatConnectRetryFrequency != null) {
                builder.heartbeatConnectRetryFrequency(heartbeatConnectRetryFrequency.getAsInt());
            }
            if (heartbeatConnectTimeout != null) {
                builder.heartbeatConnectTimeout(heartbeatConnectTimeout.getAsInt());
            }

            if (heartbeatFrequency != null) {
                builder.heartbeatFrequency(heartbeatFrequency.getAsInt());
            }

            if (heartbeatSocketTimeout != null) {
                builder.heartbeatSocketTimeout(heartbeatSocketTimeout.getAsInt());
            }

            if (heartbeatThreadCount != null) {
                builder.heartbeatThreadCount(heartbeatThreadCount.getAsInt());
            }
            if (maxAutoConnectRetryTime != null) {
                builder.maxAutoConnectRetryTime(maxAutoConnectRetryTime.getAsLong());
            }

            if (maxConnectionIdleTime != null) {
                builder.maxConnectionIdleTime(maxConnectionIdleTime.getAsInt());
            }

            if (maxConnectionLifeTime != null) {
                builder.maxConnectionLifeTime(maxConnectionLifeTime.getAsInt());
            }

            if (maxWaitTime != null) {
                builder.maxWaitTime(maxWaitTime.getAsInt());
            }

            if (minConnectionsPerHost != null) {
                builder.minConnectionsPerHost(minConnectionsPerHost.getAsInt());
            }

            if (requiredReplicaSetName != null) {
                builder.requiredReplicaSetName(requiredReplicaSetName.getAsString());
            }

            if (socketKeepAlive != null) {
                builder.socketKeepAlive(socketKeepAlive.getAsBoolean());
            }

            if (socketTimeout != null) {
                builder.socketTimeout(socketTimeout.getAsInt());
            }

        }
        return builder.build();
    }

    private static List<ServerAddress> parserMongodb(JsonObject mongodb) {
        List<ServerAddress> seeds = new LinkedList<ServerAddress>();
        JsonArray servers = mongodb.getAsJsonArray("servers");

        if (servers == null || servers.size() == 0) {
            String host = "localhost";
            int port = 27017;

            seeds.add(newServerAddress(host, port));

        } else {

            Iterator<JsonElement> iterator = servers.iterator();
            while (iterator.hasNext()) {
                JsonObject server = (JsonObject) iterator.next();

                JsonElement host = server.get("host");
                JsonElement port = server.get("port");
                seeds.add(newServerAddress(host == null ? "localhost" : host.getAsString(), port == null ? 27017 : port.getAsInt()));
            }
        }

        return seeds;
    }

    private static ServerAddress newServerAddress(String host, int port) {
        try {
            return new ServerAddress(host, port);
        } catch (UnknownHostException e) {
            throw new ConfigException("Unknown host [host=" + host + "]");
        }
    }

    public static class Event {
        private String db;
        private String collection;
        private List<String> fields;
        private String handler;

        public Event(String db, String collection, List<String> fields, String handler) {
            this.db = db;
            this.collection = collection;
            this.fields = fields;
            this.handler = handler;
        }

        public String getDb() {
            return db;
        }

        public void setDb(String db) {
            this.db = db;
        }

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

        public List<String> getFields() {
            return fields == null ? Collections.EMPTY_LIST : fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

    }

}
