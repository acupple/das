package org.mokey.stormv.das.client;

import org.mokey.stormv.das.models.DalModels;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Not thread safe iterator of Response form remote server
 * Created by wcyuan on 2015/3/10.
 */
public class ResponseIterator {
    private Queue<DalModels.Response> responses;
    private int timeout;
    private boolean isLast = false;
    private long startTime;

    public ResponseIterator(int timeout){
        this.timeout = timeout;
        this.responses = new LinkedBlockingQueue<>();
    }

    public boolean hasNext() {
        this.startTime = System.currentTimeMillis();
        return !this.isLast || !this.responses.isEmpty();
    }

    public DalModels.Response next() {
        DalModels.Response response = null;
        while (!this.isTimeout()){
            response = responses.poll();
            if(response == null)
                continue;
            if(!response.hasResultSet()){
                this.isLast = true;
            }else {
                this.isLast = response.getResultSet().getLast();
            }
            break;
        }
        return response;
    }

    public DalModels.Response get(){
        return this.next();
    }

    private boolean isTimeout(){
        if(this.timeout <= 0){
            return false;
        }
        return System.currentTimeMillis() - this.startTime > this.timeout;
    }
}
