/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.bank.view;

import com.webfront.bank.controller.BankController;
import com.webfront.bank.controller.StatementController;
import com.webfront.bank.model.SearchCriteria;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author rlittle
 */
public class SearchWindow {

    public static SearchCriteria criteria;
    TextField searchString;
    TextField amtFrom;
    TextField amtTo;
    Button btnOK;
    Button btnCancel;
    ComboBox categoryChoices;

    AnchorPane pane;
    DatePicker startDatePicker;
    DatePicker endDatePicker;
    StatementController controller;
    BankController bankController;

    public SearchWindow() {
        criteria = new SearchCriteria();
        controller = BankController.getStatementController();
        bankController = BankController.getBankController();
        //bankController.setCriteria(criteria);
        pane = new AnchorPane();
        pane.setPrefHeight(324.0);
        pane.setPrefWidth(548.0);

        Label searchLabel = new Label("Description");
        Label amtLabel = new Label("Amount");
        Label dateRangeLabel = new Label("Date range");
        Label dateFromLabel = new Label("From");
        Label dateToLabel = new Label("To");
        Label amtFromLabel = new Label("At least");
        Label amtToLabel = new Label("Not more than");
        Label catLabel = new Label("Category");

        searchString = new TextField();
        amtFrom = new TextField();
        amtTo = new TextField();
        btnOK = new Button("OK");
        btnCancel = new Button("Cancel");
        
        startDatePicker = new DatePicker();
        startDatePicker.setValue(LocalDate.now().minusDays(60));
        startDatePicker.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                LocalDate sDate = startDatePicker.getValue();
                criteria.setStartDate(sDate.format(DateTimeFormatter.ofPattern("MM/dd/yy")));
                if (criteria.getEndDate() != null) {
                    try {
                        criteria.validateRange(sDate, endDatePicker.getValue());
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }
        });

        endDatePicker = new DatePicker();
        endDatePicker.setValue(LocalDate.now());
        endDatePicker.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                LocalDate eDate = endDatePicker.getValue();
                criteria.setEndDate(eDate.format(DateTimeFormatter.ofPattern("MM/dd/yy")));
                if (criteria.getStartDate() != null) {
                    try {
                        criteria.validateRange(startDatePicker.getValue(), eDate);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else {
                    startDatePicker.setValue(eDate.minusDays(30));
                }
            }
        });
        categoryChoices = new ComboBox();

        searchLabel.setLayoutX(14.0);
        searchLabel.setLayoutY(17.0);
        searchString.setLayoutX(100.0);
        searchString.setLayoutY(14.0);
        searchString.setPrefWidth(429.0);

        dateRangeLabel.setLayoutX(14.0);
        dateRangeLabel.setLayoutY(80.0);
        dateFromLabel.setLayoutX(100.0);
        dateFromLabel.setLayoutY(55.0);
        startDatePicker.setLayoutX(100.0);
        startDatePicker.setLayoutY(75.0);

        dateToLabel.setLayoutX(305.0);
        dateToLabel.setLayoutY(55.0);
        endDatePicker.setLayoutX(305.0);
        endDatePicker.setLayoutY(75.0);

        amtLabel.setLayoutX(14.0);
        amtLabel.setLayoutY(160.0);
        amtFromLabel.setLayoutX(100.0);
        amtFromLabel.setLayoutY(130.0);
        amtFrom.setLayoutX(100.0);
        amtFrom.setLayoutY(155.0);

        amtToLabel.setLayoutX(305.0);
        amtToLabel.setLayoutY(130.0);
        amtTo.setLayoutX(305.0);
        amtTo.setLayoutY(155.0);

        catLabel.setLayoutX(14.0);
        catLabel.setLayoutY(212.0);
        categoryChoices.setLayoutX(106.0);
        categoryChoices.setLayoutY(206.0);
        boolean addAll;
        addAll = categoryChoices.getItems().addAll(controller.paymentCats);

        btnOK.setLayoutX(359.0);
        btnOK.setLayoutY(275.0);
        btnOK.setPrefWidth(79.0);
        btnCancel.setLayoutX(454.0);
        btnCancel.setLayoutY(275.0);
        btnCancel.setPrefWidth(79.0);

        pane.getChildren().add(searchLabel);
        pane.getChildren().add(searchString);
        pane.getChildren().add(dateRangeLabel);
        pane.getChildren().add(dateFromLabel);
        pane.getChildren().add(dateToLabel);

        pane.getChildren().add(startDatePicker);
        pane.getChildren().add(endDatePicker);
        pane.getChildren().add(amtLabel);
        pane.getChildren().add(amtFromLabel);
        pane.getChildren().add(amtToLabel);
        pane.getChildren().add(amtFrom);
        pane.getChildren().add(amtTo);
        pane.getChildren().add(catLabel);
        pane.getChildren().add(categoryChoices);
        pane.getChildren().add(btnOK);
        pane.getChildren().add(btnCancel);

        final Stage stage = new Stage();

        stage.addEventHandler(WindowEvent.WINDOW_HIDING, bankController.getWindowEventHandler());

        Scene scene = new Scene(pane);

        btnOK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                criteria.setSearchTarget(searchString.getText());

                if (startDatePicker.getValue() == null) {
                    criteria.setStartDate("");
                } else {
                    criteria.setStartDate(startDatePicker.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yy")));
                }

                if (endDatePicker.getValue() == null) {
                    criteria.setEndDate("");
                } else {
                    criteria.setEndDate(endDatePicker.getValue().format(DateTimeFormatter.ofPattern("MM/dd/yy")));
                }

                criteria.setMinAmount(amtFrom.getText());
                criteria.setMaxAmount(amtTo.getText());

                if (categoryChoices.getValue() == null) {
                    criteria.setPaymentClass("");
                } else {
                    criteria.setPaymentClass(categoryChoices.getValue().toString());
                }
                bankController.setCriteria(criteria);
                stage.close();
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                stage.close();
            }
        });

        stage.setTitle("Search");
        stage.setScene(scene);
        stage.show();
    }

}
