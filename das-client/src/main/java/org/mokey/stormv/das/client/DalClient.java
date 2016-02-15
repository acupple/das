package org.mokey.stormv.das.client;

import org.mokey.stormv.das.client.dal.IDalClient;
import org.mokey.stormv.das.models.DalModels;
import io.netty.channel.ChannelFuture;

import java.util.*;

/**
 * Created by wcyuan on 2015/1/30.
 */
public class DalClient implements IDalClient {

    private static final int DEFAULT_QUERY_SIZE = 1000;
    private static final int DEFAULT_FETCH_SIZE = 20000;

    public static final String CLIENT = "java das client";
    public static final String VERSION = "2.0.0";

    private Connection connection = null;
    private final String databaseName;
    private static boolean isCompressTransport = false;

    public DalClient(Connection connection, String databaseName) {
        this.databaseName = databaseName;
        this.connection = connection;
    }

    @Override
    public <T> List<T> query(String sql, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception {
        return this.query(sql, DEFAULT_QUERY_SIZE, DEFAULT_FETCH_SIZE, mapper, hints);
    }

    @Override
    public <T> List<T> query(String sql, int querySize, int fetchSize, RecordMapper<T> mapper ,DalModels.DalHints hints) throws Exception {
        return this.query(sql, querySize, fetchSize, DalModels.SqlParameters.getDefaultInstance(), mapper, hints);
    }

    @Override
    public <T> List<T> query(String sql, DalModels.SqlParameters parameters, RecordMapper<T> mapper,DalModels.DalHints hints) throws Exception {
        return this.query(sql, DEFAULT_QUERY_SIZE, DEFAULT_FETCH_SIZE, parameters, mapper, hints);
    }

    @Override
    public <T> List<T> query(String sql, int querySize, int fetchSize, DalModels.SqlParameters parameters, RecordMapper<T> mapper,DalModels.DalHints hints) throws Exception {
        return this.query(sql, parameters, querySize, fetchSize, null, mapper, hints);
    }

    @Override
    public <T> List<T> query(String sql, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper,DalModels.DalHints hints) throws Exception {
        return this.query(sql, null, headers, mapper, hints);
    }

    @Override
    public <T> List<T> query(String sql, DalModels.SqlParameters parameters, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper,DalModels.DalHints hints) throws Exception {
        return this.query(sql, parameters, DEFAULT_QUERY_SIZE, DEFAULT_FETCH_SIZE, headers, mapper, hints);
    }

    @Override
    public <T> List<T> query(String sql, int querySize, int fetchSize, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper,DalModels.DalHints hints) throws Exception {
        return this.query(sql, null, querySize, fetchSize, headers, mapper, hints);
    }

    @Override
    public <T> List<T> query(String sql, DalModels.SqlParameters parameters, int querySize, int fetchSize, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper,DalModels.DalHints hints) throws Exception {
        List<T> list = new ArrayList<>();
        DalModels.Request.Builder builder = this.newRequestBuilder(hints);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder()
                .setSql(sql)
                .setQuerySize(querySize)
                .setFetchSize(fetchSize);
        if(parameters != null){
            messageBuilder.setParameters(parameters);
        }
        builder.setCommandType(DalModels.CommandType.QUERY).setMessage(messageBuilder.build());
        if(headers != null)
            builder.addAllQueryHanders(headers);
        int queryId = RequestId.getInstance().getId();
        builder.setRequestId(queryId);

        this.connection.getChannel().writeAndFlush(builder.build());
        DalModels.Response resp;
        long start = System.currentTimeMillis();
        while(true){
            if(System.currentTimeMillis() - start > connection.getQueryTimeout()){
                throw  new Exception("Query time out"); //TODO: notify the server
            }
            resp = this.connection.getHandler().fetchResponse(queryId);
            if(resp == null)
                continue;
            if(resp.getQueryHandersCount() > 0){
                mapper.setColumnMetaData(resp.getQueryHandersList()); //Only one time, need check
            }
            if(resp.hasResultSet()){
                for (DalModels.Record record : resp.getResultSet().getRecordsList()) {
                    list.add(mapper.map(record));
                }
                if(resp.getResultSet().getLast()){
                    break;
                }
            }
        }
        this.connection.getHandler().remove(queryId);
        return list;
    }

    @Override
    public int update(String sql,DalModels.DalHints hints) throws Exception {
        return this.update(sql, null, hints);
    }

    @Override
    public int update(String sql, DalModels.SqlParameters parameters, DalModels.DalHints hints) throws Exception {
        DalModels.Request.Builder builder = this.newRequestBuilder(hints).setCommandType(DalModels.CommandType.UPDATE);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder()
                .setSql(sql);
        if(parameters != null) {
            messageBuilder.setParameters(parameters);
        }
        builder.setMessage(messageBuilder.build());

        int queryId = RequestId.getInstance().getId();
        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());
        DalModels.Response resp = this.connection.getHandler().waitResponse(queryId);
        return resp.getAffectRows();
    }

