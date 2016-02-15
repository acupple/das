package org.mokey.stormv.das.lpt.connectors;

public class ConnectorFactory {
	public static IConnector getConnector(ConnectorType type) {
		switch (type) {
		case JDBC:
			return new JDBCConnector();
		case JDBCBIG:
			return new JDBCMysqlBigConnector();
		case DAS:
			return new DasConnector();
		case AMEOBA:
			return new AmeobaConnector();
		case DASWITHCOMPRESS:
			return new DasWithCompressConnector();
		default:
			return null;
		}
	}
}
