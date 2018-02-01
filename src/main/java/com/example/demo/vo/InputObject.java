
package com.example.demo.vo;

public class InputObject {
	String data;
	String id;
	String index;
	boolean isMalformed=false;
	public boolean isMalformed() {
		return isMalformed;
	}
	public void setMalformed(boolean isMalformed) {
		this.isMalformed = isMalformed;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public InputObject(String index, String _id, String data2,boolean isvalid) {
		this.data=data2;
		this.id=_id;
		this.index=index;
		this.isMalformed=isvalid;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

}