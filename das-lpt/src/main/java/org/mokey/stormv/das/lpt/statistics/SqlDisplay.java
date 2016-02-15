package org.mokey.stormv.das.lpt.statistics;

/**
 * Created by wcyuan on 2015/3/16.
 */
public class SqlDisplay {
    private String databaseAccessType;
    private String sql;

    public String getDatabaseAccessType() {
        return databaseAccessType;
    }

    public void setDatabaseAccessType(String databaseAccessType) {
        this.databaseAccessType = databaseAccessType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
