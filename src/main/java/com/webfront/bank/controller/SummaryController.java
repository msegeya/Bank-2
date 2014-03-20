/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bank.controller;

import asjava.uniclientlibs.UniDynArray;
import asjava.uniobjects.UniSessionException;
import asjava.uniobjects.UniSubroutine;
import asjava.uniobjects.UniSubroutineException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 *
 * @author rlittle
 */
public class SummaryController implements Initializable {

    private U2 u2;

    @FXML
    Label balance;
    @FXML
    Label transCount;
    @FXML
    Label startDate;
    @FXML
    Label endDate;
    @FXML
    Label startBalance;
    @FXML
    Label deposits;
    @FXML
    Label withdrawals;
    @FXML
    Label noPayCl;
    @FXML
    Label unprocessedStatus;

    @FXML
    ListView listView;
    ObservableList<String> unprocessedList;

    public static SummaryController sc;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sc = this;
        u2 = new U2();
        unprocessedList = FXCollections.observableArrayList();
        
        try {
            UniSubroutine sub = u2.getSession().subroutine("GET.ACCOUNT.SUMMARY", 3);
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
                String msg = "SummaryController.initialize() error: " + errCode + " " + errMsg;
                Logger.getLogger(SummaryController.class.getName()).log(Level.SEVERE, null, msg);
            } else {
                this.balance.setText(oList.extract(1).toString());
                this.deposits.setText(oList.extract(2).toString());
                this.withdrawals.setText(oList.extract(3).toString());
                this.noPayCl.setText(oList.extract(5).toString());
                this.transCount.setText(oList.extract(6).toString());
                this.startDate.setText(oList.extract(7).toString());
                this.endDate.setText(oList.extract(8).toString());
                this.startBalance.setText(oList.extract(9).toString());
                UniDynArray list = oList.extract(4);
                int vals = list.dcount(1);
                if (vals == 0) {
                    unprocessedStatus.setText("No unprocessed statements");
                    listView.setVisible(false);
                } else {
                    for (int val = 1; val <= vals; val++) {
                        unprocessedList.add(list.extract(1, val).toString());
                        listView.setItems(unprocessedList);
                    }
                }
            }
        } catch (UniSessionException | UniSubroutineException ex) {
            Logger.getLogger(SummaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        balance.setText("7245.36");
    }

}
