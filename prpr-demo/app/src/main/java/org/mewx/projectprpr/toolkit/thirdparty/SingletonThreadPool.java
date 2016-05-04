package org.mewx.projectprpr.toolkit.thirdparty;

/**
 * Created by ghkjgod/LightNovel2 on 2015/10/29.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class SingletonThreadPool {
    private static ExecutorService  instance = Executors.newCachedThreadPool();
    private SingletonThreadPool (){

    }
    public static ExecutorService getInstance() {
        return instance;
    }
}