package de.ps1ttacus.SyntaxTree;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

public class TreeVisualization {
    private Pane pane;

    private double baseX;
    private double baseY;
    private double width;
    private double height;
    private double deltaX, deltaY;

    public VBox productionRulesInput;
    private HBox ableitungsfolge;

    public TreeVisualization(Pane pane, VBox productionRulesInput, HBox ableitungsfolge) {
        this.pane = pane;
        this.productionRulesInput = productionRulesInput;
        this.ableitungsfolge = ableitungsfolge;

        Bounds boundsInScene = pane.localToScene(pane.getBoundsInLocal());
        width = boundsInScene.getMaxX() - boundsInScene.getMinX();
        height = boundsInScene.getMaxY() - boundsInScene.getMinY();
        baseX = width / 2;
        baseY = 30;
    }


    private void initPaneWithActionListeners() {
        pane.setOnScroll((scrollEvent -> {
            if (scrollEvent.isControlDown()) {
                double scale = 0;
                if (scrollEvent.getDeltaY() > 0) {
                    scale = pane.getScaleX() * 0.10;
                } else {
                    scale = -pane.getScaleX() * 0.10;
                }
                pane.setScaleX(pane.getScaleX() + scale);
                pane.setScaleY(pane.getScaleY() + scale);
            }
        }));
        pane.setOnMousePressed((mouseEvent -> {
            this.deltaX = pane.getChildren().get(0).getTranslateX() - mouseEvent.getSceneX();
            this.deltaY = pane.getChildren().get(0).getTranslateY() - mouseEvent.getSceneY();
            pane.setCursor(Cursor.MOVE);
        }));
        pane.setOnMouseReleased((mouseEvent -> {
            pane.setCursor(Cursor.HAND);
        }));
        pane.setOnMouseDragged((mouseEvent -> {
            for (Node child : pane.getChildren()) {
                child.setTranslateX(mouseEvent.getSceneX() + this.deltaX);
                child.setTranslateY(mouseEvent.getSceneY() + this.deltaY);
            }
        }));
        pane.setOnMouseEntered((mouseEvent -> {
            pane.setCursor(Cursor.HAND);
        }));
    }


    public void generateTree(TreeNode root) {
        initPaneWithActionListeners();
        root.requiredWidth();
        drawTree(root, baseX, baseY);
    }

    private void highlightProductionRule(TreeNode root) {
        String leftSide = root.getProductionRule().keySet().iterator().next();
        String rightSide = root.getProductionRule().get(leftSide);

        for (Node node :
                productionRulesInput.getChildren()) {
            if (node instanceof TextField) {
                if (((TextField) node).getText().substring(0, ((TextField) node).getText().indexOf("->")).strip().equals(leftSide) && ((TextField) node).getText().substring(((TextField) node).getText().indexOf("->")).strip().contains(rightSide)) {
                    ((TextField) node).setStyle("-fx-text-fill: red; -fx-font-size: 22;");
                } else {
                    ((TextField) node).setStyle("-fx-text-fill: black; -fx-font-size: 18;");
                }
            }
        }
    }

    public void resetCircleHighlights() {
        for (Node node :
                this.pane.getChildren()) {
            if (node instanceof Circle) {
                ((Circle) node).setFill(Color.GRAY);
            }
        }
    }

    private void resetHighlightsProductionRule() {
        for (Node node :
                productionRulesInput.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).setStyle("-fx-text-fill: black; -fx-font-size: 18;");
            }
        }
    }

    private void highlightAbleitungsteilwort(String ableitungsteilwort) {
        for (Node node : ableitungsfolge.getChildren()) {
            if (!((Label) node).getText().equals("=>")) {
                if (((Label) node).getText().equals(ableitungsteilwort)) {
                    node.setStyle("-fx-text-fill: red; -fx-font-size: 22;");
                }
            }
        }
    }

    private void resetHighlightAbleitungsteilwort() {
        ableitungsfolge.getChildren().forEach(node -> {
            node.setStyle("-fx-text-fill: black; -fx-font-size: 20;");
        });
    }

    private void drawLine(double startX, double startY, double endX, double endY, double circleRadius) {
        Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY+circleRadius);
        line.setEndX(endX);
        line.setEndY(endY-circleRadius);
        line.setStyle("-fx-stroke-width: 2;");
        pane.getChildren().add(line);
    }

    private void drawTree(TreeNode root, double x, double y) {
        double circleRadius = 25;
        //if root is null something silly happened. should never be the case
        if (root == null) {
            return;
        }
        //if it's a leaf just draw it because no children or space has to be handled
        if (!root.isLeaf(root)) {
            drawChildren(root, x, y, circleRadius);
        }

        drawCircle(root, x, y, circleRadius);
    }

    private void drawChildren(TreeNode root, double x, double y, double circleRadius) {
        double tempX = x;
        if (root.getChildren().size() != 1) {
            int calculatedWidth = 0;
            for(int i = 0; i < root.getChildren().size(); i++) {
                if (i + 1 < root.getChildren().size()) {
                    calculatedWidth += (root.getChildren().get(i).total + root.getChildren().get(i + 1).total) / 2;
                }
            }
            tempX -= (calculatedWidth * circleRadius * 4) / 2;
        }
        double toAdd = 0;
        double nextX = 0;
        double nextY = y + circleRadius * 4;
        for (int i = 0; i < root.getChildren().size(); i++) {
            nextX = tempX + toAdd * circleRadius * 4;

            drawLine(x, y,nextX, nextY, circleRadius);
            drawTree(root.getChildren().get(i), nextX, y + circleRadius * 4);

            if (i + 1 < root.getChildren().size()) {
                toAdd += (root.getChildren().get(i).total + root.getChildren().get(i + 1).total) / 2;
            }
        }
    }


    private void drawCircle(TreeNode root, double x, double y, double circleRadius) {
        /* draw the root circle
         * at first call this is the starting symbol circle
         */
        Circle c = new Circle();
        c.relocate(x, y);
        c.setRadius(circleRadius);
        c.setFill(Color.GRAY);
        c.setOnMouseClicked((mouseEvent -> {
            if (c.getFill() == Color.RED) {
                c.setFill(Color.GRAY);
                resetHighlightsProductionRule();
                resetHighlightAbleitungsteilwort();
            } else {
                resetHighlightAbleitungsteilwort();
                resetCircleHighlights();
                highlightProductionRule(root);
                highlightAbleitungsteilwort(root.getAbleitungsfolgeTeilwort());
                c.setFill(Color.RED);
            }
        }));

        Label label = new Label(root.getName());
        label.setFont(new Font("System", 18));
        label.relocate(x - 5, y - 10);
        label.setOnMouseClicked((mouseEvent -> {
            if (c.getFill() == Color.RED) {
                c.setFill(Color.GRAY);
                resetHighlightsProductionRule();
                resetHighlightAbleitungsteilwort();
            } else {
                resetHighlightAbleitungsteilwort();
                resetCircleHighlights();
                highlightProductionRule(root);
                highlightAbleitungsteilwort(root.getAbleitungsfolgeTeilwort());
                c.setFill(Color.RED);
            }
        }));

        pane.getChildren().addAll(c, label);
    }

    public VBox getProductionRulesInput() {
        return productionRulesInput;
    }

    public void setProductionRulesInput(VBox productionRulesInput) {
        this.productionRulesInput = productionRulesInput;
    }
}
