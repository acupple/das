package org.mokey.stormv.das.worker.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Created by enousei on 2/17/16.
 */
public class DataSourceLocator {
    private static DataSourceLocator ourInstance = new DataSourceLocator();

    public static DataSourceLocator getInstance() {
        return ourInstance;
    }

    private DataSourceLocator() {
    }

    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    private final DataSource dataSource;

    {
        dataSource = new DataSource();
        Properties dbProperties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("db.properties");
            dbProperties.load(inputStream);
            String dasUrl = dbProperties.getProperty("datasource.url");
            String userName = dbProperties.getProperty("datasource.username");
            String password = dbProperties.getProperty("datasource.password");
            String driverClassName = dbProperties.getProperty("datasource.driver-class-name");

            PoolProperties poolProperties = create(dasUrl, userName, password, driverClassName);
            dataSource.setPoolProperties(poolProperties);
        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DataSource getDataSource(String dbName){
        if(dataSourceMap.containsKey(dbName)){
            return dataSourceMap.get(dbName);
        }

        DBSource dbSource = query(dbName);
        if(dbSource.getUrl() == null || dbSource.getDriverClassName() == null){
            return null;
        }
        PoolProperties p = create(dbSource.getUrl(), dbSource.getUserName(), dbSource.getPassword(), dbSource.getDriverClassName());
        DataSource ds = new DataSource();
        ds.setPoolProperties(p);

        dataSourceMap.put(dbName, ds);

        return ds;
    }

    public DBSource query(String dbName){
        DBSource dbSource = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM das.datasource WHERE dbName = ?");
            preparedStatement.setString(1, dbName);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                dbSource = DBSource.parse(rs);
                break;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return dbSource;
    }

    public PoolProperties create(String url, String userName, String password, String driverName){
        PoolProperties p = new PoolProperties();
        p.setUrl(url);
        p.setDriverClassName(driverName);
        if(userName != null && !userName.isEmpty()) {
            p.setUsername(userName);
        }
        if(password != null && !password.isEmpty()) {
            p.setPassword(password);
        }
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return p;
    }
}
