package org.mokey.stormv.das.worker.cache;

import org.mokey.stormv.das.models.DalModels;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * Created by wcyuan on 2015/2/28.
 */
public class SubCache {
    private final int appId;
    private final long evictionTime;

    private int maxSize = 10000;
    private int maxCount = 1000;

    private LoadingCache<DalModels.Request, DalModels.Response> loadingCache;

    public SubCache(int appId, long time){
        this.appId = appId;
        this.evictionTime = time;

        this.loadingCache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(evictionTime, TimeUnit.SECONDS)
                .build(
                        new CacheLoader<DalModels.Request, DalModels.Response>() {
                            @Override
                            public DalModels.Response load(DalModels.Request request) throws Exception {
                                return null;
                            }
                        });
    }

    public static void main(String[] args) throws Exception{
        LoadingCache<String, Integer> test = CacheBuilder.newBuilder()
                .maximumSize(5)
                .expireAfterWrite(1,TimeUnit.SECONDS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        System.out.println(s);
                        return Integer.parseInt(s);
                    }
                });
        for (int i = 0; i < 1000; i++) {
            test.get("" + i % 5);
        }

        test.get("5");
        Thread.sleep(500);
        test.get("3");
        System.out.println(test.getIfPresent("6"));
    }
}
