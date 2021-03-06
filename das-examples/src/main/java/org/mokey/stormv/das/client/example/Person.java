package org.mokey.stormv.das.client.example;

import java.sql.Timestamp;

public class Person {
	private Integer iD;
	private String address;
	private String telephone;
	private String name;
	private Integer age;
	private Integer gender;
	private Timestamp birth;
	private Integer partmentID;
	private String space;
	private byte[] test;
	
	public byte[] getTest() {
		return test;
	}
	public void setTest(byte[] test) {
		this.test = test;
	}
	public Integer getID() {
		return iD;
	}
	public void setID(Integer iD) {
		this.iD = iD;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Timestamp getBirth() {
		return birth;
	}
	public void setBirth(Timestamp birth) {
		this.birth = birth;
	}
	public Integer getPartmentID() {
		return partmentID;
	}
	public void setPartmentID(Integer partmentID) {
		this.partmentID = partmentID;
	}
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	
	
}
