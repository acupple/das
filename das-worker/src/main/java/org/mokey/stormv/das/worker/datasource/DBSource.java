package org.mokey.stormv.das.worker.datasource;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by enousei on 2/17/16.
 */
public class DBSource {
    private String dbName;
    private String url;
    private String userName;
    private String password;
    private String driverClassName;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public static DBSource parse(ResultSet rs) throws SQLException{
        DBSource dbSource = new DBSource();

        dbSource.setDbName(rs.getString("dbName"));
        dbSource.setUrl(rs.getString("url"));
        dbSource.setUserName(rs.getString("userName"));
        dbSource.setPassword(rs.getString("password"));
        dbSource.setDriverClassName(rs.getString("driverClassName"));

        return dbSource;
    }
}
