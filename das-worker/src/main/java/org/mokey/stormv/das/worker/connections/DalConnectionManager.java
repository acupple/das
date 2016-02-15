package org.mokey.stormv.das.worker.connections;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mokey.stormv.das.worker.utils.DataSourceUtil;
import org.mokey.stormv.das.models.DalModels.Transaction;

public class DalConnectionManager {
	private static DalConnectionManager manager = null;

	private Map<String, DalConnection> connCache = new ConcurrentHashMap<String, DalConnection>();

	private DalConnectionManager() {
	}

	public static DalConnectionManager instance() {
		if (manager == null)
			manager = new DalConnectionManager();
		return manager;
	}

	public DalConnection getDalConnection(String id) {
		return this.connCache.get(id);
	}

	public DalConnection getDalConnection(Transaction transaction,
			String allInOneKey) throws Exception {
		DalConnection conn;
		if (transaction.isInitialized())
			conn = this.getDalConnection(transaction.getTransactionId());
		else {
			conn = this.newDalConnection(allInOneKey);
		}
		return conn;
	}

	public void remove(String id) {
		this.connCache.remove(id);
	}

	public DalConnection newDalConnection(String allInOneKey) throws Exception {
		Connection conn = DataSourceUtil.getDataSource(allInOneKey)
				.getConnection();
		DalConnection dalConn = new DalConnection(conn, allInOneKey);
		this.connCache.put(dalConn.getUuid(), dalConn);
		return dalConn;
	}
}
