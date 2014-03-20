package com.webfront.bank.model;

import java.util.HashMap;
import java.util.Vector;

import asjava.uniclientlibs.UniDynArray;
import asjava.uniobjects.UniSessionException;
import asjava.uniobjects.UniSubroutine;
import asjava.uniobjects.UniSubroutineException;

import com.webfront.bank.controller.StatementController;

public class CategoryModel extends Vector<KeyValuePair> {

    /**
     *
     */
    private static final long serialVersionUID = 2214572886239339036L;
    StatementController controller;
    private HashMap<String, String> catXref;

    public CategoryModel(StatementController controller) {
        super();
        this.controller = controller;
        this.catXref=new HashMap<String, String>();
        setList();
    }

    private void setList() {
        try {
            UniSubroutine sub = controller.getU2().getSession().subroutine("GET.PAYMENT.CLASSES", 3);
            UniDynArray iList = new UniDynArray();
            UniDynArray oList = new UniDynArray();
            UniDynArray eList = new UniDynArray();
            iList.replace(1, "PAYMENT.CLASS");
            iList.replace(2, "CLASS.NAME");
            sub.setArg(0, iList);
            sub.setArg(1, oList);
            sub.setArg(2, eList);
            sub.call();
            oList = sub.getArgDynArray(1);
            eList = sub.getArgDynArray(2);
            if (eList.extract(1).toString().equals("-1")) {

            } else {
                int vals = oList.dcount(1);
                String key;
                String value;
                for (int val = 1; val <= vals; val++) {
                    key = oList.extract(1, val).toString();
                    value = oList.extract(2, val).toString();
                    this.add(new KeyValuePair(key, value));
                    getCatXref().put(value, key);
                }
            }
        } catch (UniSessionException | UniSubroutineException e) {
        }
    }

    public HashMap<String, String> getCatXref() {
        return catXref;
    }

    public void setCatXref(HashMap<String, String> catXref) {
        this.catXref = catXref;
    }
}
