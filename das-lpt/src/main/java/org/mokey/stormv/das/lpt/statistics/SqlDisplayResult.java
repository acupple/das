package org.mokey.stormv.das.lpt.statistics;

import org.mokey.stormv.das.lpt.connectors.ConnectorType;
import org.mokey.stormv.das.lpt.models.DasFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wcyuan on 2015/3/16.
 */
public class SqlDisplayResult {
    private List<SqlDisplay> records;

    public SqlDisplayResult(){
        records = new ArrayList<SqlDisplay>();
        records.add(getItem(ConnectorType.JDBC));
        records.add(getItem(ConnectorType.JDBCBIG));
        records.add(getItem(ConnectorType.DAS));
        records.add(getItem(ConnectorType.AMEOBA));
        records.add(getItem(ConnectorType.DASWITHCOMPRESS));
    }

    private SqlDisplay getItem(ConnectorType type){
        SqlDisplay display = new SqlDisplay();
        String clickLink = "<a href=\"./webresources/myresource?type=%s\">%s</a>";
        display.setDatabaseAccessType(String.format(clickLink, type.getValue(), type.getName()));
        display.setSql(String.format(type.getSqlPattern(), DasFeature.get().getResultCount()));
        return display;
    }

    public List<SqlDisplay> getRecords() {
        return records;
    }

    public void setRecords(List<SqlDisplay> records) {
        this.records = records;
    }
}
