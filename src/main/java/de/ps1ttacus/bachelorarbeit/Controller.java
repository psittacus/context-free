package de.ps1ttacus.bachelorarbeit;

import de.ps1ttacus.Ableitungsfolge.Ableitungsfolge;
import de.ps1ttacus.Beispielwoerter.BeispielwoerterGenerierung;
import de.ps1ttacus.SyntaxTree.TreeGenerator;
import de.ps1ttacus.SyntaxTree.TreeNode;
import de.ps1ttacus.SyntaxTree.TreeVisualization;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    public Pane pane;
    @FXML
    public TextField terminaleInput;
    @FXML
    public TextField nichtterminaleInput;
    @FXML
    public TextField startsymbolInput;
    @FXML
    public ScrollPane testwortScrollpane;
    @FXML
    public Button testwortPlus;
    @FXML
    public HBox testwortHBox;
    @FXML
    public HBox contentHBox;
    @FXML
    public Button addProductionRule;
    @FXML
    public ScrollPane productionRulesScrollpane;
    @FXML
    public VBox productionRulesVBox;
    @FXML
    public HBox ableitungsfolgeHBox;
    public HBox erzeugbareWoerterHBox;
    public HBox woerterNichtInDerSpracheHBox;

    private Model model;
    private TreeGenerator treeGenerator;

    private TreeVisualization treeVisualization;

    private Ableitungsfolge ableitungsfolge;
    private BeispielwoerterGenerierung beispielwoerterGenerierung;

    public Controller() {
        model = new Model();
        treeGenerator = new TreeGenerator(model);
        //productionRulesInputRichtext = new GenericStyledArea();
    }

    @FXML
    public void initialize() {
        //treeGenerator.generateTree(this.pane);
        //Es soll nur ein Startsymbol geben
        startsymbolInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) {
                startsymbolInput.setText(oldValue);
            }
        });
        this.pane.setViewOrder(1);
        this.productionRulesScrollpane.setFitToWidth(true);
    }

    public void setTerminale(MouseEvent mouseEvent) {
            model.setTerminale(new ArrayList<>(List.of(terminaleInput.getText().split(","))));
    }

    public void setNichtterminale(MouseEvent mouseEvent) {
            model.setNichtterminale(new ArrayList<>(List.of(nichtterminaleInput.getText().split(","))));
    }

    public void setStartsymbol(KeyEvent keyEvent) {
            model.setStartsymbol(startsymbolInput.getText());
    }

    private boolean isModelComplete() {
        if(this.model.getProduktionen() == null || this.model.getProduktionen().size() == 0) {
            return false;
        }
        if(this.model.getNichtterminale() == null || this.model.getNichtterminale().isEmpty()) {
            return false;
        }
        if(this.model.getTerminale() == null || this.model.getTerminale().isEmpty()) {
            return false;
        }
        if(this.model.getStartsymbol() == null || this.model.getStartsymbol().isEmpty()) {
            return false;
        }
        if(this.model.getTestwort() == null || this.model.getTestwort().isEmpty()) {
            return false;
        }
        return true;
    }

    private void checkIfTestwordsBelongsToLanguage() {
        if(testwortHBox.getChildren() != null && !testwortHBox.getChildren().isEmpty()) {
            for(int i = 0; i < testwortHBox.getChildren().size() - 1; i++) {
                if (testwortHBox.getChildren().get(i) instanceof Button) {
                    Button btn = (Button) testwortHBox.getChildren().get(i);
                    model.setTestwort(btn.getText());
                    treeGenerator.setModel(this.model);
                    if(treeGenerator.generateTree() != null) {
                        btn.setStyle("-fx-background-color: #33cc33;");
                    } else {
                        btn.setStyle("-fx-background-color: #cc3333;");
                    }
                }
            }
        }

        if(woerterNichtInDerSpracheHBox.getChildren() != null && !woerterNichtInDerSpracheHBox.getChildren().isEmpty()) {
            for(int i = 0; i < woerterNichtInDerSpracheHBox.getChildren().size() - 1; i++) {
                if (woerterNichtInDerSpracheHBox.getChildren().get(i) instanceof Button) {
                    Button btn = (Button) woerterNichtInDerSpracheHBox.getChildren().get(i);
                    model.setTestwort(btn.getText());
                    treeGenerator.setModel(this.model);
                    if(treeGenerator.generateTree() != null) {
                        btn.setStyle("-fx-background-color: #33cc33;");
                    } else {
                        btn.setStyle("-fx-background-color: #cc3333;");
                    }
                }
            }
        }
    }

    private void startTreeGeneration() {
        //refresh all values from the input methods
        refreshModel();
        cleanProductionRulesInput();
        TreeNode root = treeGenerator.generateTree();

        //prepare the pane, so it is fresh again:
        pane.getChildren().clear();

        ableitungsfolge = new Ableitungsfolge(treeGenerator.getAbleitungsfolge(), ableitungsfolgeHBox);
        ableitungsfolge.generateAbleitungsfolge();
        treeVisualization = new TreeVisualization(pane, productionRulesVBox,ableitungsfolgeHBox);
        treeVisualization.generateTree(root);
    }

    /**
     * Empty production rule inputs should be deleted
     */
    private void cleanProductionRulesInput() {
        ArrayList<Node> copy = new ArrayList<Node>(productionRulesVBox.getChildren());

        for (Node node :
                copy) {
            if(node instanceof TextField) {
                if(((TextField) node).getText().isEmpty()) {
                    productionRulesVBox.getChildren().remove(node);
                }
            }
        }
    }

    private void refreshModel() {
        this.model.setTerminale(new ArrayList<>(List.of(terminaleInput.getText().split(","))));
        this.model.setNichtterminale(new ArrayList<>(List.of(nichtterminaleInput.getText().split(","))));
        this.model.setStartsymbol(startsymbolInput.getText());
        ArrayList<String> pr = new ArrayList<>();
        for (Node node :
                this.productionRulesVBox.getChildren()) {
            if (node instanceof TextField) {
                if(!((TextField) node).getText().isEmpty()) {
                    pr.add(((TextField) node).getText());
                }
            }
        }
        this.model.setProduktionen(pr);

        treeGenerator.setModel(this.model);
    }

    public void addTestwort(MouseEvent mouseEvent) {
        /*
         * If a textfield is already open it should close again
         */
        if(testwortHBox.getChildren().size() > 1 && testwortHBox.getChildren().get(testwortHBox.getChildren().size() - 2) instanceof TextField) {
            testwortHBox.getChildren().remove(testwortHBox.getChildren().size() - 2);
            return;
        }
        Button button = new Button();
        button.setFont(new Font("System", 18));
        button.setOnMouseClicked((mouseEvent1 -> {
            if(mouseEvent1.getButton() == MouseButton.PRIMARY) {
                model.setTestwort(button.getText());
                startTreeGeneration();
            } else if(mouseEvent1.getButton() == MouseButton.SECONDARY) {
                testwortHBox.getChildren().remove(button);
            }
        }));

        TextField txtInput = new TextField();
        txtInput.setFont(new Font("System", 18));
        txtInput.setOnAction((actionEvent) -> {
            //as soon as the text was entered make the textinput a button
            testwortHBox.getChildren().remove(testwortHBox.getChildren().size() - 2);
            button.setText(txtInput.getText());
            testwortHBox.getChildren().add(testwortHBox.getChildren().size() - 1, button);
            //set the testwort to the content of the button so it can be verified that it's in the language or not
            this.model.setTestwort(button.getText());
            //and start the checking process
            //think about if checkIfTestwordsBelongsToLanguage should have a parameter so we don't have to set the testword manually here but just temporarily in the method
            refreshModel();
            if(isModelComplete()) {
                checkIfTestwordsBelongsToLanguage();
            }
        });
        testwortHBox.getChildren().add(testwortHBox.getChildren().size() - 1, txtInput);
    }

    public void addProductionRule(MouseEvent mouseEvent) {
        TextField textField = new TextField();
        textField.setFont(new Font("System", 18));
        textField.setOnKeyPressed((keyEvent) -> {
            if(keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.E) {
                int cursorPos = textField.getSelection().getStart();
                textField.insertText(cursorPos, "ε");
            }
        });
        textField.setOnMouseClicked((mouseEvent1) -> {
            resetAllHighlightings();
        });

        this.productionRulesVBox.getChildren().add(this.productionRulesVBox.getChildren().size() - 1, textField);
    }

    private void resetAllHighlightings() {
        if(ableitungsfolgeHBox.getChildren() != null) {
            ableitungsfolgeHBox.getChildren().forEach((node) ->
                    node.setStyle("-fx-text-fill: black; -fx-font-size: 20;")
            );
        }
        if(treeVisualization != null) {
            treeVisualization.resetCircleHighlights();
        }
        if(productionRulesVBox.getChildren() != null) {
            productionRulesVBox.getChildren().forEach((node) -> {
                node.setStyle("-fx-text-fill: black; -fx-font-size: 18;");
            });
        }
    }

    public void saveProductionRules(MouseEvent mouseEvent) {
        refreshModel();
        if(isModelComplete()) {
            checkIfTestwordsBelongsToLanguage();
        }
        cleanProductionRulesInput();
        beispielwoerterGenerierung = new BeispielwoerterGenerierung(treeGenerator);
        List<String> erzeugbareWoerter = beispielwoerterGenerierung.generateWords();

        erzeugbareWoerterHBox.getChildren().clear();

        for (String wort :
                erzeugbareWoerter) {
            Button button = new Button();
            button.setFont(new Font("System", 18));
            button.setText(wort);
            button.setTextOverrun(OverrunStyle.ELLIPSIS);
            button.setOnMouseClicked((mouseEvent1 -> {
                if (mouseEvent1.getButton() == MouseButton.PRIMARY) {
                    model.setTestwort(button.getText());
                    startTreeGeneration();
                } else if (mouseEvent1.getButton() == MouseButton.SECONDARY) {
                    erzeugbareWoerterHBox.getChildren().remove(button);
                }
            }));
            erzeugbareWoerterHBox.getChildren().add(button);
        }

    }

    public void prScrollPaneOnMouseExited(MouseEvent mouseEvent) {
        cleanProductionRulesInput();
    }

    public void addWordNotInLanguage(MouseEvent mouseEvent) {
        /*
         * If a textfield is already open it should close again
         */
        if(woerterNichtInDerSpracheHBox.getChildren().size() > 1 && woerterNichtInDerSpracheHBox.getChildren().get(woerterNichtInDerSpracheHBox.getChildren().size() - 2) instanceof TextField) {
            woerterNichtInDerSpracheHBox.getChildren().remove(woerterNichtInDerSpracheHBox.getChildren().size() - 2);
            return;
        }
        Button button = new Button();
        button.setFont(new Font("System", 18));
        button.setOnMouseClicked((mouseEvent1 -> {
            if(mouseEvent1.getButton() == MouseButton.PRIMARY) {
                model.setTestwort(button.getText());
                startTreeGeneration();
            } else if(mouseEvent1.getButton() == MouseButton.SECONDARY) {
                woerterNichtInDerSpracheHBox.getChildren().remove(button);
            }
        }));

        TextField txtInput = new TextField();
        txtInput.setFont(new Font("System", 18));
        txtInput.setOnAction((actionEvent) -> {
            //as soon as the text was entered make the textinput a button
            woerterNichtInDerSpracheHBox.getChildren().remove(woerterNichtInDerSpracheHBox.getChildren().size() - 2);
            button.setText(txtInput.getText());
            woerterNichtInDerSpracheHBox.getChildren().add(woerterNichtInDerSpracheHBox.getChildren().size() - 1, button);
            //set the testwort to the content of the button so it can be verified that it's in the language or not
            this.model.setTestwort(button.getText());
            //and start the checking process
            //think about if checkIfTestwordsBelongsToLanguage should have a parameter so we don't have to set the testword manually here but just temporarily in the method
            refreshModel();
            if(isModelComplete()) {
                checkIfTestwordsBelongsToLanguage();
            }
        });
        woerterNichtInDerSpracheHBox.getChildren().add(woerterNichtInDerSpracheHBox.getChildren().size() - 1, txtInput);
    }
}