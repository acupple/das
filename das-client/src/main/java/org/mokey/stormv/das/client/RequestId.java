package org.mokey.stormv.das.client;

import java.util.Date;

/**
 * Created by wcyuan on 2015/2/3.
 */
public class RequestId {
    private static RequestId ourInstance = new RequestId();

    public static RequestId getInstance() {
        return ourInstance;
    }

    private RequestId() {
    }

    public int getId(){
        return (int)new Date().getTime();
    }
}
