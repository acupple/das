package org.mokey.stormv.das.worker.connections;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.mokey.stormv.das.worker.wraps.DalHintsWrapper;
import org.mokey.stormv.das.models.DalModels.DalHintEnum;

public class DalConnection {
	private String uuid;
	private Connection conn;

	private Integer oldIsolationLevel;
	private Integer newIsolationLevel;
	
	private int level = 0;
	private boolean rolledBack = false;
	private boolean completed = false;
	
	public DalConnection(Connection conn, String allInOnKey){
		this.conn = conn;
		this.uuid = UUID.randomUUID().toString();
	}
	
	public void applyHints(DalHintsWrapper hints) throws SQLException {
		if(!hints.is(DalHintEnum.isolationLevel))
			return;
		Integer level = hints.getInt(DalHintEnum.isolationLevel);
		
		if(oldIsolationLevel.equals(level))
			return;
		
		newIsolationLevel = level;
		conn.setTransactionIsolation(level);
	}

	public Connection getConn(){
		return this.conn;
	}
	
	public String getUuid(){
		return this.uuid;
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		conn.setAutoCommit(autoCommit);
	}

	public int getLevel() {
		return level;
	}

	public int startTransaction() throws Exception {
		setAutoCommit(false);
		if(rolledBack || completed)
			throw new Exception("Start transaction failed.");
		return level++;
	}
	
	public void endTransaction(int startLevel) throws Exception {
		if(rolledBack || completed)
			throw new Exception("Start transaction failed.");

		if(startLevel != (level - 1)) {
			rollbackTransaction();
			throw new Exception("Start transaction failed.");
		}
		
		if(--level == 0) {
			completed = true;
			cleanup(true);
		}
	}
	
	public void rollbackTransaction() throws Exception {
		if(rolledBack)
			return;

		rolledBack = true;
		cleanup(false);
	}
	
	private void cleanup(boolean commit) throws Exception {
		try {
			if(commit)
				conn.commit();
			else
				conn.rollback();
			conn.setAutoCommit(true);
			conn.close();
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}
	
	public void close(){
		try {
			if(newIsolationLevel != null)
				conn.setTransactionIsolation(oldIsolationLevel);
			if(this.conn != null)
				this.conn.close();
			DalConnectionManager.instance().remove(this.getUuid());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
