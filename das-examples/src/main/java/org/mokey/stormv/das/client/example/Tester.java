package org.mokey.stormv.das.client.example;

import org.mokey.stormv.das.client.AutoRecordMapper;
import org.mokey.stormv.das.client.Connection;
import org.mokey.stormv.das.client.DalClient;
import org.mokey.stormv.das.client.RecordMapper;
import org.mokey.stormv.das.models.DalModels;

public class Tester extends Thread {
	private String host;
	private String logicDbName;
	private int port;
	private String sql;
	private volatile boolean stop;
	private long duration;
	private long count;
	private DalModels.DalHints hints = DalModels.DalHints.getDefaultInstance();

	public Tester(String host, String logicDbName, int port, String sql) {
		this.host = host;
		this.logicDbName = logicDbName;
		this.port = port;
		this.sql = sql;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Connection connection = null;
		try {
			connection = new Connection(host, port);
			connection.connect();
			DalClient client = new DalClient(connection, logicDbName);
			RecordMapper<TestBig> mapper = AutoRecordMapper
					.create(TestBig.class);
			long start = System.currentTimeMillis();
			while (!stop) {
				client.query(sql, 1000, 20000, mapper, hints);
				count++;
			}
			duration = System.currentTimeMillis() - start;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(connection != null){
				connection.close();
			}
		}
	}

	public void stopTest() {
		stop = true;
	}

	public long getCount() {
		return this.count;
	}

	public long getDuration() {
		return this.duration;
	}
}
