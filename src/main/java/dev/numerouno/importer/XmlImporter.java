package dev.numerouno.importer;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

// IDEA: https://www.baeldung.com/java-xerces-dom-parsing

public class XmlImporter extends FileImporter{
    public XmlImporter() {
        super();
    }

    public void parseXml(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(file);

        String item = doc.getElementsByTagName("category").item(0).getTextContent();
        System.out.println(item);
    }
}
