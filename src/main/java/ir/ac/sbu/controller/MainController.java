package ir.ac.sbu.controller;

import ir.ac.sbu.Main;
import ir.ac.sbu.command.CommandManager;
import ir.ac.sbu.exception.TableException;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;
import ir.ac.sbu.parser.LLParserGenerator;
import ir.ac.sbu.service.ExportService;
import ir.ac.sbu.service.SaveLoadService;
import ir.ac.sbu.utility.CheckUtility;
import ir.ac.sbu.utility.DialogUtility;
import ir.ac.sbu.utility.GenerateUID;
import ir.ac.sbu.utility.ResourceUtility;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MainController {
    @FXML
    private ListView<GraphModel> graphList;
    @FXML
    private AnchorPane pane;
    @FXML
    private VBox mainContainer;
    @FXML
    private MenuItem fileNewMenuItem;
    @FXML
    private MenuItem fileOpenMenuItem;
    @FXML
    private MenuItem fileSaveMenuItem;
    @FXML
    private MenuItem fileSaveAsMenuItem;
    @FXML
    private MenuItem parserRenumberMenuItem;
    @FXML
    private MenuItem parserCheckGraphMenuItem;
    @FXML
    private MenuItem parserBuildTableMenuItem;
    @FXML
    private MenuItem parserBuildPrettyTableMenuItem;
    @FXML
    private MenuItem parserBuildCSVTableMenuItem;
    @FXML
    private MenuItem parserExportFunctionsMenuItem;
    @FXML
    private MenuItem parserExportFullParserMenuItem;
    @FXML
    private Button addGraphBtn;
    @FXML
    private MenuItem helpAboutMenuItem;
    @FXML
    private MenuItem helpLicenseMenuItem;
    @FXML
    private MenuItem helpManualMenuItem;

    public static final String mainGraphName = "MAIN";
    private GraphModel mainGraphModel;
    private DrawPaneController drawPaneController;
    private ObservableList<GraphModel> graphs;
    // Last file which is used for saving PGEN file will be stored to used for later save in applications
    // It will be used when open a file too.
    private File lastChosenFileForSave = null;
    private WindowEvent windowEvent;

    @FXML
    private void initialize() {
        this.graphs = FXCollections.observableArrayList();
        this.drawPaneController = new DrawPaneController(pane);
        CommandManager.init(drawPaneController);
        graphList.setItems(graphs);

        mainGraphModel = new GraphModel(mainGraphName);
        drawPaneController.setGraph(mainGraphModel);
        graphs.add(mainGraphModel);

        graphList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            drawPaneController.setGraph(newValue);
            drawPaneController.refresh();
        });

        graphList.setCellFactory(param ->
        {
            ListCell<GraphModel> cell = new ListCell<GraphModel>() {
                @Override
                protected void updateItem(GraphModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        textProperty().bind(item.nameProperty());
                    }
                }
            };

            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteButton = new MenuItem("Delete");
            MenuItem renameButton = new MenuItem("Rename");
            MenuItem duplicateButton = new MenuItem("Duplicate");
            MenuItem moveUpButton = new MenuItem("Move Up");
            MenuItem moveDownButton = new MenuItem("Move Down");

            deleteButton.setOnAction(event -> cell.getListView().getItems().remove(cell.getItem()));
            renameButton.setOnAction(event -> {
                Optional<String> newValue = DialogUtility.showInputDialog(cell.getItem().getName(), "Rename Graph");
                if (newValue.isPresent()) {
                    try {
                        CheckUtility.checkGraphName(newValue.get());
                        cell.getItem().setName(newValue.get());
                    } catch (IllegalArgumentException e) {
                        DialogUtility.showErrorDialog(e.getMessage());
                    }
                }
            });
            duplicateButton.setOnAction(event -> {
                Optional<String> result = DialogUtility.showInputDialog(cell.getItem().getName(), "New Graph");
                if (result.isPresent()) {
                    try {
                        CheckUtility.checkGraphName(result.get());
                        GraphModel currentGraph = cell.getItem();
                        GraphModel duplicateGraph = currentGraph.createCopy(result.get());
                        graphs.add(duplicateGraph);
                    } catch (IllegalArgumentException e) {
                        DialogUtility.showErrorDialog(e.getMessage());
                    }
                }
            });
            moveUpButton.setOnAction(event -> {
                int currentIndex = cell.getIndex();
                Collections.swap(graphs, currentIndex, currentIndex - 1);
            });
            moveDownButton.setOnAction(event -> {
                int currentIndex = cell.getIndex();
                Collections.swap(graphs, currentIndex, currentIndex + 1);
            });

            contextMenu.getItems().addAll(deleteButton, renameButton, duplicateButton, moveUpButton, moveDownButton);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                    if (cell.getItem() == null) {
                        cell.textProperty().unbind();
                        cell.setText("");
                    }
                } else {
                    if (cell.getItem().getName().equals(mainGraphName)) {
                        deleteButton.setDisable(true);
                        renameButton.setDisable(true);
                        moveUpButton.setDisable(true);
                        moveDownButton.setDisable(true);
                    }
                    // {@code mainGraphName} and element after it can not move to up
                    if (cell.getIndex() <= 1) {
                        moveUpButton.setDisable(true);
                    } else if (cell.getIndex() == graphs.size() - 1) {
                        moveDownButton.setDisable(true);
                    }
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });

        mainContainer.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        mainContainer.addEventHandler(KeyEvent.KEY_RELEASED, event -> drawPaneController.setFirstNode(null));
        fileNewMenuItem.setOnAction(this::fileNew);
        fileOpenMenuItem.setOnAction(this::fileOpen);
        fileSaveMenuItem.setOnAction(this::fileSave);
        fileSaveAsMenuItem.setOnAction(this::fileSaveAs);
        parserRenumberMenuItem.setOnAction(this::parserRenumber);
        parserCheckGraphMenuItem.setOnAction(this::parserCheckGraphs);
        parserBuildTableMenuItem.setOnAction(this::parserBuildTable);
        parserBuildPrettyTableMenuItem.setOnAction(this::parserBuildPrettyTable);
        parserBuildCSVTableMenuItem.setOnAction(this::parserBuildCsvTable);
        parserExportFunctionsMenuItem.setOnAction(this::parserExportFunctions);
        parserExportFullParserMenuItem.setOnAction(this::parserExportFullParser);
        helpAboutMenuItem.setOnAction(this::helpAbout);
        helpLicenseMenuItem.setOnAction(this::helpLicense);
        helpManualMenuItem.setOnAction(this::helpManual);
        addGraphBtn.setOnAction(this::addGraph);

        setOnCloseRequest();
    }

    public void addGraph(ActionEvent actionEvent) {
        Optional<String> result = DialogUtility.showInputDialog("Name", "New Graph");
        if (result.isPresent()) {
            try {
                CheckUtility.checkGraphName(result.get());
                GraphModel newGraph = new GraphModel(result.get());
                graphs.add(newGraph);
            } catch (IllegalArgumentException e) {
                DialogUtility.showErrorDialog(e.getMessage());
            }
        }
    }

    public void parserRenumber(ActionEvent actionEvent) {
        List<NodeModel> nodes = graphList.getItems().stream().
                flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setId(i);
        }
        GenerateUID.setIdCounter(nodes.size());
        drawPaneController.refresh();
    }

    public void parserCheckGraphs(ActionEvent actionEvent) {
        parserRenumber(actionEvent);
        try {
            new LLParserGenerator(graphs);
            DialogUtility.showSuccessDialog("There is no problem!");
        } catch (TableException e) {
            DialogUtility.showErrorDialog(e.getMessages());
        } catch (Exception e) {
            DialogUtility.showErrorDialog(String.format("Unhandled exception happened %s.\nPlease send your " +
                    "PGen file to us. Thanks.", e.toString()));
        }
    }

    private void parserBuildTable(ActionEvent actionEvent) {
        parserRenumber(actionEvent);
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save Table to", "*.npt");
        try {
            if (selectedFile != null) {
                LLParserGenerator parser = new LLParserGenerator(graphs);
                parser.buildTable(selectedFile);
                DialogUtility.showSuccessDialog("Successful!");
            }
        } catch (TableException e) {
            DialogUtility.showErrorDialog(e.getMessages());
        } catch (Exception e) {
            DialogUtility.showErrorDialog(String.format("Unhandled exception happened %s.\nPlease send your " +
                    "PGen file to us. Thanks.", e.toString()));
        }
    }

    private void parserBuildPrettyTable(ActionEvent actionEvent) {
        parserRenumber(actionEvent);
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save Pretty Table to", "*.prt");
        try {
            if (selectedFile != null) {
                LLParserGenerator parser = new LLParserGenerator(graphs);
                parser.buildPrettyTable(selectedFile);
                DialogUtility.showSuccessDialog("Successful!");
            }
        } catch (TableException e) {
            DialogUtility.showErrorDialog(e.getMessages());
        } catch (Exception e) {
            DialogUtility.showErrorDialog(String.format("Unhandled exception happened %s.\nPlease send your " +
                    "PGen file to us. Thanks.", e.toString()));
        }
    }

    private void parserBuildCsvTable(ActionEvent actionEvent) {
        parserRenumber(actionEvent);
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save CSV Table to", "*.csv");
        try {
            if (selectedFile != null) {
                LLParserGenerator parser = new LLParserGenerator(graphs);
                parser.buildCSVTable(selectedFile);
                DialogUtility.showSuccessDialog("Successful!");
            }
        } catch (TableException e) {
            DialogUtility.showErrorDialog(e.getMessages());
        } catch (Exception e) {
            DialogUtility.showErrorDialog(String.format("Unhandled exception happened %s.\nPlease send your " +
                    "PGen file to us. Thanks.", e.toString()));
        }
    }

    private void parserExportFunctions(ActionEvent actionEvent) {
        parserRenumber(actionEvent);
        File selectedDirectory = DialogUtility.showDirectoryDialog(pane.getScene().getWindow());
        if (selectedDirectory != null) {
            ExportService exportService = new ExportService(selectedDirectory.toPath());
            exportService.exportGraphs(graphs);
            DialogUtility.showSuccessDialog("Exported successfully");
        }
    }

    public void parserExportFullParser(ActionEvent actionEvent) {
        parserRenumber(actionEvent);
        File directorySelected = DialogUtility.showDirectoryDialog(pane.getScene().getWindow());
        try {
            if (directorySelected != null) {
                InputStream parserSource = ResourceUtility.getResourceAsStream("parser/Parser.java");
                InputStream lexicalSource = ResourceUtility.getResourceAsStream("parser/Lexical.java");
                InputStream codeGeneratorSource = ResourceUtility.getResourceAsStream("parser/CodeGenerator.java");
                Path destination = Paths.get(directorySelected.getPath());

                LLParserGenerator parser = new LLParserGenerator(graphs);
                parser.buildTable(new File(destination + "/table.npt"));
                Files.copy(parserSource, destination.resolve("Parser.java"), REPLACE_EXISTING);
                Files.copy(lexicalSource, destination.resolve("Lexical.java"), REPLACE_EXISTING);
                Files.copy(codeGeneratorSource, destination.resolve("CodeGenerator.java"), REPLACE_EXISTING);

                DialogUtility.showSuccessDialog("Parser generated successfully");
            }
        } catch (TableException e) {
            DialogUtility.showErrorDialog(e.getMessages());
        } catch (IOException e) {
            DialogUtility.showErrorDialog("Exception during exporting files", e.getMessage());
        } catch (Exception e) {
            DialogUtility.showErrorDialog(String.format("Unhandled exception happened %s.\nPlease send your " +
                    "PGen file to us. Thanks.", e.toString()));
        }
    }

    private void fileNew(ActionEvent actionEvent) {
        String dialogString = lastChosenFileForSave != null ? "Do you want to save changes?" : "Do you want to save changes to a file?";
        Optional<ButtonType> result = DialogUtility.showConfirmationDialog("PGEN", dialogString);
        if (!result.isPresent() || result.get().equals(ButtonType.CANCEL)) {
            return;
        }
        ButtonType selectedButton = result.get();
        if (selectedButton.equals(ButtonType.YES)) {
            fileSave(actionEvent);
        }
        lastChosenFileForSave = null;
        graphs.clear();
        CommandManager.getInstance().clearCommandHistory();
        mainGraphModel = new GraphModel(mainGraphName);
        graphs.add(mainGraphModel);
        drawPaneController.setGraph(mainGraphModel);
        drawPaneController.refresh();
    }

    private void fileOpen(ActionEvent actionEvent) {
        File selectedFile = DialogUtility.showOpenDialog(pane.getScene().getWindow(), "*.pgs");
        if (selectedFile != null) {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.load(graphList);
            drawPaneController.setGraph(graphList.getItems().get(0));
            drawPaneController.refresh();
            lastChosenFileForSave = selectedFile;
        }
    }

    private void fileSave(ActionEvent actionEvent) {
        _fileSave();
    }

    private void fileSaveAs(ActionEvent actionEvent) {
        _fileSaveAs();
    }

    private void _fileSave() {
        if (lastChosenFileForSave != null) {
            saveGraphs(lastChosenFileForSave);
        } else {
            _fileSaveAs();
        }
    }

    private void _fileSaveAs() {
        File selectedFile = DialogUtility.showSaveDialog(pane.getScene().getWindow(), "Save PGen File", "*.pgs");
        if (selectedFile != null) {
            lastChosenFileForSave = selectedFile;
            saveGraphs(lastChosenFileForSave);
        } else {
            if (windowEvent != null) {
                windowEvent.consume();
            }
        }
    }

    private void saveGraphs(File file) {
        parserRenumber(null);
        SaveLoadService exportService = new SaveLoadService(file);
        exportService.save(graphs);
    }

    public void helpAbout(ActionEvent actionEvent) {
        showModal(ResourceUtility.getResource("fxml/About.fxml"), "About");
    }

    public void helpLicense(ActionEvent actionEvent) {
        showModal(ResourceUtility.getResource("fxml/License.fxml"), "License");
    }

    public void helpManual(ActionEvent actionEvent) {
        showModal(ResourceUtility.getResource("fxml/Help.fxml"), "Help");
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) {
            if (keyEvent.getCode().equals(KeyCode.Z) && !keyEvent.isShiftDown()) {
                CommandManager.getInstance().rollBack();
            }
            if (keyEvent.isShiftDown() && keyEvent.getCode().equals(KeyCode.Z)) {
                CommandManager.getInstance().redoCommand();
            }
        }
    }

    private void showModal(URL resource, String title) {
        final FXMLLoader loader = new FXMLLoader(resource);

        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.getIcons().add(new Image(ResourceUtility.getResourceAsStream("assets/Icon.png")));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOnCloseRequest() {
        Main.getPrimaryStage().setOnCloseRequest(event -> {
            windowEvent = event;
            String dialogString = lastChosenFileForSave != null ? "Do you want to save changes?" : "Do you want to save changes to a file?";
            Optional<ButtonType> result = DialogUtility.showConfirmationDialog("PGEN", dialogString);
            if (!result.isPresent()) {
                return;
            }
            ButtonType selectedButton = result.get();
            if (selectedButton.equals(ButtonType.YES)) {
                _fileSave();
            }
            if (selectedButton.equals(ButtonType.YES) || selectedButton.equals(ButtonType.NO)) {
                if (!windowEvent.isConsumed()) {
                    Platform.exit();
                    System.exit(0);
                }
            } else if (selectedButton.equals(ButtonType.CANCEL)) {
                windowEvent.consume();
            }
        });
    }
}
