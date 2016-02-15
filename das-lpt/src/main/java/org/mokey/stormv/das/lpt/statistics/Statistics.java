package org.mokey.stormv.das.lpt.statistics;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
	private AtomicInteger requestCount = new AtomicInteger();
	private AtomicInteger binarySize = new AtomicInteger();
	private AtomicInteger recordCount = new AtomicInteger();
	
	private Timer allTime = new Timer();
	private Timer serviceHandleTime = new Timer();
	private Timer decodeTime = new Timer();
	private Timer netWorkTime = new Timer();
	private Timer createClient = new Timer();
	
	public void increment(){
		this.requestCount.incrementAndGet();
	}

	public Timer getAllTime() {
		return allTime;
	}

	public AtomicInteger getBinarySize() {
		return binarySize;
	}

	public int getRequestCount() {
		return requestCount.get();
	}

	public Timer getServiceHandleTime() {
		return serviceHandleTime;
	}

	public Timer getDecodeTime() {
		return decodeTime;
	}

	public AtomicInteger getRecordCount() {
		return recordCount;
	}

	public Timer getNetWorkTime() {
		return netWorkTime;
	}
	
	public Timer getCreateClient() {
		return createClient;
	}

	public float getRecordAvgCount(){
		int count = this.getRequestCount();
		if(count == 0)
			return 0;
		return (this.getRecordCount().get() + 0.0f) / count;
	}
}
