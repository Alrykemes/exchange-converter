package devalrykemes.exchangeconverterapp.controller;

import devalrykemes.exchangeconverterapp.exceptions.SystemException;
import devalrykemes.exchangeconverterapp.repository.H2DBRepository;
import devalrykemes.exchangeconverterapp.service.ExchageRateAPI;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.w3c.dom.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class WindowController {

    @FXML
    private ComboBox<String> comboboxOf;
    @FXML
    private ComboBox<String> comboboxFrom;
    @FXML
    private ChoiceBox<String> choiceboxFormat;
    @FXML
    private TextField textFieldToConvert;
    @FXML
    private TextField textFieldConverted;
    @FXML
    private Button buttonConverter;
    @FXML
    private ListView<String> listHistorich;

    private ExchageRateAPI exchageRateAPI;
    private Connection connectionH2DB;
    private Alert alert;
    private DecimalFormat currencyFormat;
    private TextFormatter<BigDecimal> textFormatter;
    private List<String> listHistorichItems;

    @FXML
    public void initialize() {
        currencyFormat = new DecimalFormat("#,##0.00");
        exchageRateAPI = new ExchageRateAPI();
        connectionH2DB = H2DBRepository.getConnection();
        listHistorichItems = new ArrayList<>();
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conversor de Moedas");

        textFormatter = new TextFormatter<>(filter);
        textFieldToConvert.setTextFormatter(textFormatter);

        choiceboxFormat.getItems().addAll("Siglas","Nomes");

        comboboxOf.setOnMouseClicked(event -> {
            this.setComboboxOf();
        });

        comboboxFrom.setOnMouseClicked(event -> {
            this.setComboboxFrom();
        });

        buttonConverter.setOnAction(event -> {
            String coinBase = null;
            String coinToConvert = null;
            String valueToConvert = null;
            String valueConverted = null;

            if (verifyChoiceBox(choiceboxFormat)) {
                if (verifyComboBox(comboboxOf)) {
                    coinBase = comboboxOf.getValue();
                    if (verifyComboBox(comboboxFrom)) {
                        coinToConvert = comboboxFrom.getValue();
                        if (verifyTextField(textFieldToConvert)) {
                            valueToConvert = textFieldToConvert.getText();

                            if(verifyChoiceBox(choiceboxFormat)) {
                                try {
                                    if (choiceboxFormat.getValue().equals("Nomes")) {
                                        coinBase = exchageRateAPI.getSiglaCoinbyName(coinBase);
                                        coinToConvert = exchageRateAPI.getSiglaCoinbyName(coinToConvert);
                                    }
                                } catch (SystemException ex) {
                                    alert.setContentText(ex.getMessage());
                                    alert.showAndWait();
                                }
                            }

                            try {
                                valueConverted = String.format(exchageRateAPI.convertCurrency(
                                        coinBase,
                                        coinToConvert,
                                        new BigDecimal(valueToConvert))
                                        .setScale(2, RoundingMode.HALF_UP)
                                        .toString());

                                textFieldConverted.setText(valueConverted);

                                listHistorichItems.add(valueToConvert +
                                        " " + coinBase +
                                        " -> " + valueConverted +
                                        " " + coinToConvert);

                                listHistorich.getItems().clear();
                                listHistorich.getItems().addAll(listHistorichItems);
                            } catch (SystemException ex) {
                                alert.setContentText(ex.getMessage());
                                alert.showAndWait();
                            }
                        }
                    }
                }
            }
        });
    }

    private void setComboboxOf() {
        try {
            if (choiceboxFormat.getValue().toUpperCase().equals("SIGLAS")) {
                comboboxOf.getItems().clear();
                comboboxOf.getItems().addAll(exchageRateAPI.getAbbreviatedCoins());
            }
            if (choiceboxFormat.getValue().toUpperCase().equals("NOMES")) {
                comboboxOf.getItems().clear();
                comboboxOf.getItems().addAll(exchageRateAPI.getNameCoins());
            }
        } catch (SystemException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (NullPointerException ex) {
            alert.setContentText("Selecione um Formato de moedas");
            alert.showAndWait();
        }
    }

    private void setComboboxFrom() {
        try {
            if (choiceboxFormat.getValue().toUpperCase().equals("SIGLAS")) {
                comboboxFrom.getItems().clear();
                comboboxFrom.getItems().addAll(exchageRateAPI.getAbbreviatedCoins());
            }
            if (choiceboxFormat.getValue().toUpperCase().equals("NOMES")) {
                comboboxFrom.getItems().clear();
                comboboxFrom.getItems().addAll(exchageRateAPI.getNameCoins());
            }
        } catch (SystemException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (NullPointerException ex) {
            alert.setContentText("Selecione um Formato de moedas");
            alert.showAndWait();
        }
    }

    public boolean verifyTextField(TextField textField) {
        if(textField.getText() == null || textField.getText().isEmpty()) {
            alert.setContentText("Você deve inserir um valor para ser convertido!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public boolean verifyChoiceBox(ChoiceBox<String> choiceBox) {
        if(choiceBox.getValue() == null) {
            alert.setContentText("Selecione um forma de vizualização das moedas!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public boolean verifyComboBox(ComboBox<String> comboBox) {
        if(comboBox.getValue() == null || comboBox.getValue().isEmpty()) {
            alert.setContentText("Selecione os tipos de moeda para converter!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    UnaryOperator<TextFormatter.Change> filter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("\\d*(\\.\\d{0,2})?")) {
            return change;
        }
        return null;
    };

    public void refreshHistorich() {

    }
}