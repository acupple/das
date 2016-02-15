package org.mokey.stormv.das.lpt.connectors;

import org.mokey.stormv.das.client.Connection;
import org.mokey.stormv.das.client.DalClient;
import org.mokey.stormv.das.client.RecordMapper;
import org.mokey.stormv.das.models.DalModels;
import org.mokey.stormv.das.lpt.models.Ameoba;
import org.mokey.stormv.das.lpt.models.AutoRecordMapper;
import org.mokey.stormv.das.lpt.models.DasFeature;
import org.mokey.stormv.das.lpt.statistics.StatisticsLogger;

import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class DasConnector extends Connector{
	private static ConnectorType type = ConnectorType.DAS;
	private static Connection connection;
	private static RecordMapper<Ameoba> mapper;
	private static List<DalModels.ColumnMata> headers = new LinkedList<DalModels.ColumnMata>();
	private static DalClient client;
	
	static{
		try {
			mapper = AutoRecordMapper.create(Ameoba.class);
			headers.add(DalModels.ColumnMata.newBuilder().setName("id").setType(Types.INTEGER).build());
			headers.add(DalModels.ColumnMata.newBuilder().setName("name").setType(Types.VARCHAR).build());
			headers.add(DalModels.ColumnMata.newBuilder().setName("content").setType(Types.VARBINARY).build());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init();
	}
	
	public static void init(){
		try {
			connection = new Connection(DasFeature.get().getHost(), DasFeature.get().getPort());
			connection.connect();
			client = new DalClient(connection, type.getDbName());
			client.setCompressTransport(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public ConnectorType process() throws Exception {
		try{
			String sql = String.format(type.getSqlPattern(), DasFeature.get().getResultCount());
			this.models.addAll(client.query(sql,
					DasFeature.get().getQuerySize(), DasFeature.get().getFetchSize(),
					headers, mapper, DalModels.DalHints.getDefaultInstance()));
			StatisticsLogger.getStatistics(type).getRecordCount().addAndGet(this.models.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		return type;
	}

}
