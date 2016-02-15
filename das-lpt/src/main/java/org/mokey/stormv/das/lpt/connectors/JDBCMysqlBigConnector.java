package org.mokey.stormv.das.lpt.connectors;

import org.mokey.stormv.das.lpt.models.Ameoba;
import org.mokey.stormv.das.lpt.models.DasFeature;
import org.mokey.stormv.das.lpt.statistics.Statistics;
import org.mokey.stormv.das.lpt.statistics.StatisticsLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by wcyuan on 2015/3/13.
 */
public class JDBCMysqlBigConnector extends Connector{
    private static  ConnectorType type = ConnectorType.JDBCBIG;

    @Override
    public ConnectorType process() throws Exception {
        Statistics statistics = StatisticsLogger.getStatistics(type);

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try
        {
            //conn = DataSourceLocator.newInstance().getDataSource(type.getDbName()).getConnection();
            statement = conn.prepareStatement(String.format(type.getSqlPattern(), DasFeature.get().getResultCount()));
            statistics.getServiceHandleTime().start();
            rs = statement.executeQuery();
            statistics.getServiceHandleTime().end();

            while(rs.next()){
                statistics.getDecodeTime().start();
                this.models.add(Ameoba.parseFromJDBC(rs));
                statistics.getDecodeTime().end();
            }

            statistics.getRecordCount().addAndGet(this.models.size());
        }catch(Exception e){
            throw e;
        }finally{
            if(rs != null)
                rs.close();
            if(statement != null)
                statement.close();
            if(conn != null)
                conn.close();
        }
        return type;
    }
}
