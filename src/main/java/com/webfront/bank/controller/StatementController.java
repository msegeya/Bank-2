/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bank.controller;

import asjava.uniclientlibs.UniDynArray;
import asjava.uniclientlibs.UniException;
import asjava.uniclientlibs.UniString;
import asjava.uniclientlibs.UniStringException;
import asjava.uniobjects.UniSessionException;
import asjava.uniobjects.UniSubroutine;
import asjava.uniobjects.UniSubroutineException;
import com.webfront.bank.model.SearchCriteria;
import com.webfront.bank.model.SelectItem;
import com.webfront.bank.model.StatementEntry;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

/**
 *
 * @author rlittle
 */
public class StatementController implements Initializable {

    private final U2 u2;
    HashMap<String, String> paymentClassMap;
    public ObservableList<String> paymentCats;
    public static StatementController statementController;

    public StatementController() {
        u2 = new U2();
    }

    public LinkedList<StatementEntry> getLedger(SearchCriteria sc) throws UniException {
        LinkedList<StatementEntry> list = new LinkedList<>();
        try {
            UniSubroutine sub = getU2().getSession().subroutine("GET.BANK.LEDGER", 3);
            UniDynArray iList = new UniDynArray();
            UniDynArray oList = new UniDynArray();
            UniDynArray eList = new UniDynArray();
            if (sc.getStartDate() == null) {
                iList.replace(1, "");
            } else {
                iList.replace(1, sc.getStartDate());
            }
            if (sc.getEndDate() == null) {
                iList.replace(2, "");
            } else {
                iList.replace(2, sc.getEndDate());
            }            
            if (sc.getMinAmount() != null) {
                iList.replace(3, sc.getMinAmount());
            }
            if (sc.getMaxAmount() != null) {
                iList.replace(3, sc.getMaxAmount());
            }
            if (sc.getPaymentClass() != null) {
                String pc = sc.getPaymentClass();
                if (paymentClassMap.containsKey(pc)) {
                    iList.replace(5, paymentClassMap.get(sc.getPaymentClass()));
                }
            }
            if (sc.getSearchTarget() != null) {
                iList.replace(6, sc.getSearchTarget());
            }
            sub.setArg(0, iList);
            sub.setArg(1, oList);
            sub.setArg(2, eList);
            sub.call();
            eList = sub.getArgDynArray(2);
            String errStat = eList.extract(1).toString();
            if (errStat.equals("-1")) {
                String errMsg = eList.extract(3).toString();
                Integer errCode = Integer.getInteger(eList.extract(2).toString());
                System.out.println(errCode + ": " + errMsg);
                throw new UniException(errMsg, errCode);
            }
            oList = sub.getArgDynArray(1);
            int vals = oList.dcount(1);
            Float recsLoaded;
            Float recCount = (float) vals;
            Float floatVal = (float) 1;
            StatementEntry entry;
            for (int val = 1; val <= vals; val++) {
                recsLoaded = (floatVal / recCount) * 100;
                entry = new StatementEntry();
                entry.setId(oList.extract(1, val).toString());
                entry.setTransDate(oList.extract(2, val).toString());
                entry.setTransDescription(oList.extract(3, val).toString());
                entry.setPaymentClass(oList.extract(4, val).toString());

                entry.setTransAmount(Float.valueOf(oList.extract(5, val).toString()));
                entry.setTransBalance(Float.valueOf(oList.extract(6, val).toString()));
                String checkNumber = oList.extract(7, val).toString();
                if (checkNumber != null) {
                    entry.setCheckNumber(Integer.getInteger(oList.extract(7, val).toString()));
                    entry.setCheckNum(checkNumber);
                }
                entry.setQIFUpdate(!oList.extract(8, val).toString().equals("0"));
                entry.setClassDescription(oList.extract(9, val).toString());

                entry.setEntryId(oList.extract(1, val).toString());
                entry.setDate(oList.extract(2, val).toString());

                entry.setDesc(oList.extract(3, val).toString());
                entry.setPayClass(oList.extract(4, val).toString());
                entry.setAmt(Float.valueOf(oList.extract(5, val).toString()));
                entry.setBal(Float.valueOf(oList.extract(6, val).toString()));

                list.add(entry);
                floatVal += 1;
            }

        } catch (UniSessionException | UniSubroutineException e) {
        }
        return list;
    }

    public ArrayList<SelectItem> getPaymentClassList() {
        ArrayList<SelectItem> items = new ArrayList<>();
        paymentClassMap = new HashMap<>();
        paymentCats = FXCollections.observableArrayList();
        try {
            UniSubroutine sub = getU2().getSession().subroutine("GET.PAYMENT.CLASSES", 3);
            UniDynArray iList = new UniDynArray();
            UniDynArray oList = new UniDynArray();
            UniDynArray eList = new UniDynArray();
            sub.setArg(0, iList);
            sub.setArg(1, oList);
            sub.setArg(2, eList);
            sub.call();
            oList = sub.getArgDynArray(1);
            eList = sub.getArgDynArray(2);
            if (eList.extract(1).toString().equals("-1")) {
                String errCode = eList.extract(2).toString();
                String errMsg = eList.extract(3).toString();
                System.out.println("Error code " + errCode + ": " + errMsg);
            } else {
                int vals = oList.dcount(1);
                for (int val = 1; val <= vals; val++) {
                    String key = oList.extract(1, val).toString();
                    String value = oList.extract(2, val).toString();
                    items.add(new SelectItem(key, value));
                    paymentClassMap.put(value, key);
                    paymentCats.add(value);
                }
            }
        } catch (UniSessionException | UniSubroutineException ex) {
            System.out.println(ex.getMessage());
        }
        return items;
    }

