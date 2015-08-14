package org.wzj.mongodb.trigger;

/**
 * Created by wens on 15/8/13.
 */
public class TestHandler implements Handler {
    @Override
    public void process(Stream stream) {
        System.out.println(stream);
    }
}
