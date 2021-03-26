package ir.ac.sbu.model;

import ir.ac.sbu.utility.GenerateUID;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;
import java.util.stream.Collectors;

public class GraphModel {
    private StringProperty name;
    private List<NodeModel> nodes;
    private NodeModel start;

    public GraphModel() {
    }

    public GraphModel(String name) {
        this.name = new SimpleStringProperty(name);
        nodes = new ArrayList<>();
    }

    public GraphModel createCopy(String newName) {
        GraphModel graphModel = new GraphModel(newName);
        Map<Integer, NodeModel> mapBetweenLastAndNewId = new HashMap<>();
        graphModel.nodes = new ArrayList<>();
        for (NodeModel node : nodes) {
            NodeModel nodeModel = new NodeModel(node.getX(), node.getY(), graphModel, GenerateUID.createID());
            mapBetweenLastAndNewId.put(node.getId(), nodeModel);
            nodeModel.setFinalNode(node.isFinalNode());
            nodeModel.setStartNode(node.isStartNode());
            if (node.isStartNode()) {
                graphModel.start = nodeModel;
            }
            graphModel.nodes.add(nodeModel);
        }

        for (NodeModel node : nodes) {
            for (EdgeModel edgeModel : node.getAdjacentList()) {
                NodeModel startNode = mapBetweenLastAndNewId.get(edgeModel.getStart().getId());
                EdgeModel newEdgeModel = new EdgeModel(startNode, mapBetweenLastAndNewId.get(edgeModel.getEnd().getId()));
                newEdgeModel.setToken(edgeModel.getToken());
                newEdgeModel.setFunction(edgeModel.getFunction());
                newEdgeModel.setAnchorX(edgeModel.getAnchorX());
                newEdgeModel.setAnchorY(edgeModel.getAnchorY());
                newEdgeModel.setGraph(edgeModel.isGraph());
                newEdgeModel.setGlobal(edgeModel.isGlobal());
                startNode.getAdjacentList().add(newEdgeModel);
            }
        }
        return graphModel;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setNodes(List<NodeModel> nodes) {
        this.nodes = nodes;
    }

    public NodeModel getStart() {
        return start;
    }

    public void setStart(NodeModel start) {
        if (this.start != null)
            this.start.setStartNode(false);
        this.start = start;
        start.setStartNode(true);
    }

    public List<NodeModel> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    public List<EdgeModel> getEdges() {
        return nodes.stream().map(NodeModel::getAdjacentList).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
