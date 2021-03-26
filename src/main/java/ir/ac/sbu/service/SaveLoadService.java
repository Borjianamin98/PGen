package ir.ac.sbu.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.ac.sbu.model.GraphModel;
import ir.ac.sbu.model.NodeModel;
import ir.ac.sbu.service.jsonmodel.GraphJSON;
import ir.ac.sbu.utility.DialogUtility;
import ir.ac.sbu.utility.GenerateUID;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SaveLoadService {

    public static void save(List<GraphModel> graphs, File file) {
        List<GraphJSON> graphJSONs = graphs.stream().map(GraphJSON::new).collect(Collectors.toList());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String out = gson.toJson(graphJSONs);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(out);
        } catch (FileNotFoundException e) {
            DialogUtility.showErrorDialog(e.getMessage());
        }

    }

    public static List<GraphModel> load(File file) throws FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<GraphModel> graphs = Arrays.stream(gson.fromJson(new FileReader(file), GraphJSON[].class))
                .map(GraphJSON::toGraphModel)
                .collect(Collectors.toList());

        GenerateUID.setIdCounter(graphs.stream().
                flatMap(graphModel -> graphModel.getNodes().stream())
                .mapToInt(NodeModel::getId).max().orElse(0) + 1);

        return graphs;
    }
}
