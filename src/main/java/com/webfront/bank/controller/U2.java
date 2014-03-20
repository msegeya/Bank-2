/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.bank.controller;

import asjava.uniobjects.UniJava;
import asjava.uniobjects.UniSession;
import asjava.uniobjects.UniSessionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rlittle
 */
public final class U2 {
    private String server;
    private String userName;
    private String password;
    private String account;
    private UniSession session;
    
    public U2() {
        setServer("localhost");
        setUserName("uvuser");
        setPassword("uvuser999");
        setAccount("UVUSERS");
        try {
            setSession(new UniJava().openSession());
            getSession().connect(getServer(), getUserName(), getPassword(), getAccount());
        } catch (UniSessionException ex) {
            Logger.getLogger(U2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setSession(UniSession s) {
    	this.session=s;
    }
    
    public UniSession getSession() {
    	return this.session;
    }
    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(String account) {
        this.account = account;
    }
}
