package org.mokey.stormv.das.worker.datasource;

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
}
