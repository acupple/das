package org.mokey.stormv.das.client.dal;

import org.mokey.stormv.das.client.RecordMapper;
import org.mokey.stormv.das.models.DalModels;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by wcyuan on 2015/1/27.
 */
public interface IAsyncDalClient {
    <V> Future<List<V>> query(String sql, int querySize, int fetchSize, List<DalModels.ColumnMata> headers, final RecordMapper<V> mapper);
    <V> Future<List<V>> query(String sql, List<DalModels.ColumnMata> headers, final RecordMapper<V> mapper);
    <V> Future<List<V>> query(String sql, final RecordMapper<V> mapper);
    Future<Integer> update(String sql, DalModels.SqlParameters parameters);
    Future<Integer[]> batchUpdate(String[] sqls);
    Future<Integer[]> batchUpdate(String sql, DalModels.SqlParameters[] parametersList);
    Future<Map<String, ?>> call(String callString, DalModels.SqlParameters parameters);
    Future<int[]> batchCall(String callString, DalModels.SqlParameters[] parametersList);
}
