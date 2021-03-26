package ir.ac.sbu.service.jsonmodel;

import ir.ac.sbu.model.EdgeModel;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphJSON {

    private String name;
    private List<NodeJSON> nodes;
    private List<EdgeJSON> edges;
    private int start = -1;

    public GraphJSON() {
    }

    public GraphJSON(GraphModel g) {
        name = g.getName();
        nodes = g.getNodes().stream().map(NodeJSON::new).collect(Collectors.toList());
        edges = g.getEdges().stream().map(EdgeJSON::new).collect(Collectors.toList());
        if (g.getStart() != null) {
            start = g.getStart().getId();
        }
    }

    public GraphModel toGraphModel() {
        GraphModel graphModel = new GraphModel(name);
        Map<Integer, NodeModel> graphNodes = nodes.stream()
                .collect(Collectors.toMap(NodeJSON::getId, node -> node.toNodeModel(graphModel)));
        graphModel.getNodes().addAll(graphNodes.values());
        if (start != -1) {
            graphModel.getNodes().stream()
                    .filter(nodeJSON -> nodeJSON.getId() == start)
                    .findFirst().ifPresent(graphModel::setStart);
        }

        edges.forEach(edgeJSON ->
        {
            NodeModel start = graphNodes.get(edgeJSON.getStart());
            NodeModel end = graphNodes.get(edgeJSON.getEnd());
            EdgeModel edge = new EdgeModel(start, end);
            edge.setFunction(edgeJSON.getFunc());
            edge.setToken(edgeJSON.getToken());
            edge.anchorXProperty().setValue(edgeJSON.getAnchorX());
            edge.anchorYProperty().setValue(edgeJSON.getAnchorY());
            edge.setGraph(edgeJSON.isGraph());
            edge.setGlobal(edgeJSON.isGlobal());
            start.getAdjacentList().add(edge);
        });
        return graphModel;
    }

    public String getName() {
        return name;
    }

    public List<NodeJSON> getNodes() {
        return nodes;
    }

    public List<EdgeJSON> getEdges() {
        return edges;
    }

    public int getStart() {
        return start;
    }
}
