package converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser {

    private Node node;

    public Node parse(String input) {
        node = getNodeFromJson(input);
        return node;
    }

    public String getDocument() { return null == node ? "" : node.toXML(); }

    private Node getNodeFromJson(String input) {
        Matcher matcher = jsonMatcher(input);
        if (!matcher.find()) { return null; }

        Node root = new Node("root", null);
        Node next = root;
        while (matcher.find()) {
            if (matcher.group().matches("(\"[@#\\w.]*?\"\\s*:\\s*[\\[{])|([\\[{])")) {
                String key = matcher.group().matches("([\\[{])") ? "element" :
                        matcher.group().split(":")[0].replaceAll("\"", "").trim();
                Node child = new Node(key, next);
                next.addChild(child);
                next = child;
            }
            else if (matcher.group().matches("(\"[@#\\w.]*?\"\\s*:\\s*(\"[\\S\\s]*?\"|null|true|false|[\\d.]+))")) {
                String key = matcher.group().split(":")[0].replaceAll("\"", "").trim();
                String value = matcher.group().split(":")[1].trim();
                Node child = new Node(key, next);
                if (!"null".equals(value)) { child.setContent(value.replaceAll("\"", "")); }
                next.addChild(child);
            }
            else if (matcher.group().matches("[]}]")) { next = processNode(next); }
            else if (matcher.group().matches("(\"\\S*?\"|null|true|false|[\\d.]+)")) {
                Node child = new Node("element", next);
                if (!"null".equals(matcher.group())) { child.setContent(matcher.group().replaceAll("\"", "")); }
                next.addChild(child);
            }
        }
        if (root.getChildren().size() == 1) {
            root = root.getChildren().get(0);
            root.setParent(null);
        }
        return root;
    }

    private Node processNode(Node node) {
        HashMap<String, Integer> countsBefore = count(node);
        node.removeIllegalChildren();
        HashMap<String, Integer> countsAfter = count(node);

        // contains illegal attributes -> convert attributes and content to children
        if (countsAfter.get("attributes") < countsBefore.get("attributes") ||
                countsAfter.get("content") == 0 || countsBefore.get("children") > 0) {
            for (Node child : node.getChildren()) {
                child.setTag(child.getTag().replaceAll("[@#]", ""));
            }
        }
        // single XML object
        if (countsAfter.get("attributes").equals(countsBefore.get("attributes")) &&
                countsAfter.get("children").equals(countsBefore.get("children")) &&
                countsAfter.get("content").equals(countsBefore.get("content")) ) {
            List<Node> toBeRemoved = new ArrayList<>();
            List<Node> toBeAdded = new ArrayList<>();
            for (Node child : node.getChildren()) {
                if (child.getTag().matches("@.*")) {
                    node.addAttribute(child.getTag().replace("@", ""),
                            child.getContent() == null ? "" : child.getContent());
                    toBeRemoved.add(child);
                } else if (child.getTag().matches("#.*")) {
                    if (countsAfter.get("children") == 0 && child.getChildren().size() == 0) {
                        node.setContent(child.getContent());
                        toBeRemoved.add(child);
                    } else if (countsAfter.get("children") == 0 && child.getChildren().size() > 0) {
                        for (Node grandChild : child.getChildren()) {
                            grandChild.setParent(node);
                            toBeAdded.add(grandChild);
                        }
                        child.getChildren().clear();
                        toBeRemoved.add(child);
                    }
                    else {
                        child.setTag(child.getTag().replace("#", ""));
                    }
                }
            }
            for (Node child : toBeRemoved) { node.removeChild(child); }
            for (Node child : toBeAdded) { node.addChild(child); }
        }
        if (node.getChildren().size() == 0 && node.getAttributes().size() == 0 && countsAfter.get("content") == 0) {
            node.setContent("");
        }
        return node.getParent();
    }

    private HashMap<String, Integer> count(Node node) {
        HashMap<String, Integer> map = new HashMap<>();
        int countAttr = 0;
        int countChildren = 0;
        int countContent = 0;
        for (Node child : node.getChildren()) {
            if (child.getTag().matches("@\\S+")) { countAttr++; }
            else if (child.getTag().equals("#" + node.getTag())) { countContent++; }
            else { countChildren++; }
        }
        map.put("attributes", countAttr);
        map.put("children", countChildren);
        map.put("content", countContent);
        return map;
    }

    private Matcher jsonMatcher(String input) {
        return Pattern.compile(
                "(\"[@#\\w.]*?\"\\s*:\\s*[\\[{])" +
                "|(\"[@#\\w.]*?\"\\s*:\\s*(\"[\\S\\s]*?\"|null|true|false|[\\d.]+))" +
                "|([\\[\\]{}])" +
                "|(\"\\S*?\"|null|true|false|[\\d.]+)")
                .matcher(input);
    }
}
