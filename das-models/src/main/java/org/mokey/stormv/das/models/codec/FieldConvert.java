package org.mokey.stormv.das.models.codec;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.mokey.stormv.das.models.DalModels.AvailableType;
import com.google.protobuf.ByteString;

public class FieldConvert {
	public static ByteString getBinary(ResultSet rs, int columnIndex, int dbType)
			throws SQLException {
		byte[] bytes = rs.getBytes(columnIndex);
		if(bytes == null){
			return ByteString.EMPTY;
		}
		return ByteString.copyFrom(bytes);
	}
	
	public static AvailableType getField(ResultSet rs, int columnIndex, int dbType) throws SQLException{
		AvailableType.Builder field = AvailableType.newBuilder().setIsNull(false);
		
		switch (dbType) {
		case Types.INTEGER:
			return field.setInt32Arg(rs.getInt(columnIndex)).build();
		case Types.SMALLINT:
			return field.setInt32Arg(rs.getShort(columnIndex)).build();
		case Types.BIGINT:
			return field.setInt64Arg(rs.getLong(columnIndex)).build();
		case Types.VARCHAR:
			String str = rs.getString(columnIndex);
			if(str == null){
				return field.setIsNull(true).build();
			}else{
				return field.setStringArg(str).build();
			}
		case Types.DECIMAL:
			BigDecimal deci = rs.getBigDecimal(columnIndex);
			if(deci == null)
				return field.setIsNull(true).build();
			else
				return field.setStringArg(deci.toString()).build();
		case Types.TIMESTAMP:
			Timestamp tims = rs.getTimestamp(columnIndex);
			if(null == tims){
				return field.setIsNull(true).build();
			}else{
				return field.setInt64Arg(tims.getTime()).build();
			}
		case Types.BLOB:
		case Types.LONGVARBINARY:
		case Types.BINARY:
		case Types.VARBINARY:
			byte[] bytes = rs.getBytes(columnIndex);
			if(null == bytes){
				return field.setIsNull(true).build();
			}else{
				return field.setBytesArg(ByteString.copyFrom(bytes)).build();
			}
		default:
			throw new SQLException(String.format(
					"DB Type[%s] can't be matched", dbType));
		}
	}
	
	public static Object resolveField(AvailableType field, int dbType) throws SQLException{
		if(field == null || field.getIsNull())
			return null;
		switch (dbType) {
		case Types.INTEGER:
			return field.getInt32Arg();
		case Types.BIGINT:
			return field.getInt64Arg();
		case Types.SMALLINT:
			return ((short)field.getInt32Arg());
		case Types.VARCHAR:
			return field.getStringArg();
		case Types.DECIMAL:
			return new BigDecimal(field.getStringArg());
		case Types.TIMESTAMP:
			return new Timestamp(field.getInt64Arg());
		case Types.BLOB:
		case Types.LONGVARBINARY:
		case Types.BINARY:
		case Types.VARBINARY:
			return field.toByteArray();
		default:
			throw new SQLException(String.format(
					"DB Type[%s] can't be matched", dbType));
		}
	}
	
	public static Object resolveBinary(ByteString field, int dbType) throws SQLException {
		if(field == null || field.isEmpty())
			return null;
		switch (dbType) {
		case Types.INTEGER:
			return getInt(field);
		case Types.BIGINT:
			return getLong(field);
		case Types.SMALLINT:
			return getShort(field);
		case Types.VARCHAR:
			return field.toStringUtf8();
		case Types.DECIMAL:
			return new BigDecimal(field.toStringUtf8());
		case Types.TIMESTAMP:
			return Timestamp.valueOf(field.toStringUtf8());
		case Types.BLOB:
		case Types.LONGVARBINARY:
		case Types.BINARY:
		case Types.VARBINARY:
			return field.toByteArray();
		default:
			throw new SQLException(String.format(
					"DB Type[%s] can't be matched", dbType));
		}
	}
	
	private static Integer getInt(ByteString field) {
		byte[] b = field.toByteArray();
		if (b.length == 4)
			return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
					| (b[3] & 0xff);
		else if (b.length == 2)
			return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);

		return 0;
	}

	private static Short getShort(ByteString field) {
		byte[] b = field.toByteArray();
		if (b.length == 2)
			return (short) ((b[0] & 0xf) << 8 | (b[1] & 0xf));
		return 0;
	}

	private static Long getLong(ByteString field) {
		byte[] b = field.toByteArray();
		if (b.length == 8)
			return ((b[0] & 0xFFL) << 56) | ((b[1] & 0xFFL) << 48)
					| ((b[2] & 0xFFL) << 40) | ((b[3] & 0xFFL) << 32)
					| ((b[4] & 0xFFL) << 24) | ((b[5] & 0xFFL) << 16)
					| ((b[6] & 0xFFL) << 8) | ((b[7] & 0xFFL) << 0);
		if (b.length == 4)
			return (long) getInt(field);
		if (b.length == 2)
			return (long) getShort(field);
		return 0l;
	}
}
