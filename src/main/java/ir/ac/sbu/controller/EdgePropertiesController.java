package ir.ac.sbu.controller;

import ir.ac.sbu.utility.CheckUtility;
import ir.ac.sbu.utility.DialogUtility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ir.ac.sbu.command.ChangeEdgeCmd;
import ir.ac.sbu.command.CommandManager;
import ir.ac.sbu.model.EdgeModel;

public class EdgePropertiesController {
    @FXML
    private Button applyBtn;
    @FXML
    private TextField tokenText;
    @FXML
    private TextField funcText;
    @FXML
    private CheckBox graphChk;
    @FXML
    private CheckBox globalChk;

    private EdgeModel edge;

    public void init(EdgeModel edge) {
        this.edge = edge;
        tokenText.setText(edge.getToken());
        funcText.setText(edge.getFunction());
        globalChk.setSelected(edge.isGlobal());
        graphChk.setSelected(edge.isGraph());
    }

    public void apply(ActionEvent actionEvent) {
        applyFunc();
    }

    public void applyWithEnterKey(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            applyFunc();
        }
    }

    private void applyFunc() {
        try {
            CheckUtility.checkTokenName(tokenText.getText());
            CheckUtility.checkFunctionName(funcText.getText());
            Stage stage = (Stage) applyBtn.getScene().getWindow();
            CommandManager.getInstance().applyCommand(new ChangeEdgeCmd(edge, tokenText.getText(), funcText.getText(), graphChk.isSelected(), globalChk.isSelected()));
            stage.close();
        } catch (IllegalArgumentException e) {
            DialogUtility.showErrorDialog(e.getMessage());
        }
    }
}
