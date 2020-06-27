package converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class Node {
    private String tag;
    private String content;
    private Node parent;
    final private List<Node> children;
    private LinkedHashMap<String, String> attributes;

    public Node(String tag, Node parent) {
        this.tag = tag;
        content = null;
        this.parent = parent;
        children = new ArrayList<>();
        attributes = new LinkedHashMap<>();
    }

    public void setTag(String tag) { this.tag = tag; }

    public String getTag() { return tag; }

    public void setContent(String content) { this.content = content; }

    public String getContent() { return content; }

    public void setParent(Node node) { parent = node; }

    public Node getParent() { return parent; }

    public List<Node> getChildren() { return children; }

    public void addChild(Node child) { children.add(child); }

    public void removeChild(Node child) { children.remove(child); }

    public void removeIllegalChildren() {
        List<Node> illegalChildren = new ArrayList<>();
        for (Node child : children) {
            String key = child.tag.replaceAll("\"", "");
            if (key.matches("[@#]") || key.length() == 0) { illegalChildren.add(child); }
            else if (key.matches("[@#].*")) {
                for (Node node :children) {
                    if (node.getTag().equals(key.replaceFirst("[@#]", ""))) { illegalChildren.add(child); }
                }
                if (key.matches("[@].*") && child.getChildren().size() > 0) { child.setTag(key.replaceFirst("@", "")); }
            }
        }
        for (Node child :illegalChildren) { removeChild(child); }
    }

    public LinkedHashMap<String, String> getAttributes() { return attributes; }

    public void setAttributes(LinkedHashMap<String, String> attributes) { this.attributes = attributes; }

    public void addAttribute(String k, String v) { attributes.put(k, v); }

    public String toJSON() { return "{\n" + getJSON(this, new StringBuilder(), 0).toString() + "\n}"; }

    public String toXML() { return getXML(this, new StringBuilder(), 0).toString(); }

    public String getNodeInfo() { return getNodeInfo(this, new StringBuilder()).toString(); }

    public StringBuilder getNodeInfo(Node node, StringBuilder s) {
        s.append(node.parent == null ? "" : "\n").append("Element:\n");
        s.append(String.format("path = %s\n", getPath(node)));
        s.append(node.children.size() > 0 ? "" :
                node.content == null ? "value = null\n" : String.format("value = \"%s\"\n", node.content));
        if (node.attributes.size() > 0) {
            s.append("attributes:\n");
            for (String k : node.attributes.keySet()) {
                s.append(String.format("%s = \"%s\"\n", k, node.attributes.get(k)));
            }
        }
        for (Node child : node.children) { s = getNodeInfo(child, s); }
        return s;
    }

    private StringBuilder getJSON(Node node, StringBuilder s, int indent) {
        // simple k-v
        if (node.children.size() == 0 && node.attributes.size() == 0) {
            return s.append(" ".repeat((indent + 1) * 2))
                    .append(isArray(node.parent) ? "" : "\"" + node.tag + "\" : ")
                    .append(node.content == null ? "null" : "\"" + node.content + "\"");
        }
        indent++;
        s.append(" ".repeat(indent * 2))
                .append(isArray(node.parent) ? "" : "\"" + node.tag + "\" : ")
                .append(isArray(node) && node.attributes.size() == 0 ? "[" : "{")
                .append("\n");
        // add attribute and content
        indent++;
        if (node.attributes.size() > 0) {
            for (String k : node.attributes.keySet()) {
                s.append(" ".repeat(indent * 2))
                        .append(String.format("\"@%s\" : \"%s\",\n", k, node.attributes.get(k)));
            }
            if (node.children.size() == 0) {
                s.append(" ".repeat(indent * 2))
                        .append(String.format("\"#%s\" : %s\n",
                                node.tag,
                                node.content == null ?"null" : "\"" + node.content + "\""));
            }
        }
        // add children
        if (node.children.size() > 0) {
            if (node.attributes.size() > 0) {
                s.append(" ".repeat(indent * 2))
                        .append(String.format("\"#%s\" : ", node.tag))
                        .append(isArray(node) ? "[\n" : "{\n");
                indent++;

                int childrenLeft = node.children.size();
                for (Node child : node.children) {
                    s = getJSON(child, s, indent).append((--childrenLeft == 0 ? "\n" : ",\n"));
                }

                indent--;
                s.append(" ".repeat(indent * 2)).append(isArray(node) ? "]\n" : "}\n");
            } else {
                int childrenLeft = node.children.size();
                for (Node child : node.children) {
                    s = getJSON(child, s, indent).append((--childrenLeft == 0 ? "\n" : ",\n"));
                }
            }
        }
        indent--;
        s.append(" ".repeat(indent * 2)).append(isArray(node) && node.attributes.size() == 0 ? "]" : "}");
        return s;
    }

    private StringBuilder getXML(Node node, StringBuilder s, int indent) {
        StringBuilder attributes = new StringBuilder();
        for (String key : node.attributes.keySet()) {
            attributes.append(String.format(" %s=\"%s\"", key, node.attributes.get(key)));
        }
        // enclosed element
        if (node.children.size() == 0 && node.content == null) {
            s.append(" ".repeat(indent * 2)).append(String.format("<%s%s />\n", node.tag, attributes));
        }
        // no children
        else if (node.children.size() == 0) {
            s.append(" ".repeat(indent * 2))
                    .append(String.format("<%s%s>%s</%s>\n", node.tag, attributes, node.content, node.tag));
        }
        // have children
        else {
            s.append(" ".repeat(indent * 2)).append(String.format("<%s%s>\n", node.tag, attributes));
            indent++;
            for (Node child : node.children) { s = getXML(child, s, indent); }
            indent--;
            s.append(" ".repeat(indent * 2)).append(String.format("</%s>\n", node.tag));
        }
        return s;
    }


    private String getPath(Node node) {
        StringBuilder path = new StringBuilder(node.tag);
        Node next = node;
        while (next.parent != null && !"".equals(next.parent.tag)) {
            path.insert(0, next.parent.tag + ", ");
            next = next.parent;
        }
        return path.toString();
    }

    private boolean isArray(Node node) {
        if (node == null || node.children.size() < 2) { return false; }
        String tag = node.children.get(0).tag;
        if (tag.equals("#" + node.tag)) { return false; }
        for (Node child : node.children) { if (!tag.equals(child.tag)) { return false; } }
        return true;
    }
}