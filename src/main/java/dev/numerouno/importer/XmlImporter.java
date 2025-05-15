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
    public void importFile(File file) throws IOException {

    }

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
        List<Category> categories = parseCategories(root, null);

        for (Category category : categories) {
            System.out.println(category.getName());
            System.out.println(category.items.toString());
            for (Category categ : category.children) {
                System.out.println(categ.getName());
                System.out.println(categ.items.toString());
            }
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
                        String id = item.getTextContent().trim();
                        cat.items.add(new CatItem(id));
                    }
                }

                categories.add(cat);

            }
        }
        return categories;
    }
}
