module devalrykemes.exchangeconverterapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.google.gson;
    requires java.sql;

    opens devalrykemes.exchangeconverterapp to javafx.fxml;
    exports devalrykemes.exchangeconverterapp;

    opens devalrykemes.exchangeconverterapp.controller to javafx.fxml;
    exports devalrykemes.exchangeconverterapp.controller;
}