package org.mokey.stormv.das.client.example;

import java.sql.Timestamp;

public class TestBig {
	private Integer id;
	private Integer quantity;
	private Short type;
	private String address;
	private Timestamp last_changed;
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
	
}
