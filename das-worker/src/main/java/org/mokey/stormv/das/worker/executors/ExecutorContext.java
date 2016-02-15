package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.worker.wraps.DalHintsWrapper;
import org.mokey.stormv.das.worker.wraps.DalSqlParametersWrapper;
import org.mokey.stormv.das.models.DalModels;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Parse or resolve the context for each executor
 * Do some check and transform
 * Created by wcyuan on 2015/2/5.
 */
public class ExecutorContext {
    private ChannelHandlerContext context;
    private DalModels.Request request;
    private int[] columnTypes;

    private String clientIp;

    private int querySize  = 1000;
    private int fetchSize = 20000;

    private DalHintsWrapper dalHintsWrapper;
    private DalSqlParametersWrapper dalSqlParametersWrapper;
    private List<DalSqlParametersWrapper> dalSqlParametersWrapperList;

    public ExecutorContext(DalModels.Request request, ChannelHandlerContext ctx){
        this.request = request;
        this.context = ctx;
        this.clientIp = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
        this.dalHintsWrapper = new DalHintsWrapper(request.getHints());
        this.dalSqlParametersWrapper = new DalSqlParametersWrapper(request.getMessage().getParameters());
        this.dalSqlParametersWrapperList = new LinkedList<DalSqlParametersWrapper>();
        if(request.getMessage().getParametersListCount() > 0){
            for (DalModels.SqlParameters parameters : request.getMessage().getParametersListList()){
                this.dalSqlParametersWrapperList.add(new DalSqlParametersWrapper(parameters));
            }
        }
        if(hasQueryHeader()){
            columnTypes = new int[request.getQueryHandersCount()];
            for (int i = 0; i < request.getQueryHandersCount(); i++) {
                DalModels.ColumnMata meta = request.getQueryHanders(i);
                columnTypes[i] = meta.getType();
            }
        }
    }

    public int[] getColumnTypes() {
        return columnTypes;
    }

    public DalModels.CommandType getCommandType(){
        return request.getCommandType();
    }

    public List<DalModels.ColumnMata> getQueryHeaders(){
        return request.getQueryHandersList();
    }

    public boolean hasQueryHeader(){
        return this.request.getQueryHandersCount() > 0;
    }
    public void setColumnTypes(int[] columnTypes){
        this.columnTypes = columnTypes;
    }

    public ChannelHandlerContext getContext(){
        return this.context;
    }

    public String getDbName(){
        return request.getLogicDb();
    }
    public DalModels.Transaction getTransaction(){
        return request.getTransaction();
    }

    public int getQuerySize(){
        if(request.getMessage().getQuerySize() > 0){
            this.querySize = request.getMessage().getQuerySize();
        }
        return this.querySize;
    }

    public int getFetchSize(){
        if(request.getMessage().getFetchSize() > 0){
            this.fetchSize = request.getMessage().getFetchSize();
        }
        return this.fetchSize;
    }

    public DalModels.Request getRequest() {
        return request;
    }

    public String getClientIp() {
        return clientIp;
    }

    public boolean getCompress(){
        return request.getCompress();
    }

    public String getSql(){
        return this.request.getMessage().getSql();
    }

    public List<String> getSqls(){
        return this.request.getMessage().getSqlsList();
    }

    public int getRequestId(){
        return this.request.getRequestId();
    }

    public DalHintsWrapper getDalHintsWrapper() {
        return dalHintsWrapper;
    }

    public DalSqlParametersWrapper getDalSqlParametersWrapper() {
        return dalSqlParametersWrapper;
    }

    public List<DalSqlParametersWrapper> getDalSqlParametersWrapperList() {
        return dalSqlParametersWrapperList;
    }

    public boolean isBatchWithOutParameters(){
        return this.request.getMessage().getSqlsCount() > 0;
    }

    public boolean isBatchWithParametersList(){
        return this.request.getMessage().getParametersListCount() > 0;
    }
}
