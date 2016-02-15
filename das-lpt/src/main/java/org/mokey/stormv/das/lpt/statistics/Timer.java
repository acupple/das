package org.mokey.stormv.das.lpt.statistics;

import java.util.concurrent.atomic.AtomicLong;

public class Timer {
	private AtomicLong time = new AtomicLong();
	private long start = 0;

	public void start() {
		this.start = System.currentTimeMillis();
	}

	public void end() {
		this.time.addAndGet(System.currentTimeMillis() - this.start);
		this.start = 0;
	}

	public void add(long time){
		this.time.addAndGet(time);
	}
	
	public long getTime() {
		return this.time.get();
	}

	public float getAvg(int count) {
		return count == 0 ? 0 : (this.getTime() + 0.0f) / count;
	}
}
