package org.mokey.stormv.das.lpt.connectors;

/**
 * Created by wcyuan on 2015/3/10.
 */
public enum ConnectorType {
    JDBC("JDBC Connect",
            "SELECT TOP %s Id,quantity,type,address,last_changed FROM [HotelPubDB].[dbo].[dal_client_test_big]",
            "MultiThreadingTest", 0),
    JDBCBIG("JDBC Big Connect",
            "SELECT id, name, content FROM dao_test.ameobaTable LIMIT %s",
            "dao_test", 2),
    DAS("DAS Client",
            "SELECT id, name, content FROM dao_test.ameobaTable LIMIT %s",
            "dao_test", 3),
    AMEOBA("Ameoba Proxy",
            "SELECT id, name, content FROM dao_test.ameobaTable LIMIT %s",
            "dao_test_ameola", 4),
    DASWITHCOMPRESS("Das Compress Client",
            "SELECT id, name, content FROM dao_test.ameobaTable LIMIT %s",
            "dao_test", 5);

    private int value;
    private String name;
    private String sqlPattern;
    private String dbName;

    private ConnectorType(String name, String sqlPattern, String dbName, int type){
        this.name = name;
        this.sqlPattern = sqlPattern;
        this.dbName = dbName;
        this.value = type;
    }

    public int getValue(){
        return this.value;
    }

    public String getSqlPattern() {
        return sqlPattern;
    }

    public String getDbName() {
        return dbName;
    }

    public String getName() {
        return name;
    }

    public static ConnectorType parse(int value){
        switch (value)
        {
            case 0:
                return JDBC;
            case 2:
                return JDBCBIG;
            case 3:
                return DAS;
            case 4:
                return AMEOBA;
            case 5:
                return DASWITHCOMPRESS;
            default:
                break;
        }
        return JDBC;
    }
}
