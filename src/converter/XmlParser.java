package converter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser {

    private Node node;

    public Node parse(String input) {
        node = getNodeFromXML(input);
        return node;
    }

    public String getDocument() { return null == node ? "" : node.toJSON(); }

    private Node getNodeFromXML(String input) {
        String text = input;
        Matcher matcher = xmlMatcher(text);
        if (!matcher.find()) { return null; }

        Node root = makeRoot(getTag(matcher.group()));
        Node next = root;
        Node child;

        while (matcher.find()) {
            // enclosed element
            if (matcher.group().matches("<.*?/>")) {
                child = new Node(getTag(matcher.group()), next);
                child.setAttributes(getAttributes(matcher.group()));
                next.addChild(child);
                text = text.replaceFirst("<" + child.getTag() + "[\\s\\S]*?/>", "");
            }
            // unclosed tag
            else if (matcher.group().matches("<[^/][\\s\\S]*?[^/]>")) {
                child = new Node(getTag(matcher.group()), next);
                child.setAttributes(getAttributes(matcher.group()));
                next.addChild(child);
                next = child;
            }
            // closed tag
            else {
                HashMap<String, String> map = consumeTag(next.getTag(), text);
                next.setContent(map.get("content"));
                text = map.getOrDefault("text", text);
                if (next.getChildren().size() > 0) { next.setContent(null); }
                next = next.getParent();
            }
        }
        return root;
    }

    private Node makeRoot(String rootTag) {
        Node root = new Node(rootTag, null);
        root.setAttributes(getAttributes(rootTag));
        return root;
    }

    private String getTag (String tagContent){
        Matcher matcher = Pattern.compile("(?<=<)\\w+").matcher(tagContent);
        if (matcher.find()) { return matcher.group(); }
        return null;
    }

    private LinkedHashMap<String, String> getAttributes (String tagContent){
        Matcher matcher = Pattern.compile("\\w+\\s*=\\s*(['\"][\\s\\S]*?['\"])").matcher(tagContent);
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        while (matcher.find()) {
            attributes.put(matcher.group().split("=")[0].trim(),
                    matcher.group().split("=")[1].replaceAll("['\"]", "").trim());
        }
        return attributes;
    }

    private HashMap<String, String> consumeTag (String tag, String text){
        HashMap<String, String> map = new HashMap<>();
        String regex = String.format("\\s*<%s(\\s+.*?)?>([\\s]|[^<>])*?<\\/%s>\\s*", tag, tag);
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            map.put("text", (text.substring(0, matcher.start()) + text.substring(matcher.end())).trim());
            map.put("content", matcher.group().replaceAll("<[\\s\\S]*?>", "").trim());
        }
        return map;
    }

    private Matcher xmlMatcher(String text) { return Pattern.compile("(<[^?][\\s\\S]*?>)").matcher(text); }
}
