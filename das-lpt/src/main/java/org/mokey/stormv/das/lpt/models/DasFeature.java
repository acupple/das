package org.mokey.stormv.das.lpt.models;


public class DasFeature {
	
	private static DasFeature dasmeta = new DasFeature();
	
	static{
		dasmeta = new DasFeature();
		dasmeta.setEnableDasClientCache(true);
		dasmeta.setEnableDasServiceCache(true);
		dasmeta.setFetchSize(20000);
		dasmeta.setHost("dal.dev.nt.ctripcorp.com");
		dasmeta.setPort(8080);
		dasmeta.setQuerySize(2000);
		dasmeta.setResultCount(1000);
	}
	
	public static DasFeature get(){
		return dasmeta;
	}
	
	private String host;
	private int resultCount;
	private int port;
	private int fetchSize;
	private int querySize;
	private boolean enableDasServiceCache;
	private boolean enableDasClientCache;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getResultCount() {
		return resultCount;
	}
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getFetchSize() {
		return fetchSize;
	}
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
	public int getQuerySize() {
		return querySize;
	}
	public void setQuerySize(int querySize) {
		this.querySize = querySize;
	}
	public boolean isEnableDasServiceCache() {
		return enableDasServiceCache;
	}
	public void setEnableDasServiceCache(boolean enableDasServiceCache) {
		this.enableDasServiceCache = enableDasServiceCache;
	}
	public boolean isEnableDasClientCache() {
		return enableDasClientCache;
	}
	public void setEnableDasClientCache(boolean enableDasClientCache) {
		this.enableDasClientCache = enableDasClientCache;
	}
	
}
