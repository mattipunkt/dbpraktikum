package dev.numerouno.importer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;

// IDEA: https://www.baeldung.com/java-xerces-dom-parsing

/**
 * XML-Importer-Class extends the FileImporter and specializes on parsing XML Files
 */
public class XmlImporter extends FileImporter {

    private static final Logger LOGGER = LogManager.getLogger(XmlImporter.class);

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
     * @throws ParserConfigurationException Configuration from Parser
     * @throws IOException File not Found
     * @throws SAXException Encapsulate a general SAX error or warning.
     */
    public void parseXml() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(super.getFile());
        doc.getDocumentElement().normalize();


        // Element root = doc.getDocumentElement();
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

                Shop shopObj = new Shop(shopElement.getAttribute("name"),
                        new Address(
                                shopElement.getAttribute("street"),
                                Integer.parseInt(shopElement.getAttribute("zip"))
                        )
                );
                shopObj.setProductList(parseProducts(shopElement));
                shopsList.add(shopObj);

            }
        }
        return shopsList;
    }

    private static List<Product> parseProducts(Element productElement) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < productElement.getChildNodes().getLength(); i++) {
            Node item = productElement.getChildNodes().item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equals("item")) {
                Element itemElement = (Element) item;
                String type = itemElement.getAttribute("pgroup");
                switch (type) {
                    case "Book" -> products.add(parseBook(itemElement));
                    case "DVD" -> products.add(parseDVDs(itemElement));
                    case "Music" -> products.add(parseCDs(itemElement));
                }
            }
        }
        for (Product product : products) {
            if (product != null) {
                System.out.println(product);
            }
        }

        return products;
    }

    private static CD parseCDs(Element cdElement) {
        CD cd = new CD(cdElement.getAttribute("asin"));
        // Rank
        cd.setRank(parseRank(cdElement));
        // EAN
        cd.setEan(parseEan(cdElement));
        // Image
        cd.setImage(parseImage(cdElement));
        // Titel
        cd.setName(parseTitle(cdElement));
        // Release Date
        cd.setDate(parseReleaseDate(cdElement));
        // Artists
        cd.setArtists(parsePeople(cdElement));
        // Label
        cd.setLabel(parseLabels(cdElement));
        // Titles
        cd.setTitles(parseTitles(cdElement));
        // Similars
        cd.setSimilarProducts(parseSimilars(cdElement));
        // Price
        cd.setPrice(parsePrice(cdElement));
        // Condition
        cd.setCondition(parseCondition(cdElement));
        return cd;
    }

    private static Book parseBook(Element bookElement) {
        Book book = new Book(bookElement.getAttribute("asin"));
        book.setName(parseTitle(bookElement));
        book.setRank(parseRank(bookElement));
        book.setEan(parseEan(bookElement));
        book.setImage(parseImage(bookElement));
        book.setSimilarProducts(parseSimilars(bookElement));
        book.setCondition(parseCondition(bookElement));
        book.setPrice(parsePrice(bookElement));
        book.setVerlag(parsePublishers(bookElement));
        book.setPages(parsePages(bookElement));
        book.setDate(parsePublicationDate(bookElement));
        book.setIsbn(parseIsbn(bookElement));
        book.setAuthors(parsePeople(bookElement));
        book.setAudiobook(parseAudiobook(bookElement));
        return book;
    }

    private static DVD parseDVDs(Element dvdElement) {
        DVD dvd = new DVD(dvdElement.getAttribute("asin"));
        dvd.setPrice(parsePrice(dvdElement));
        dvd.setName(parseTitle(dvdElement));
        dvd.setRank(parseRank(dvdElement));
        dvd.setImage(parseImage(dvdElement));
        dvd.setSimilarProducts(parseSimilars(dvdElement));
        dvd.setCondition(parseCondition(dvdElement));
        dvd.setPrice(parsePrice(dvdElement));
        dvd.setEan(parseEan(dvdElement));
        dvd.setFormat(parseFormat(dvdElement));
        dvd.setRuntime(parseRuntime(dvdElement));
        dvd.setRegioncode(parseRegionCode(dvdElement));
        return dvd;
    }


    private static int parseRank(Element element) {
        String rank = element.getAttribute("salesrank");
        if (!rank.equals("null")) {
            if (!rank.isBlank()) {
                return Integer.parseInt(rank);
            }
        }
        return -1;
    }

    private static String parseEan(Element element) {
        String ean = null;
        try {
            ean = requireNonBlank(getTagValue("ean", element));
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Could not parse ean as Tag");
        }
        try {
            ean = requireNonBlank(element.getAttribute("ean"));
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Could not parse ean as Attribute");
        }
        return ean;
    }

    private static String parseImage(Element element) {
        String url = null;
        try {
            String url_tmp = ((Element) element.getElementsByTagName("details").item(0)).getAttribute("img");
            if (!url_tmp.isBlank()) {
                url = url_tmp;
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Image not found as tag for object {}", element);
        }
        try {
            String url_tmp = element.getAttribute("picture");
            if (!url_tmp.isBlank()) {
                url = url_tmp;
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Image not found as attribute for object {}", element);
        }
        return url;
    }

    private static String parseTitle(Element element) {
        try {
            return requireNonBlank(getTagValue("title", element));
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Title not found as tag for object {}", element);
        }
        return null;
    }

    private static String parseReleaseDate(Element element) {
        NodeList musicspec = element.getElementsByTagName("musicspec");
        for (int i = 0; i < musicspec.getLength(); i++) {
            Element musicSpecElement = (Element) musicspec.item(i);
            try {
                return requireNonBlank(getTagValue("releasedate", musicSpecElement));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Release date not found as tag for object {}", musicSpecElement);
            }

        }
        return null;
    }

    private static List<Person> parsePeople(Element element) {
        List<Person> people = new ArrayList<>();
        NodeList artists = element.getElementsByTagName("artists");
        for (int i = 0; i < artists.getLength(); i++) {
            Element artistElement = (Element) artists.item(i);
            try {
                people.add(new Person(requireNonBlank(getTagValue("artist", artistElement)), "artist"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Artist name not found as tag for object {}", artistElement);
            }
            try {
                people.add(new Person(requireNonBlank(artistElement.getAttribute("name")), "artist"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Artist name not found as Attribute for object {}", artistElement);
            }
        }
        NodeList creators = element.getElementsByTagName("creators");
        for (int i = 0; i < creators.getLength(); i++) {
            Element creatorElement = (Element) creators.item(i);
            try {
                people.add(new Person(requireNonBlank(getTagValue("creator", creatorElement)), "creator"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Creator name not found as tag for object {}", creatorElement);
            }
            try {
                people.add(new Person(requireNonBlank(creatorElement.getAttribute("name")), "creator"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Creator name not found as Attribute for object {}", creatorElement);
            }
        }
        NodeList actors = element.getElementsByTagName("actors");
        for (int i = 0; i < actors.getLength(); i++) {
            Element actorElement = (Element) actors.item(i);
            try {
                people.add(new Person(requireNonBlank(getTagValue("actor", actorElement)), "actor"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Actor name not found as tag for object {}", actorElement);
            }
            try {
                people.add(new Person(requireNonBlank(actorElement.getAttribute("name")), "actor"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Actor name not found as Attribute for object {}", actorElement);
            }
        }
        NodeList authors = element.getElementsByTagName("authors");
        for (int i = 0; i < authors.getLength(); i++) {
            Element authorElement = (Element) authors.item(i);
            try {
                people.add(new Person(requireNonBlank(getTagValue("author", authorElement)), "author"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Author name not found as tag for object {}", authorElement);
            }
            try {
                people.add(new Person(requireNonBlank(authorElement.getAttribute("name")), "author"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Author name not found as Attribute for object {}", authorElement);
            }        }
        NodeList directors = element.getElementsByTagName("directors");
        for (int i = 0; i < directors.getLength(); i++) {
            Element directorElement = (Element) directors.item(i);
            try {
                people.add(new Person(requireNonBlank(getTagValue("director", directorElement)), "director"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Director name not found as tag for object {}", directorElement);
            }
            try {
                people.add(new Person(requireNonBlank(directorElement.getAttribute("name")), "director"));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Director name not found as Attribute for object {}", directorElement);
            }
        }

        return people;
    }

    private static List<String> parseLabels(Element element) {
        NodeList labels = element.getElementsByTagName("labels");
        List<String> labelsList = new ArrayList<>();
        for (int i = 0; i < labels.getLength(); i++) {
            Element labelElement = (Element) labels.item(i);
            try {
                labelsList.add(requireNonBlank(getTagValue("label", labelElement)));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Label name not found as tag for object {}", labelElement);
            }
            try {
                labelsList.add(requireNonBlank(labelElement.getAttribute("name")));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Label name not found as Attribute for object {}", labelElement);
            }
        }
        return labelsList;
    }


    private static List<MusicTitle> parseTitles(Element element) {
        NodeList titles = element.getElementsByTagName("tracks");
        List<MusicTitle> titlesList = new ArrayList<>();
        for (int i = 0; i < titles.getLength(); i++) {
            Element titleElement = (Element) titles.item(i);
            titlesList.add(new MusicTitle(getTagValue("title", titleElement)));
        }
        return titlesList;
    }

    private static List<Product> parseSimilars(Element element) {
        Node similars = element.getElementsByTagName("similars").item(0);
        NodeList similarElements = ((Element) similars).getElementsByTagName("sim_product");
        List<Product> similarsList = new ArrayList<>();
        for (int i = 0; i < similarElements.getLength(); i++) {
            Element similarElement = (Element) similarElements.item(i);
            try {
                System.out.println(getTagValue("asin", similarElement));
                similarsList.add(new Product(requireNonBlank(requireNonBlank(getTagValue("asin", similarElement)))));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Similars tag found, but could not read ASIN from Sub-Tag");
            }
        }
        NodeList similarItems = ((Element) similars).getElementsByTagName("item");
        for (int i = 0; i < similarItems.getLength(); i++) {
            Element similarElement = (Element) similarItems.item(i);
            try {
                System.out.println(similarElement.getAttribute("asin"));
                similarsList.add(new Product(requireNonBlank(similarElement.getAttribute("asin"))));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Similars tag found, but could not read ASIN from Attribute");
            }
        }

        return similarsList;
    }

    private static String parseCondition(Element element) {
        Node price = element.getElementsByTagName("price").item(0);
        Element priceElement = (Element) price;
        try {
            return priceElement.getAttribute("state");
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Price tag found, but could not read state from Attribute");
        }
        return null;
    }

    private static double parsePrice(Element element) {
        Node price = element.getElementsByTagName("price").item(0);
        Element priceElement = (Element) price;

        try {
            double priceValue = Double.parseDouble(requireNonBlank(getTagValue("price", element)));
            return priceValue * Double.parseDouble(priceElement.getAttribute("mult"));
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.WARN, "No valid price provided: ");
        }
        return -1.0;
    }

    private static String parseFormat(Element element) {
        NodeList dvdspec = element.getElementsByTagName("dvdspec");
        for (int i = 0; i < dvdspec.getLength(); i++) {
            Element musicSpecElement = (Element) dvdspec.item(i);
            try {
                return requireNonBlank(getTagValue("format", musicSpecElement));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Format tag found, but could not read format from Attribute");
            }
        }
        return null;
    }

    private static int parseRegionCode(Element element) {
        Node dvdspec = element.getElementsByTagName("dvdspec").item(0);
        Element dvdspecElement = (Element) dvdspec;
        try {
            return Integer.parseInt(requireNonBlank(getTagValue("regioncode", dvdspecElement)));
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.warn("No valid region-code provided", e);
        }
        return -1;
    }

    private static int parseRuntime(Element element) {
        Node dvdspec = element.getElementsByTagName("dvdspec").item(0);
        Element dvdspecElement = (Element) dvdspec;
        try {
            return Integer.parseInt(requireNonBlank(getTagValue("runningtime", dvdspecElement)));
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.warn("No valid runtime provided", e);
        }
        return -1;
    }

    private static List<String> parsePublishers(Element element) {
        NodeList publishers = element.getElementsByTagName("publishers");
        List<String> publishersList = new ArrayList<>();
        for (int i = 0; i < publishers.getLength(); i++) {
            Element publisherElement = (Element) publishers.item(i);
            try {
                publishersList.add(requireNonBlank(getTagValue("publisher", publisherElement)));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "No valid publisher provided in Tag", e);
            } try {
                publishersList.add(requireNonBlank(publisherElement.getAttribute("name")));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "No valid publisher provided in Attribute name", e);
            }
        }
        return publishersList;
    }

    private static int parsePages(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        try {
            return Integer.parseInt(Objects.requireNonNull(getTagValue("runningtime", bookspecElement)));
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println("No valid runtime provided");
        }
        return -1;
    }

    private static String parsePublicationDate(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        Node publicationElement = bookspecElement.getElementsByTagName("publication").item(0);
        try {
            return requireNonBlank(((Element) publicationElement).getAttribute("date"));
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Publication Date tag found, but could not read date");
        }
        return null;
    }

    private static String parseIsbn(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        Node publicationElement = bookspecElement.getElementsByTagName("isbn").item(0);
        try {
            return requireNonBlank(((Element) publicationElement).getAttribute("val"));
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARN, "Isbn tag found, but could not read isbn");
        }
        return null;
    }

    private static boolean parseAudiobook(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        try {
            return Objects.equals(getTagValue("binding", bookspecElement), "CD");
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println("Can not specify if audiobook or not.");
        }
        return false;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    private static String requireNonBlank(String string) {
        Objects.requireNonNull(string);
        if (string.trim().isEmpty()) {
            throw new NullPointerException("String cannot be empty");
        }
        return string;
    }
}
