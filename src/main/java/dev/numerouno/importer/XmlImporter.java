package dev.numerouno.importer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// IDEA: https://www.baeldung.com/java-xerces-dom-parsing

/**
 * XML-Importer-Class extends the FileImporter and specializes on parsing XML Files
 */
public class XmlImporter extends FileImporter {

    /**
     * Constructor initializes super-class-methods
     */
    public XmlImporter() {
        super();
    }

    @Override
    public void importFile(File file) throws IOException {}

    /**
     * This function acts as a starter class for the recursion-method
     *
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public void parseXml() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(super.getFile());
        doc.getDocumentElement().normalize();


        Element root = doc.getDocumentElement();
        // List<Category> categories = parseCategories(root, null);
        List<Shop> shops = parseShops(doc.getElementsByTagName("shop"));
        for (Shop shop : shops) {
            System.out.println(shop.getName());
        }

    }


    /**
     * This function parses the categories recursively
     *
     * @param element Child-Element to run the recursion on
     * @param parent  Just a placeholder for parent-attribute in Category-Object
     * @return List of all categories as objects
     */
    private static List<Category> parseCategories(Element element, Category parent) {
        List<Category> categories = new ArrayList<>();

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals("category")) {
                Element categoryElement = (Element) child;

                String name = categoryElement.getFirstChild().getNodeValue().trim();

                Category cat = new Category(name, parent);
                cat.children = parseCategories(categoryElement, cat);

                NodeList items = categoryElement.getChildNodes();
                for (int j = 0; j < items.getLength(); j++) {
                    Node item = items.item(j);
                    if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equals("item")) {
                        String asin = item.getTextContent().trim();
                        cat.items.add(new Product(asin));
                    }
                }

                categories.add(cat);

            }
        }
        return categories;
    }

    private static List<Shop> parseShops(NodeList shops) {
        List<Shop> shopsList = new ArrayList<>();
        for (int i = 0; i < shops.getLength(); i++) {
            Node shop = shops.item(i);
            if (shop.getNodeType() == Node.ELEMENT_NODE && shop.getNodeName().equals("shop")) {
                Element shopElement = (Element) shop;

                shopsList.add(new Shop(
                        shopElement.getAttribute("name"),
                        new Address(
                                shopElement.getAttribute("street"),
                                Integer.parseInt(shopElement.getAttribute("zip"))
                        )
                    )
                );
            }
        }
        return shopsList;
    }

    private static List<Product> parseProducts(Element productElement) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < productElement.getChildNodes().getLength(); i++) {
            Node item = productElement.getChildNodes().item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equals("item")) {
            }
        }

        return products;
    }
}
