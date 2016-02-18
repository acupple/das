package org.mokey.stormv.das.worker.utils;

import javax.sql.DataSource;

import org.mokey.stormv.das.worker.datasource.DataSourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The database resource factory
 * @author wcyuan
 */
public class DataSourceUtil {
	
	private static Logger logger = LoggerFactory.getLogger(DataSourceUtil.class);
	
	/**
	 * Initialize the specified all in one keys for warm up
	 * @param allInOneKeys
	 */
	public static void init(String... allInOneKeys){
		if(allInOneKeys != null){
			for (int i = 0; i < allInOneKeys.length; i++) {
				try{
					DataSourceLocator.getInstance().getDataSource(allInOneKeys[i]);
					logger.info(String.format("The database %s has been warmed up", allInOneKeys[i]));
				}catch(Exception e){
					logger.error(String.format("Warm up database %s failed.", allInOneKeys[i]));
				}
			}
		}
	}
	
	public static DataSource getDataSource(String allInOneKey) throws Exception{
		return DataSourceLocator.getInstance().getDataSource(allInOneKey);
	}
}