    @Override
    public int update(String sql, DalModels.SqlParameters parameters, List<DalModels.KeyHolder> holders,DalModels.DalHints hints) throws Exception {
        if(holders == null){
            holders = new ArrayList<>();
        }
        DalModels.Request.Builder builder = this.newRequestBuilder(hints).setCommandType(DalModels.CommandType.UPDATE);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder()
                .setSql(sql);
        if(parameters != null) {
            messageBuilder.setParameters(parameters);
        }
        builder.setMessage(messageBuilder.build());

        int queryId = RequestId.getInstance().getId();
        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());
        DalModels.Response resp = this.connection.getHandler().waitResponse(queryId);
        holders.addAll(resp.getKeyHoldersList());
        return resp.getAffectRows();
    }

    @Override
    public int[] batchUpdate(String sql, List<DalModels.SqlParameters> parametersList,DalModels.DalHints hints) throws Exception {
        DalModels.Request.Builder builder = this.newRequestBuilder(hints).setCommandType(DalModels.CommandType.BATCHUPDATE);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder()
                .setSql(sql);
        if(parametersList != null && parametersList.size() > 0) {
            messageBuilder.addAllParametersList(parametersList);
        }
        builder.setMessage(messageBuilder.build());

        int queryId = RequestId.getInstance().getId();
        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());
        DalModels.Response resp = connection.getHandler().waitResponse(queryId);
        int[] affectRows = null;
        if(resp.getAffectRowsListCount() > 0){
            int [] rows = new int[resp.getAffectRowsListCount()];
            for (int i = 0; i < resp.getAffectRowsListCount(); i++) {
                rows[i] = resp.getAffectRowsList(i);
            }
            return rows;
        }
        affectRows = new int[]{ resp.getAffectRows() };
        return affectRows;
    }

    @Override
    public int[] batchUpdate(String sql, DalModels.DalHints hints, DalModels.SqlParameters... parametersList) throws Exception {
        return this.batchUpdate(sql, Arrays.asList(parametersList), hints);
    }

    @Override
    public int[] batchUpdate(List<String> sqls, DalModels.DalHints hints) throws Exception {
        DalModels.Request.Builder builder = this.newRequestBuilder(hints).setCommandType(DalModels.CommandType.BATCHUPDATE);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder()
                .addAllSqls(sqls);
        builder.setMessage(messageBuilder.build());

        int queryId = RequestId.getInstance().getId();
        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());
        DalModels.Response resp = connection.getHandler().waitResponse(queryId);
        int[] affectRows;
        if(resp.getAffectRowsListCount() > 0){
            int [] rows = new int[resp.getAffectRowsListCount()];
            for (int i = 0; i < resp.getAffectRowsListCount(); i++) {
                rows[i] = resp.getAffectRowsList(i);
            }
            return rows;
        }
        affectRows = new int[]{ resp.getAffectRows() };
        return affectRows;
    }

    @Override
    public int[] batchUpdate(DalModels.DalHints hints, String... sqls) throws Exception {
        return this.batchUpdate(Arrays.asList(sqls), hints);
    }

    @Override
    public Map<String, ?> call(String sql, DalModels.DalHints hints) throws Exception {
        return  this.call(sql, null, hints);
    }

    @Override
    public Map<String, ?> call(String sql, DalModels.SqlParameters parameters,DalModels.DalHints hints) throws Exception {
        Map<String, Object> map = new HashMap<>();
        DalModels.Request.Builder builder = this.newRequestBuilder(hints).setCommandType(DalModels.CommandType.STOREPROCEDURE);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder()
                .setSql(sql);
        if(parameters != null){
            messageBuilder.setParameters(parameters);
        }
        builder.setMessage(messageBuilder.build());

        int queryId = RequestId.getInstance().getId();
        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());
        DalModels.Response resp = connection.getHandler().waitResponse(queryId);
        if(resp.getKeyHoldersCount() > 0){
            for (DalModels.KeyHolder keyHolder : resp.getKeyHoldersList()) {
                for (DalModels.KeyValue keyValue : keyHolder.getHolderList())
                    map.put(keyValue.getKey(), keyValue.getValue());
            }
        }
        return map;
    }

    @Override
    public int[] batchCall(String sql,DalModels.DalHints hints, DalModels.SqlParameters... parametersList) throws Exception {
        return this.batchCall(sql, Arrays.asList(parametersList), hints);
    }

    @Override
    public int[] batchCall(String sql, List<DalModels.SqlParameters> parametersList,DalModels.DalHints hints) throws Exception {
        int[] results = null;
        DalModels.Request.Builder builder = this.newRequestBuilder(hints).setCommandType(DalModels.CommandType.BATCHSTOREPROCEDURE);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder()
                .setSql(sql);
        if(parametersList != null && parametersList.size() > 0){
            messageBuilder.addAllParametersList(parametersList);
        }

        builder.setMessage(messageBuilder.build());
        int queryId = RequestId.getInstance().getId();
        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());
        DalModels.Response resp = connection.getHandler().waitResponse(queryId);
        if(resp.getAffectRowsListCount() > 0){
            results = new int[resp.getAffectRowsListCount()];
            for (int i = 0; i < resp.getAffectRowsListCount(); i++) {
                results[i] = resp.getAffectRowsList(i);
            }
        }
        return results;
    }

    public synchronized void setCompressTransport(boolean isCompress) throws Exception{
        if(!(isCompress ^ isCompressTransport)){
            return;
        }
        if(isCompress) {
            final int requestId = RequestId.getInstance().getId();
            DalModels.Request.Builder builder = newRequestBuilder(DalModels.DalHints.getDefaultInstance())
                    .setCompress(true)
                    .setRequestId(requestId)
                    .setCommandType(DalModels.CommandType.SETTING);
            ChannelFuture future = this.connection.getChannel().writeAndFlush(builder.build());
            DalModels.Response response = this.connection.getHandler().waitResponse(requestId);
            if(response != null){
                ClientInitializer.addCompressDecoderEncoder(future);
            }
        }else {
            final int requestId = RequestId.getInstance().getId();
            DalModels.Request.Builder builder = newRequestBuilder(DalModels.DalHints.getDefaultInstance())
                    .setCompress(false)
                    .setRequestId(requestId)
                    .setCommandType(DalModels.CommandType.SETTING);
            ChannelFuture future = this.connection.getChannel().writeAndFlush(builder.build());
            DalModels.Response response = this.connection.getHandler().waitResponse(requestId);
            if(response != null){
                ClientInitializer.removeCompressDecoderEncoder(future);
            }
        }
    }

    private DalModels.Request.Builder newRequestBuilder(DalModels.DalHints hints) {
        DalModels.Request.Builder builder = DalModels.Request.newBuilder();
        builder.setHints(DalModels.DalHints.newBuilder().build());
        builder.setLogicDb(databaseName);
        builder.setVersion(DalModels.Version.newBuilder().setClient(CLIENT)
                .setVersion(VERSION).build());
        builder.setHints(hints);
        return builder;
    }
}
