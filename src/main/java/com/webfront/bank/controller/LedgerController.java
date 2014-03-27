/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bank.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public class LedgerController {

    public Connection connection;
    public DriverManager manager;
    public String viewQuery;
    private ResultSet results;

    public LedgerController() {
        viewQuery = "select transDate,transDesc,primaryCat,subCat,amount,balance ";
        //viewQuery+= "from ledger_view where transDate > :transDate;";
        viewQuery+= "from ledger_view;";
        results = null;
    }

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://mustang/bank?user=rlittle");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(LedgerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ResultSet getResults() {
        try {
            Statement stmt = connection.createStatement(); 
            results = stmt.executeQuery(viewQuery);
        } catch (SQLException ex) {
            Logger.getLogger(LedgerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
    }
}
