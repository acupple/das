package org.mokey.stormv.das.lpt.statistics;

public class JsonData {
	private String databaseAccessType;
	/**
	 * The request count
	 */
	private int requestCount;
	
	/**
	 * All-all time
	 */
	private float allAllTime;
	
	/**
	 * JDBC: executQuery time
	 * Direct: prepared statement + executeQuery
	 * DAS: Query executor time
	 */
	private float serviceHandleTime;
	
	private float resultDecodeTime;
	private float resultSize;

	private int binarySize;

	private float binaryAvgSize;
	
	private float networkTime;
	
	private float createClient;

	public float getNetworkTime() {
		return networkTime;
	}

	public void setNetworkTime(float networkTime) {
		this.networkTime = networkTime;
	}

	public String getDatabaseAccessType() {
		return databaseAccessType;
	}

	public void setDatabaseAccessType(String databaseAccessType) {
		this.databaseAccessType = databaseAccessType;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}

	public float getAllAllTime() {
		return allAllTime;
	}

	public void setAllAllTime(float allAllTime) {
		this.allAllTime = allAllTime;
	}

	public float getServiceHandleTime() {
		return serviceHandleTime;
	}

	public void setServiceHandleTime(float serviceHandleTime) {
		this.serviceHandleTime = serviceHandleTime;
	}

	public float getResultDecodeTime() {
		return resultDecodeTime;
	}

	public void setResultDecodeTime(float resultDecodeTime) {
		this.resultDecodeTime = resultDecodeTime;
	}

	public float getResultSize() {
		return resultSize;
	}

	public void setResultSize(float resultSize) {
		this.resultSize = resultSize;
	}

	public float getCreateClient() {
		return createClient;
	}

	public void setCreateClient(float createClient) {
		this.createClient = createClient;
	}

	public int getBinarySize() {
		return binarySize;
	}

	public void setBinarySize(int binarySize) {
		this.binarySize = binarySize;
	}

	public float getBinaryAvgSize() {
		return binaryAvgSize;
	}

	public void setBinaryAvgSize(float binaryAvgSize) {
		this.binaryAvgSize = binaryAvgSize;
	}
}
