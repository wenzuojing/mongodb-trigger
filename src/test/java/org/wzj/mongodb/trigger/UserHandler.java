package org.wzj.mongodb.trigger;

/**
 * Created by wens on 15/8/13.
 */
public class UserHandler implements Handler {
    @Override
    public void process(Stream stream) {
        //do something
        System.out.println(stream);
    }
}
