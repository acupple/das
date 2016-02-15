package org.mokey.stormv.das.worker.executors;

import org.mokey.stormv.das.worker.cache.QueryCache;
import org.mokey.stormv.das.worker.connections.DalConnection;
import org.mokey.stormv.das.models.DalModels;
import org.mokey.stormv.das.models.codec.FieldConvert;
import io.netty.channel.ChannelFuture;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by wcyuan on 2015/2/5.
 */
public abstract class Executor {
    private static final String GENERATED_KEY = "GENERATED_KEY";

    protected ExecutorContext context;
    private DalModels.Response.Builder builder;

    /**
     * Set executor context
     */
    public final void setContext(ExecutorContext context) {
        this.context = context;
        this.builder = newResponseBuilder();
    }

    public ExecutorContext getContext(){
        return this.context;
    }

    /**
     * Write and flush response to netty output transporter
     * And re-created the response builder.
     */
    public final ChannelFuture buildAndFlush() {
        DalModels.Response response = builder.build();
        ChannelFuture future = this.context.getContext().writeAndFlush(response);
        this.builder = newResponseBuilder();
        return future;
    }

    /**
     * Created a new response builder.
     * @return proto-buf response builder
     */
    private final DalModels.Response.Builder newResponseBuilder() {
        return DalModels.Response.newBuilder().setRequestId(context.getRequestId());
    }

    /**
     * Obtain the query header meta information from the <code>ResultSet</code> object
     * And build them into response.
     * @param rs the <code>ResultSet</code> object
     * @throws Exception
     */
    public final void addQueryHeaders(ResultSet rs) throws Exception {
        if(context.hasQueryHeader()){
            builder.addAllQueryHanders(context.getQueryHeaders());
        }else{
            ResultSetMetaData metaData = rs.getMetaData();
            int[] columnTypes  = new int[metaData.getColumnCount()];
            for (int i = 0; i < columnTypes.length; i++) {
                int currentType = metaData.getColumnType(i + 1);
                columnTypes[i] = currentType;
                builder.addQueryHanders(
                        DalModels.ColumnMata.newBuilder().
                                setName(metaData.getColumnName(i + 1)).
                                setType(currentType).
                                build());
            }
            context.setColumnTypes(columnTypes);
        }
    }

    /**
     * Parse columns from one record of <code>ResultSet</code> object,
     * convert them to <code>DalModels.AvailableType</code> objects,
     * @param rs the <code>ResultSet</code> object
     * @return a <code>DalModels.Record</code> object which contains the converted columns
     * @throws SQLException
     */
    public final DalModels.Record newRecord(ResultSet rs)
            throws SQLException {
        DalModels.Record.Builder rd = DalModels.Record.newBuilder();
        for (int i = 0; i < context.getColumnTypes().length; i++) {
            rd.addFields(FieldConvert.getField(rs, i + 1, context.getColumnTypes()[i]));
        }
        return rd.build();
    }

    /**
     * Build <code>DalModels.InnerResultSet</code> into response
     * The size of inner result set is limited by query size and the query count.
     * @param innerResultSet a <code>DalModels.InnerResultSet</code> object which contains a fragment of query result set.
     *
     */
    public final void setResultSet(DalModels.InnerResultSet innerResultSet, boolean needCache) {
        this.builder.setResultSet(innerResultSet);
        DalModels.Response response = builder.build();
        this.context.getContext().writeAndFlush(response);
        this.builder = newResponseBuilder();
        if(needCache){
            QueryCache.put(context.getRequest(), response);
        }
    }

    public final void setResultSet(DalModels.InnerResultSet innerResultSet){
        this.setResultSet(innerResultSet, false);
    }

    public final void addAffectRowsList(int[] results) {
        for (int i = 0; i < results.length; i++) {
            this.builder.addAffectRowsList(results[i]);
        }
    }

    public final void setAffectRows(int rows) {
        this.builder.setAffectRows(rows);
    }

    public final void addKeyHolders(Map<String, String> holders){
        for (String key : holders.keySet()){
            builder.addKeyHolders(
                    DalModels.KeyHolder.newBuilder().addHolder(
                            DalModels.KeyValue.newBuilder().setKey(key)
                                    .setValue(holders.get(key))
                                    .build())
                            .build());
        }
    }

    /**
     * Swallow any throwable to response and return immediately
     * @param exception
     */
    public final void throwException(Exception exception) {
        String message;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            message = "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            message = "bad getErrorInfoFromException";
        }
        this.builder.setException(message);
        this.buildAndFlush();
    }

    /**
     * Fetch the generated keys from the result set which is return from update/store procedure.
     * @param rs standard JDBC <code>ResultSet</code> instance
     * @throws Exception
     */
    public final void buildGeneratedKeys(ResultSet rs) throws SQLException {
        if (rs != null) {
            while (rs.next()) {
                Object generatedKey = rs.getObject(1);
                builder.addKeyHolders(
                        DalModels.KeyHolder.newBuilder().addHolder(
                                DalModels.KeyValue.newBuilder().setKey(GENERATED_KEY)
                                        .setValue(generatedKey.toString())
                                        .build())
                                .build());
            }
        }
    }

    public final void addTransaction(DalModels.Transaction transaction){
        this.builder.setTransaction(transaction);
    }

    /**
     * Process the database access request of the <code>ExecutorContext</code>
     * @param connection The connection of specified database.
     * @throws Exception Any unhandled exception
     */
    public abstract void execute(DalConnection connection) throws Exception;
}
