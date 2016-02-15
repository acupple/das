package org.mokey.stormv.das.worker.cache;

import org.mokey.stormv.das.models.DalModels;

/**
 * Created by wcyuan on 2015/2/28.
 */
public class QueryCache {
    public static void put(DalModels.Request request, DalModels.Response response){

    }
    public static DalModels.Response get(DalModels.Request request){
        return null;
    }

    public static boolean present(DalModels.Request request){
        return false;
    }
}
