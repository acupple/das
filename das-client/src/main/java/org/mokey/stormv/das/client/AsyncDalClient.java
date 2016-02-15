package org.mokey.stormv.das.client;

import org.mokey.stormv.das.client.dal.IAsyncDalClient;
import org.mokey.stormv.das.models.DalModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by wcyuan on 2015/1/30.
 */
public class AsyncDalClient implements IAsyncDalClient {

    private final static int DEFAULT_QUERY_SIZE = 1000;
    private final static int DEFAULT_FETCH_SIZE = 20000;

    private ExecutorService service;
    private Connection connection = null;
    private final String databaseName;

    public AsyncDalClient(ExecutorService service, Connection connection, String databaseName) {
        this.service = service;
        this.connection = connection;
        this.databaseName = databaseName;
    }

    public AsyncDalClient(Connection connection, String databaseName) {
        this(Executors.newCachedThreadPool(),connection, databaseName);
    }

    @Override
    public <V> Future<List<V>> query(String sql, int querySize, int fetchSize, List<DalModels.ColumnMata> headers, final RecordMapper<V> mapper) {
        DalModels.Request.Builder builder = BuilderUtils.newRequestBuilder(databaseName);
        builder.setCommandType(DalModels.CommandType.QUERY).setMessage(
                DalModels.RequestMessage.newBuilder().setSql(sql).setQuerySize(querySize)
                        .setFetchSize(fetchSize).build());
        if(headers != null)
            builder.addAllQueryHanders(headers);
        final int queryId = builder.hashCode();
        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());

        return this.service.submit(new Callable<List<V>>(){
            @Override
            public List<V> call() throws Exception {
                List<V> list = new ArrayList<>();
                DalModels.Response resp;
                while(true){
                    resp = connection.getHandler().fetchResponse(queryId);
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
                return list;
            }
        });
    }

    @Override
    public <V> Future<List<V>> query(String sql, List<DalModels.ColumnMata> headers, RecordMapper<V> mapper) {
       return this.query(sql, DEFAULT_QUERY_SIZE, DEFAULT_FETCH_SIZE, headers, mapper);
    }

    @Override
    public <V> Future<List<V>> query(String sql, RecordMapper<V> mapper) {
        return this.query(sql, null, mapper);
    }

    @Override
    public Future<Integer> update(String sql, DalModels.SqlParameters parameters) {
        DalModels.Request.Builder builder = BuilderUtils.newRequestBuilder(databaseName);
        builder.setCommandType(DalModels.CommandType.UPDATE)
                .setMessage(DalModels.RequestMessage.newBuilder().setSql(sql).build());

        final int queryId = builder.hashCode();

        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());

        return this.service.submit(new Callable<Integer>(){
            @Override
            public Integer call() throws Exception {
                DalModels.Response resp;
                while (true){
                    resp = connection.getHandler().fetchResponse(queryId);
                    if(resp == null)
                        continue;
                   return  resp.getAffectRows();
                }
            }
        });
    }

    @Override
    public Future<Integer[]> batchUpdate(String[] sqls) {
        DalModels.Request.Builder builder = BuilderUtils.newRequestBuilder(databaseName);
        DalModels.RequestMessage.Builder mssageBuilder = DalModels.RequestMessage.newBuilder();
        for (int i = 0; i < sqls.length; i++) {
            mssageBuilder.addSqls(sqls[i]);
        }
        builder.setCommandType(DalModels.CommandType.UPDATE).setMessage(mssageBuilder.build());
        final int queryId = builder.hashCode();

        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());

        return this.service.submit(new Callable<Integer[]>() {
            @Override
            public Integer[] call() throws Exception {
                DalModels.Response resp;
                while (true){
                    resp = connection.getHandler().fetchResponse(queryId);
                    if(resp == null)
                        continue;
                    return resp.getAffectRowsListList().toArray(new Integer[0]);
                }
            }
        });
    }

    @Override
    public Future<Integer[]> batchUpdate(String sql, DalModels.SqlParameters[] parametersList) {
        DalModels.Request.Builder builder = BuilderUtils.newRequestBuilder(databaseName);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder();
        messageBuilder.setSql(sql);
        for(DalModels.SqlParameters param : parametersList){
            messageBuilder.addParametersList(param);
        }
        builder.setCommandType(DalModels.CommandType.UPDATE).setMessage(messageBuilder.build());
        final int queryId = builder.hashCode();

        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());

        return this.service.submit(new Callable<Integer[]>() {
            @Override
            public Integer[] call() throws Exception {
                DalModels.Response resp;
                while (true){
                    resp = connection.getHandler().fetchResponse(queryId);
                    if(resp == null)
                        continue;
                    return resp.getAffectRowsListList().toArray(new Integer[0]);
                }
            }
        });
    }

    @Override
    public Future<Map<String, ?>> call(String callString, DalModels.SqlParameters parameters) {
        DalModels.Request.Builder builder = BuilderUtils.newRequestBuilder(databaseName);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder();
        messageBuilder.setSql(callString);
        messageBuilder.setParameters(parameters);
        builder.setCommandType(DalModels.CommandType.STOREPROCEDURE).setMessage(messageBuilder.build());
        final int queryId = builder.hashCode();

        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());

        return this.service.submit(new Callable<Map<String, ?>>() {
            @Override
            public Map<String, ?> call() throws Exception {
                DalModels.Response resp;
                Map<String, String> map = new HashMap<>();
                while (true){
                    resp = connection.getHandler().fetchResponse(queryId);
                    if(resp == null)
                        continue;
                    for (DalModels.KeyHolder holder: resp.getKeyHoldersList()){
                       for (DalModels.KeyValue keyValue: holder.getHolderList()){
                            map.put(keyValue.getKey(), keyValue.getValue());
                       }
                    }
                }
            }
        });
    }

    @Override
    public Future<int[]> batchCall(String callString, DalModels.SqlParameters[] parametersList) {
        DalModels.Request.Builder builder = BuilderUtils.newRequestBuilder(databaseName);
        DalModels.RequestMessage.Builder messageBuilder = DalModels.RequestMessage.newBuilder();
        messageBuilder.setSql(callString);
        for(DalModels.SqlParameters param : parametersList){
            messageBuilder.addParametersList(param);
        }
        builder.setCommandType(DalModels.CommandType.STOREPROCEDURE).setMessage(messageBuilder.build());
        final int queryId = builder.hashCode();

        builder.setRequestId(queryId);
        this.connection.getChannel().writeAndFlush(builder.build());

        return this.service.submit(new Callable<int[]>() {
            @Override
            public int[] call() throws Exception {
                DalModels.Response resp;
                Map<String, String> map = new HashMap<>();
                while (true){
                    resp = connection.getHandler().fetchResponse(queryId);
                    if(resp == null)
                        continue;
                    for (DalModels.KeyHolder holder: resp.getKeyHoldersList()){
                        for (DalModels.KeyValue keyValue: holder.getHolderList()){
                            map.put(keyValue.getKey(), keyValue.getValue());
                        }
                    }
                }
            }
        });
    }
}
