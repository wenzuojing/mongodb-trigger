package org.wzj.mongodb.trigger;

/**
 * Created by wens on 15/8/13.
 */
public class ClassUtils {


    public static <T> T newInstance(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        return (T) Thread.currentThread().getContextClassLoader().loadClass(name).newInstance();

    }
}
