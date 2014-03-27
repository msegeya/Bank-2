/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bank.controller;

import asjava.uniclientlibs.UniStringException;
import com.webfront.bank.MainApp;
import com.webfront.bank.model.SearchCriteria;
import com.webfront.bank.model.SelectItem;
import com.webfront.bank.model.StatementEntry;
import com.webfront.bank.view.SearchWindow;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

/**
 *
 * @author rlittle
 */
public class BankController implements Initializable {

    /**
     * @return the bankController
     */
    public static BankController getBankController() {
        return bankController;
    }

    private IntegerProperty pctLoaded = new SimpleIntegerProperty(0);
    @FXML
    ObservableList<StatementEntry> ledgerList;
    @FXML
    ObservableList<SelectItem> paymentClassList;
    @FXML
    ObservableList<String> paymentCats;
    @FXML
    TableView<StatementEntry> tableView;
    @FXML
    TableColumn<StatementEntry, String> dateColumn;
    @FXML
    TableColumn<StatementEntry, String> descriptionColumn;
    @FXML
    TableColumn<StatementEntry, String> paymentClassColumn;
    @FXML
    TableColumn<SelectItem, String> classDescriptionColumn;
    @FXML
    TableColumn<StatementEntry, Float> amountColumn;
    @FXML
    TableColumn<StatementEntry, Float> balanceColumn;
    @FXML
    TableColumn<StatementEntry, String> checkColumn;

    @FXML
    ComboBox paymentClasses;
    @FXML
    ProgressBar progressBar;

    @FXML
    AnchorPane anchorPane;

    @FXML
    Button searchButton;

    @FXML
    Button resetButton;

    @FXML
    SummaryController summaryController;

    private U2 u2;
    public static MainApp app;
    HashMap<String, String> paymentClassMap;
    private String startDate;
    private String endDate;
    Task<ObservableList<StatementEntry>> getLedgerTask;
    private static StatementController statementController;
    private static BankController bankController;

    private final WindowEventHandler<WindowEvent> windowEventHandler;
    private SearchCriteria criteria;

