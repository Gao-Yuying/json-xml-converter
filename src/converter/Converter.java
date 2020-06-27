package converter;

class Converter {

    private final XmlParser xmlParser = new XmlParser();
    private final JsonParser jsonParser = new JsonParser();
    private Node node;
    private String document;

    public void convert(String input) {
        if (isXml(input)) {
            node = xmlParser.parse(input);
            document = xmlParser.getDocument();
        }
        else if (isJson(input)) {
            node = jsonParser.parse(input);
            document = jsonParser.getDocument();
        }
        else { System.out.println("Invalid form of XML/JSON!"); }
    }

    public String getDocument() { return document; }

    public String getNodeInfo() { return node == null ? "" : node.getNodeInfo(); }

    private Boolean isXml(String input) { return input.matches("\\s*<.*>\\s*"); }

    private Boolean isJson(String input) { return input.matches("\\s*\\{.*}\\s*"); }
}
