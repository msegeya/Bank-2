/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bank.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import asjava.uniclientlibs.UniDynArray;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author rlittle
 */
public class StatementEntry implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3724652318892755712L;
    private String id;
    private String transDate;
    private Float transAmount;
    private String transDescription;
    private Float transBalance;
    private String paymentClass;
    private Integer checkNumber;
    private Boolean QIFUpdate;
    private LinkedHashMap<String, String> splitPayment;
    private String classDescription;
    
    private final SimpleStringProperty entryId;
    private final SimpleStringProperty date;
    private final SimpleStringProperty checkNum;
    private final SimpleStringProperty desc;
    private final SimpleStringProperty payClass;
    private final SimpleStringProperty classDesc;
    private final SimpleFloatProperty amt;
    private final SimpleFloatProperty bal;
    
    public StatementEntry() {
        id = new String();
        transDate = new String();
        transAmount = new Float(0);
        transDescription = new String();
        transBalance = new Float(0);
        paymentClass = new String();
        checkNumber = new Integer(00000);
        QIFUpdate = false;
        splitPayment = new LinkedHashMap<>();
        classDescription = new String();
        
        entryId=new SimpleStringProperty();
        date=new SimpleStringProperty();
        checkNum=new SimpleStringProperty();
        desc=new SimpleStringProperty();
        payClass=new SimpleStringProperty();
        classDesc=new SimpleStringProperty();
        amt=new SimpleFloatProperty();
        bal=new SimpleFloatProperty();
    }

    public Object get(int attr, int val) {
        switch (attr) {
            case 1:
                return id;
            case 2:
                return transDate;
            case 3:
                return transDescription;
            case 4:
                return paymentClass;
            case 5:
                return transAmount;
            case 6:
                return transBalance;
            case 7:
                if (null != checkNumber) {
                    return checkNumber;
                }
                return new String();
            case 8:
                return QIFUpdate;
            case 9:
                return classDescription;
        }
        return "";
    }

    @Override
    public String toString() {
        UniDynArray uda = new UniDynArray();
        uda.replace(1, getTransDate());
        uda.replace(2, getTransDescription());
        uda.replace(3, getPaymentClass());
        uda.replace(4, getTransAmount());
        uda.replace(5, getTransBalance());

        if (null != getCheckNumber()) {
            uda.replace(6, getCheckNumber());
        }
        uda.replace(7, getQIFUpdate());
        Set<String> keys = splitPayment.keySet();
        Iterator<String> iterator = keys.iterator();
        int val = 1;
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = splitPayment.get(key);
            uda.replace(8, val, key);
            uda.replace(8, val, value);
            val++;
        }
        uda.replace(9, getClassDescription());
        return uda.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UniDynArray toUniDynArray() {
        return new UniDynArray(toString());
    }

    /**
     * @return the transDate
     */
    public String getTransDate() {
        return transDate;
    }

    /**
     * @param transDate the transDate to set
     */
    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    /**
     * @return the transAmount
     */
    public Float getTransAmount() {

        return transAmount;
    }

    /**
     * @param transAmount the transAmount to set
     */
    public void setTransAmount(Float transAmount) {
        this.transAmount = transAmount;
    }

    /**
     * @return the transDescription
     */
    public String getTransDescription() {
        return transDescription;
    }

    /**
     * @param transDescription the transDescription to set
     */
    public void setTransDescription(String transDescription) {
        this.transDescription = transDescription;
    }

    /**
     * @return the transBalance
     */
    public Float getTransBalance() {
        return transBalance;
    }

    /**
     * @param transBalance the transBalance to set
     */
    public void setTransBalance(Float transBalance) {
        this.transBalance = transBalance;
    }

    /**
     * @return the paymentClass
     */
    public String getPaymentClass() {
        return paymentClass;
    }

    /**
     * @param paymentClass the paymentClass to set
     */
    public void setPaymentClass(String paymentClass) {
        this.paymentClass = paymentClass;
    }

    /**
     * @return the checkNumber
     */
    public Integer getCheckNumber() {
        return checkNumber;
    }

    /**
     * @param checkNumber the checkNumber to set
     */
    public void setCheckNumber(Integer checkNumber) {
        this.checkNumber = checkNumber;
    }

    /**
     * @return the QIFUpdate
     */
    public Boolean getQIFUpdate() {
        return QIFUpdate;
    }

    /**
     * @param QIFUpdate the QIFUpdate to set
     */
    public void setQIFUpdate(Boolean QIFUpdate) {
        this.QIFUpdate = QIFUpdate;
    }

    /**
     * @return the splitPayment
     */
    public LinkedHashMap<String, String> getSplitPayment() {
        return splitPayment;
    }

    /**
     * @param splitPayment the splitPayment to set
     */
    public void setSplitPayment(LinkedHashMap<String, String> splitPayment) {
        this.splitPayment = splitPayment;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    /**
     * @return the entryId
     */
    public String getEntryId() {
        return entryId.get();
    }

    /**
     * @param entryId the entryId to set
     */
    public void setEntryId(String entryId) {
        this.entryId.set(entryId);
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date.get();
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date.set(date);
    }

    /**
     * @return the desc
     */
    public SimpleStringProperty getDesc() {
        return desc;
    }
    
//    public String getDesc() {
//        return desc.get();
//    }

    /**
     * @param d
     */
    public void setDesc(SimpleStringProperty d) {
        this.desc.setValue(d.getValue());
    }

    /**
     * @return the payClass
     */
    public SimpleStringProperty getPayClass() {
        return payClass;
    }

    /**
     * @param payClass the payClass to set
     */
    public void setPayClass(String payClass) {
        this.payClass.set(payClass);
    }

    /**
     * @return the classDesc
     */
    public String getClassDesc() {
        return classDesc.get();
    }

    /**
     * @param classDesc the classDesc to set
     */
    public void setClassDesc(String classDesc) {
        this.classDesc.set(classDesc);
    }

    /**
     * @return the amt
     */
    public Float getAmt() {
        return amt.get();
    }

    /**
     * @param amt the amt to set
     */
    public void setAmt(Float amt) {
        this.amt.set(amt);
    }

    /**
     * @return the bal
     */
    public Float getBal() {
        return bal.get();
    }

    /**
     * @param bal the bal to set
     */
    public void setBal(Float bal) {
        this.bal.set(bal);
    }

    public void setCheckNum(String chkNum) {
        if(chkNum != null) {
            this.checkNum.set(chkNum);
        }
    }
    /**
     * @return the checkNum
     */
    public String getCheckNum() {
        return checkNum.get();
    }

    public void setDesc(String newValue) {
        this.desc.set(newValue);
    }
}
