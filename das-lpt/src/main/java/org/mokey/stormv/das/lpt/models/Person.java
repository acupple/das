package org.mokey.stormv.das.lpt.models;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Person extends Model {
	private Integer id;
	private Integer quantity;
	private Short type;
	private String address;
	private Timestamp last_changed;

	@Override
	public int size() {
		return 10 + (address == null ? 0 : address.getBytes().length) + 8;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Short getType() {
		return type;
	}
	public void setType(Short type) {
		this.type = type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Timestamp getLast_changed() {
		return last_changed;
	}
	public void setLast_changed(Timestamp last_changed) {
		this.last_changed = last_changed;
	}
	
	private static Map<String, Method> seters = new HashMap<String, Method>();

	public static Person parseFromJDBC(ResultSet rs) throws Exception{
		Person pojo = new Person();	
		pojo.setId((Integer)rs.getObject("Id"));
		pojo.setQuantity((Integer)rs.getObject("quantity"));
		pojo.setType((Short)rs.getObject("type"));
		pojo.setAddress((String)rs.getObject("address"));
		pojo.setLast_changed((Timestamp)rs.getObject("last_changed"));
		return pojo;
	}
}
