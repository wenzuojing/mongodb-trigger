package org.wzj.mongodb.trigger;

/**
 * Created by wens on 15/8/13.
 */
public interface Handler {

    public void process(Stream stream);

}
