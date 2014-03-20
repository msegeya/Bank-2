/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bank.model;

/**
 *
 * @author rlittle
 */
public class SelectItem {
    private String key;
    private String value;
    public SelectItem() {
    }
    public SelectItem(String k, String v) {
        this.key=k;
        this.value=v;
    }
    public String getKey() {
        return this.key;
    }
    public String getValue() {
        return this.value;
    }
    public void setKey(String k) {
        this.key=k;
    }
    public void setValue(String v) {
        this.value=v;
    }
    
}