    void doImport() {
        try {
            UniSubroutine sub = getU2().getSession().subroutine("GET.UNPROCESSED.STATEMENTS", 3);
            UniDynArray iList = new UniDynArray();
            UniDynArray oList = new UniDynArray();
            UniDynArray eList = new UniDynArray();
            sub.setArg(0, iList);
            sub.setArg(1, oList);
            sub.setArg(2, eList);
            sub.call();
            oList = sub.getArgDynArray(1);
            eList = sub.getArgDynArray(2);
            String errStat = eList.extract(1).toString();
            String errCode = eList.extract(2).toString();
            String errMsg = eList.extract(3).toString();
            if (errStat.equals("-1")) {
                System.out.println("Error from doImport(): (" + errCode + ") " + errMsg);
            } else {
                int vals = oList.dcount(1);
                for (int val = 1; val <= vals; val++) {
                    sub = getU2().getSession().subroutine("PROCESS.STATEMENT", 3);
                    iList.replace(1, oList.extract(1, val));
                    eList = new UniDynArray();
                    sub.setArg(0, iList);
                    sub.setArg(1, oList);
                    sub.setArg(2, eList);
                    sub.call();
                    eList = sub.getArgDynArray(2);
                    errStat = eList.extract(1).toString();
                    errCode = eList.extract(2).toString();
                    errMsg = eList.extract(3).toString();
                    if (errStat.equals("-1")) {
                        System.out.println("Error from doImport(): (" + errCode + ") " + errMsg);
                    }
                }
            }
        } catch (UniSessionException | UniSubroutineException ex) {

        }
    }

    public void doSave(StatementEntry item) {
        try {
            UniSubroutine sub = getU2().getSession().subroutine("SET.FILE.REC", 3);
            UniDynArray iList = new UniDynArray();
            UniDynArray oList = new UniDynArray();
            UniDynArray eList = new UniDynArray();

            UniString transDate = new UniString(item.getTransDate());
            UniString amt = new UniString(Float.toString(item.getAmt()));
            UniString bal = new UniString(Float.toString(item.getBal()));
            try {
                transDate = getU2().getSession().iconv(transDate, "D");
                amt = getU2().getSession().iconv(amt, "MR2");
                bal = getU2().getSession().iconv(bal, "MR2");
            } catch (UniStringException ex) {
                Logger.getLogger(BankController.class.getName()).log(Level.SEVERE, null, ex);
            }

            iList.replace(1, "STATEMENTS");
            iList.replace(2, item.getId());
            iList.replace(3, 1, transDate.toString());
            iList.replace(3, 2, amt);
            iList.replace(3, 3, item.getDesc().getValue());
            iList.replace(3, 4, bal);
            iList.replace(3, 5, item.getPaymentClass());
            String checkNum = new String();
            if (item.getCheckNumber() != null) {
                checkNum = item.getCheckNumber().toString();
            }
            iList.replace(3, 7, checkNum);
            iList.replace(3, 8, item.getQIFUpdate() ? "1" : "0");
            for (String k : item.getSplitPayment().keySet()) {
                String v = item.getSplitPayment().get(k);
                iList.replace(3, 9, Integer.parseInt(k), k);
                iList.replace(3, 9, Integer.parseInt(k), v);
            }
            sub.setArg(0, iList);
            sub.setArg(1, oList);
            sub.setArg(2, eList);
            sub.call();
            eList = sub.getArgDynArray(2);
            String errStat = eList.extract(1).toString();
            String errCode = eList.extract(2).toString();
            String errMsg = eList.extract(3).toString();
            if (errStat.equals("-1")) {
                System.out.println("Error from doImport(): (" + errCode + ") " + errMsg);
            }
        } catch (UniSessionException | UniSubroutineException ex) {
            System.out.println(ex.getExtendedMessage());
        }
    }

    public void doExport() {
        try {
            UniSubroutine sub = getU2().getSession().subroutine("CREATE.QIF", 3);
            UniDynArray iList = new UniDynArray();
            UniDynArray oList = new UniDynArray();
            UniDynArray eList = new UniDynArray();
            sub.setArg(0, iList);
            sub.setArg(1, oList);
            sub.setArg(2, eList);
            sub.call();
        } catch (UniSessionException | UniSubroutineException ex) {

        }
    }

    /**
     *
     * @return
     */
    public U2 getU2() {
        return this.u2;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("StatementController.initialize()");
        statementController = this;
    }
    
}
