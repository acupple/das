package org.mokey.stormv.das.client.dal;

import org.mokey.stormv.das.client.RecordMapper;
import org.mokey.stormv.das.models.DalModels;

import java.util.List;
import java.util.Map;

/**
 * Created by wcyuan on 2015/1/30.
 */
public interface IDalClient {
    /**
     * Query against the given sql, the result set will be processed by
     * the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param mapper The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception;

    /**
     * Query against the given sql, query size and fetch size. the result set will be processed by
     * the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param querySize Specified the query size which decide each inner result size
     *                  transported between DAS Service and client.
     * @param fetchSize Specified the fetch size equals with ResultSet.FetchSize
     * @param mapper The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, int querySize, int fetchSize, RecordMapper<T> mapper, DalModels.DalHints hints)throws Exception;

    /**
     * Query against the given sql and parameters, the result set will be processed by
     * the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parameters A container that holds all the necessary parameters
     * @param mapper The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, DalModels.SqlParameters parameters, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception;

    /**
     * Query against the given sql and parameters, the result set will be processed by
     * the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param querySize Specified the query size which decide each inner result size
     *                  transported between DAS Service and client.
     * @param fetchSize Specified the fetch size equals with ResultSet.FetchSize
     * @param parameters A container that holds all the necessary parameters
     * @param mapper  The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, int querySize, int fetchSize, DalModels.SqlParameters parameters, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception;

    /**
     * Query against the given sql with columns information,  the result set will be processed by
     * the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param headers The query columns meta information
     * @param mapper The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception;

    /**
     * Query against the given sql with columns information,  the result set will be processed by
     * the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parameters A container that holds all the necessary parameters
     * @param headers The query columns meta information
     * @param mapper The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, DalModels.SqlParameters parameters, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception;

    /**
     * Query against the given sql with columns information, query size and fetch size,
     * the result set will be processed by the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param querySize Specified the query size which decide each inner result size
     *                  transported between DAS Service and client.
     * @param fetchSize Specified the fetch size equals with ResultSet.FetchSize
     * @param headers The query columns meta information
     * @param mapper The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, int querySize, int fetchSize, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception;

    /**
     * Query against the given sql with columns information, query size and fetch size,
     * the result set will be processed by the given <code>RecordMapper</code>, and then return POJO collection
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parameters A container that holds all the necessary parameters
     * @param querySize Specified the query size which decide each inner result size
     *                  transported between DAS Service and client.
     * @param fetchSize Specified the fetch size equals with ResultSet.FetchSize
     * @param headers The query columns meta information
     * @param mapper The record mapper object that will map the database
     *               row-record to the Java Object
     * @param hints settings
     * @param <T> Generic POJO/Entity type parameter.
     * @return The mapped POJO collection.
     * @throws Exception
     */
    <T> List<T> query(String sql, DalModels.SqlParameters parameters, int querySize, int fetchSize, List<DalModels.ColumnMata> headers, RecordMapper<T> mapper, DalModels.DalHints hints) throws Exception;

    /**
     * Execute update against the given sql
     * @param sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param hints settings
     * @return The affected row numbers
     * @throws Exception
     */
    int update(String sql, DalModels.DalHints hints) throws Exception;

    /**
     * Execute update against the given sql with parameter place holders,"?" for MySQL
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parameters A container that holds all the necessary parameters
     * @param hints settings
     * @return The affected row numbers
     * @throws Exception
     */
    int update(String sql, DalModels.SqlParameters parameters, DalModels.DalHints hints) throws Exception;

    /**
     * Execute update against the given sql with parameter place holders,"?" for MySQL
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parameters A container that holds all the necessary parameters
     * @param holders generated keys
     * @param hints settings
     * @return The affected row numbers
     * @throws Exception
     */
    int update(String sql, DalModels.SqlParameters parameters, List<DalModels.KeyHolder> holders, DalModels.DalHints hints) throws Exception;

    /**
     * Execute batch update against the given sql with parameter place holders.
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param hints settings
     * @param parametersList An array of update parameters
     * @return An array of update counts containing one element for each
     *          command in the batch
     * @throws Exception
     */
    int[] batchUpdate(String sql, DalModels.DalHints hints, DalModels.SqlParameters... parametersList) throws Exception;

    /**
     * Execute batch update against the given sql with parameter place holders.
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parametersList An list of update parameters
     * @param hints settings
     * @return An array of update counts containing one element for each
     *          command in the batch
     * @throws Exception
     */
    int[] batchUpdate(String sql, List<DalModels.SqlParameters> parametersList, DalModels.DalHints hints) throws Exception;

    /**
     * Execute batch update against the given sqls
     * @param sqls sql arrays of <code>String</code> which are added into batch
     * @param hints settings
     * @return An array of update counts containing one element for each
     *          command in the batch
     * @throws Exception
     */
    int[] batchUpdate(List<String> sqls, DalModels.DalHints hints) throws Exception;

    /**
     * Execute batch update against the given sqls
     * @param hints settings
     * @param sqls sql arrays of <code>String</code> which are added into batch
     * @return An array of update counts containing one element for each
     *          command in the batch
     * @throws Exception
     */
    int[] batchUpdate(DalModels.DalHints hints, String... sqls) throws Exception;
    /**
     * Execute store procedure against the given sql.
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param hints settings
     * @return the result collection.
     * @throws Exception
     */
    Map<String, ?> call(String sql, DalModels.DalHints hints) throws Exception;

    /**
     * Execute store procedure against the given sql with parameter place holders.
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parameters A container that holds all the necessary parameters
     * @param hints settings
     * @return he returned update count and result set in order
     * @throws Exception
     */
    Map<String, ?> call(String sql, DalModels.SqlParameters parameters, DalModels.DalHints hints) throws Exception;

    /**
     * Execute batch for the specified store procedure against given sql with parameter place holders.
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param hints settings
     * @param parametersList An array of update parameters
     * @return how many rows been affected for each of parameters
     * @throws Exception
     */
    int[] batchCall(String sql, DalModels.DalHints hints, DalModels.SqlParameters... parametersList) throws Exception;

    /**
     * Execute batch for the specified store procedure against given sql with parameter place holders.
     * @param sql sql The <code>String</code> object that is the SQL statement to
     *            be sent to the database.
     * @param parametersList An array of update parameters
     * @param hints settings
     * @return how many rows been affected for each of parameters
     * @throws Exception
     */
    int[] batchCall(String sql, List<DalModels.SqlParameters> parametersList, DalModels.DalHints hints) throws Exception;
}
