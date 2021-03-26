package ir.ac.sbu.controller;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import ir.ac.sbu.command.CommandManager;
import ir.ac.sbu.command.MakeEdgeCmd;
import ir.ac.sbu.command.MakeNodeCmd;
import ir.ac.sbu.graphics.BoundLine;
import ir.ac.sbu.graphics.GraphNode;
import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;

public class DrawPaneController implements RefreshableController {
    private final Pane pane;
    private GraphModel graph;
    private NodeModel firstNode = null;

    public DrawPaneController(Pane pane) {
        this.pane = pane;
        pane.setOnMouseReleased(this::mouseClickEvent);
    }

    public void refresh() {
        if (graph == null) {
            return; // Not initialized yet.
        }
        pane.getChildren().clear();

        for (NodeModel nodeModel : graph.getNodes()) {
            for (EdgeModel edge : nodeModel.getAdjacentList()) {
                BoundLine line = new BoundLine(edge);
                pane.getChildren().addAll(line, line.getAnchor(), line.getText(), line.getArrowEnd());
                line.toBack();
            }
        }

        for (NodeModel nodeModel : graph.getNodes()) {
            GraphNode graphNode = new GraphNode(nodeModel);
            graphNode.setOnShiftClick(this::onShiftClick);
            pane.getChildren().add(graphNode);
        }
    }

    private void onShiftClick(MouseEvent mouseEvent) {
        NodeModel node = ((GraphNode) mouseEvent.getSource()).getNodeModel();
        firstNode = node;
        if (firstNode != null) {
            CommandManager.getInstance().applyCommand(new MakeEdgeCmd(firstNode, node));
        }
    }

    private void mouseClickEvent(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            CommandManager.getInstance().applyCommand(new MakeNodeCmd(graph, mouseEvent.getX(), mouseEvent.getY()));
        }
    }

    public void setGraph(GraphModel graph) {
        this.graph = graph;
    }

    public GraphModel getGraph() {
        return graph;
    }

    public void setFirstNode(NodeModel firstNode) {
        this.firstNode = firstNode;
    }
}