    public BankController() {
        statementController = new StatementController();
        u2 = statementController.getU2();
        paymentClasses = new ComboBox();
        paymentCats = FXCollections.observableArrayList();
        startDate = new String();
        endDate = new String();
        ledgerList = FXCollections.observableArrayList();
        windowEventHandler = new WindowEventHandler<>();
        criteria = new SearchCriteria();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bankController = this;
        app = MainApp.app;
        summaryController = SummaryController.sc;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
        endDate = df.format(cal.getTime());
        this.pctLoaded.set(0);
        criteria.setEndDate(endDate);

        try {
            String str = getU2().getSession().iconv(endDate, "D").toString();
            int edate = Integer.parseInt(str);
            int sdate = edate - 60;
            startDate = getU2().getSession().oconv(Integer.toString(sdate), "D2/").toString();
            criteria.setStartDate(startDate);
            getLedgerTask = new Task<ObservableList<StatementEntry>>() {
                @Override
                protected ObservableList call() throws Exception {
                    return FXCollections.observableArrayList(getStatementController().getLedger(criteria));
                }
            };
            getLedgerTask.setOnSucceeded(new EventHandler() {
                @Override
                public void handle(Event t) {
                    setLedgerList(getLedgerTask.getValue());
                    tableView.getItems().addAll(ledgerList);
                    progressBar.setProgress(0);
                    progressBar.setVisible(false);
                }
            });

            this.paymentClassList = FXCollections.observableArrayList(getPaymentClassList());
            List<String> list = new ArrayList<>();
            for (SelectItem item : paymentClassList) {
                String i = item.getValue();
                list.add(i);
            }
            boolean addAll;
            addAll = paymentClasses.getItems().addAll(list);
            paymentClasses.getSelectionModel().selectedItemProperty().addListener(new ChangeHandler<String>());
        } catch (UniStringException ex) {
            Logger.getLogger(BankController.class.getName()).log(Level.SEVERE, null, ex);
        }

        dateColumn.setCellValueFactory(new PropertyValueFactory<StatementEntry, String>("date"));

        descriptionColumn.setCellValueFactory(new Callback<CellDataFeatures<StatementEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<StatementEntry, String> se) {
                // se.getValue() returns the StatementEntry instance for a particular TableView row
                return se.getValue().getDesc();
            }
        });

        descriptionColumn.setCellFactory(TextFieldTableCell.<StatementEntry, String>forTableColumn(new DefaultStringConverter()));
        checkColumn.setCellValueFactory(new PropertyValueFactory<StatementEntry, String>("checkNum"));
        paymentClassColumn.setCellValueFactory(new PropertyValueFactory<StatementEntry, String>("payClass"));

        classDescriptionColumn.setCellValueFactory(new PropertyValueFactory<SelectItem, String>("classDescription"));
        classDescriptionColumn.setCellFactory(ComboBoxTableCell.<SelectItem, String>forTableColumn(paymentCats));

        amountColumn.setCellValueFactory(new PropertyValueFactory<StatementEntry, Float>("transAmount"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<StatementEntry, Float>("transBalance"));

        EventHandler<TableColumn.CellEditEvent<SelectItem, String>> nameEditHandler;
        nameEditHandler = new EventHandler<TableColumn.CellEditEvent<SelectItem, String>>() {

            @Override
            public void handle(TableColumn.CellEditEvent<SelectItem, String> t) {
                TableColumn<StatementEntry, ?> tc = tableView.getEditingCell().getTableColumn();
                boolean newAssign = false;
                String newValue = t.getNewValue();
                StatementEntry item = tableView.getSelectionModel().getSelectedItem();
                if (item.getPayClass().getValue() == null || "".equals(item.getPayClass().getValue())) {
                    newAssign = true;
                }
                String newPaymentClass = paymentClassMap.get(newValue);
                item.setClassDescription(newValue);
                item.setPaymentClass(newPaymentClass);
                item.setPayClass(newPaymentClass);
                doSave(item);
                if (newAssign) {
                    int cnt = Integer.parseInt(summaryController.noPayCl.getText());
                    cnt--;
                    summaryController.noPayCl.setText(Integer.toString(cnt));
                }
            }
        };

        classDescriptionColumn.setOnEditCommit(nameEditHandler);

        EventHandler<TableColumn.CellEditEvent<StatementEntry, String>> textHandler
                = new EventHandler<TableColumn.CellEditEvent<StatementEntry, String>>() {

                    @Override
                    public void handle(TableColumn.CellEditEvent<StatementEntry, String> t) {
                        TableColumn<StatementEntry, ?> tc = tableView.getEditingCell().getTableColumn();
                        String newValue = t.getNewValue();
                        StatementEntry item = tableView.getSelectionModel().getSelectedItem();
                        item.setDesc(newValue);
                        doSave(item);
                    }

                };

        descriptionColumn.setOnEditCommit(textHandler);
        descriptionColumn.editableProperty().set(true);

        paymentClassColumn.setCellValueFactory(new Callback<CellDataFeatures<StatementEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<StatementEntry, String> p) {
                return p.getValue().getPayClass();
            }

        });

        getLedgerTask.run();
    }

    @FXML
    void doImport() {
        getStatementController().doImport();
    }

    public void doSave(StatementEntry item) {
        getStatementController().doSave(item);
    }

    @FXML
    void doExport() {
        getStatementController().doExport();
    }

    @FXML
    void doSearch() {
        SearchWindow searchCriteria;
        searchCriteria = new SearchWindow();
    }

    @FXML
    void doReset() {
        getLedgerTask = new Task<ObservableList<StatementEntry>>() {
            @Override
            protected ObservableList call() throws Exception {
                System.out.println("BankController.doReset() task running");
                criteria.setStartDate(startDate);
                criteria.setEndDate(endDate);
                criteria.setPaymentClass("");
                criteria.setMinAmount("");
                criteria.setMaxAmount("");
                criteria.setSearchTarget("");
                return FXCollections.observableArrayList(getStatementController().getLedger(criteria));
            }
        };
        getLedgerTask.setOnSucceeded(new EventHandler() {
            @Override
            public void handle(Event t) {
                System.out.println("BankController.doReset() task completed");
                tableView.getItems().clear();
                setLedgerList(getLedgerTask.getValue());
                tableView.getItems().addAll(ledgerList);
                progressBar.setProgress(0);
                progressBar.setVisible(false);
            }
        });
        getLedgerTask.run();
    }

    @FXML
    void doClassMaint() {

    }

    @FXML
    void viewLedger() {

    }

    public ArrayList<SelectItem> getPaymentClassList() {
        ArrayList<SelectItem> items = new ArrayList<>();
        paymentClassMap = new HashMap<>();
        paymentCats = FXCollections.observableArrayList();
        return getStatementController().getPaymentClassList();
    }

    public Node createPage(Integer idx) {
        System.out.println(idx.toString());
        return null;
    }

    /**
     * @return the ledgerList
     */
    @FXML
    public ObservableList<StatementEntry> getLedgerList() {
        return ledgerList;
    }

    /**
     * @param ledgerList the ledgerList to set
     */
    public void setLedgerList(ObservableList<StatementEntry> ledgerList) {
        this.ledgerList = ledgerList;
    }

    @FXML
    void doExit() {
        System.exit(0);
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the pctLoaded
     */
    public IntegerProperty getPctLoaded() {
        return pctLoaded;
    }

    /**
     * @param pctLoaded the pctLoaded to set
     */
    public void setPctLoaded(IntegerProperty pctLoaded) {
        this.pctLoaded = pctLoaded;
    }

    /**
     * @return the statementController
     */
    public static StatementController getStatementController() {
        return statementController;
    }

    /**
     * @param aStatementController the statementController to set
     */
    public static void setStatementController(StatementController aStatementController) {
        statementController = aStatementController;
    }

    /**
     * @return the u2
     */
    public U2 getU2() {
        return u2;
    }

    /**
     * @param u2 the u2 to set
     */
    public void setU2(U2 u2) {
        this.u2 = u2;
    }

    /**
     * @return the windowEventHandler
     */
    public WindowEventHandler<WindowEvent> getWindowEventHandler() {
        return windowEventHandler;
    }

    /**
     * @return the criteria
     */
    public SearchCriteria getCriteria() {
        return criteria;
    }

    /**
     * @param criteria the criteria to set
     */
    public void setCriteria(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    class ChangeHandler<String> implements ChangeListener {

        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
            System.out.println("ChangeHandler.changed() invoked.");
            if (t1 != null) {
                System.out.println(t1.toString());
                if (paymentClassMap.containsKey(t1.toString())) {
                    System.out.println("new category " + paymentClassMap.get(t1.toString()));
                }
            }
        }

    }

    class WindowEventHandler<WindowEvent> implements EventHandler {

        @Override
        public void handle(Event event) {
            getLedgerTask = new Task<ObservableList<StatementEntry>>() {
                @Override
                protected ObservableList call() throws Exception {
                    return FXCollections.observableArrayList(getStatementController().getLedger(criteria));
                }
            };
            getLedgerTask.setOnSucceeded(new EventHandler() {
                @Override
                public void handle(Event t) {
                    tableView.getItems().clear();
                    setLedgerList(getLedgerTask.getValue());
                    tableView.getItems().addAll(ledgerList);
                    progressBar.setProgress(0);
                    progressBar.setVisible(false);
                }
            });
            getLedgerTask.run();
        }

    }

}
