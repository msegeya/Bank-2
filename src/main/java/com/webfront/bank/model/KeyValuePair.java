package com.webfront.bank.model;

public class KeyValuePair {
	public String key;
	public String value;
	public KeyValuePair(String k, String v) {
		this.key=k;
		this.value=v;
	}
	public String toString() {
		return this.value;
	}
	public String getKey() {
		return this.key;
	}
	public String getValue() {
		return this.value;
	}
}
