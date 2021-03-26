package ir.ac.sbu.utility;

public class CheckUtility {
    public static String DELIMITER = ",";

    private CheckUtility() {
    }

    public static void checkGraphName(String graphName) {
        if (graphName.trim().isEmpty()) {
            throw new IllegalArgumentException("Graph name can not be empty.");
        } else if (graphName.contains(DELIMITER)) {
            throw new IllegalArgumentException("Graph can not contain '" + DELIMITER + "' character.");
        } else if (graphName.contains(" ")) {
            throw new IllegalArgumentException("Graph can not contain ' ' character.");
        }
    }

    public static void checkTokenName(String tokenName) {
        if (tokenName.startsWith("$")) {
            throw new IllegalArgumentException("All string starting with $ are predefined tokens.");
        } else if (tokenName.trim().isEmpty()) {
            throw new IllegalArgumentException("Token can not be empty.");
        } else if (tokenName.contains(DELIMITER)) {
            throw new IllegalArgumentException("Token can not contain '" + DELIMITER + "' character.");
        }
    }

    public static void checkFunctionName(String functionName) {
        if (functionName.contains(DELIMITER)) {
            throw new IllegalArgumentException("Token can not contain '" + DELIMITER + "' character.");
        } else if (functionName.contains(" ")) {
            throw new IllegalArgumentException("Token can not contain ' ' character.");
        }
    }
}
