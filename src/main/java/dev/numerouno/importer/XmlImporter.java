package dev.numerouno.importer;

import dev.numerouno.db.Database;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final IntegrityLogger il = new IntegrityLogger();
    /**
     * Constructor initializes super-class-methods
     */
    public XmlImporter(Database db) {
        super(db);
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


        Element root = doc.getDocumentElement();
        List<Category> categories = parseCategories(root, null);

        for (Category category : categories) {
            try {
                category.create(super.database, il);
            } catch (AlreadyExistsException e) {
                il.addError(IntegrityLogger.ErrorType.DUPLICATE_ENTRY, e.getMessage() + category);
            }
        }

        List<Shop> shops = parseShops(doc.getElementsByTagName("shop"));
        for (Shop shop : shops) {
            shop.create(super.database, il);
        }
        String filename = "xml-log-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt";
        il.printProblemsToFile(new File(filename));
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

    /**
     * parses the shop
     * @param shops NodeList with each shop (probably just one)
     * @return List of Shops including products
     */
    private static List<Shop> parseShops(NodeList shops) {
        List<Shop> shopsList = new ArrayList<>();
        for (int i = 0; i < shops.getLength(); i++) {
            Node shop = shops.item(i);
            if (shop.getNodeType() == Node.ELEMENT_NODE && shop.getNodeName().equals("shop")) {
                Element shopElement = (Element) shop;

                Shop shopObj = new Shop(shopElement.getAttribute("name"),
                        new Address(
                                shopElement.getAttribute("street"),
                                shopElement.getAttribute("zip")
                        )
                );
                shopObj.setProductList(parseProducts(shopElement));
                shopsList.add(shopObj);

            }
        }
        return shopsList;
    }

    /**
     * parses products from shop
     * @param productElement ShopElement
     * @return List of Products
     */
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

        return products;
    }

    /**
     * parses CDs
     * @param cdElement parses if element is cd
     * @return CD-Object
     */
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

    /**
     * parses books
     * @param bookElement book-xml-element
     * @return Book-Object
     */
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

    /**
     * parses dvds
     * @param dvdElement dvd xml-element
     * @return DVD-Object
     */
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
        dvd.setPeople(parsePeople(dvdElement));
        return dvd;
    }


    /**
     * parses rank of an element
     * @param element Element to parse
     * @return Rank
     */
    private static Integer parseRank(Element element) {
        Integer ranking = null;
        String rank = element.getAttribute("salesrank");
        if (!rank.equals("null")) {
            if (!rank.isBlank()) {
                ranking = Integer.parseInt(rank);
            }
        }
        if (ranking == null) {
            LOGGER.log(Level.WARN, "Could not get rank");
        }
        return ranking;
    }

    /**
     * parses ean of an element
     * @param element Element to parse
     * @return ean
     */
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
            LOGGER.log(Level.DEBUG, "Could not parse ean as Attribute");
        }
        if (ean == null) {
            LOGGER.log(Level.WARN, "Could not parse ean");
        }
        return ean;
    }

    /**
     * parses image of an element
     * @param element Element to parse
     * @return Image as String
     */
    private static String parseImage(Element element) {
        String url = null;
        try {
            String url_tmp = ((Element) element.getElementsByTagName("details").item(0)).getAttribute("img");
            if (!url_tmp.isBlank()) {
                url = url_tmp;
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.DEBUG, "Image not found as tag for object {}", element);
        }
        try {
            String url_tmp = element.getAttribute("picture");
            if (!url_tmp.isBlank()) {
                url = url_tmp;
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.DEBUG, "Image not found as attribute for object {}", element);
        }
        if (url == null) {
            LOGGER.log(Level.WARN, "Could not parse Image");
        }
        return url;
    }

    /**
     * parses title of an element
     * @param element Element to parse
     * @return title as string
     */
    private static String parseTitle(Element element) {
        String title = null;
        try {
            title = requireNonBlank(getTagValue("title", element));
        } catch (NullPointerException e) {
            LOGGER.log(Level.DEBUG, "Title not found as tag for object {}", element);
        }
        if (title == null) {
            LOGGER.log(Level.WARN, "Could not parse Product's Title");
        }
        return title;
    }

    /**
     * parses release-date of an element
     * @param element Element to parse
     * @return date as string
     */
    private static String parseReleaseDate(Element element) {
        String releaseDate = null;
        NodeList musicspec = element.getElementsByTagName("musicspec");
        for (int i = 0; i < musicspec.getLength(); i++) {
            Element musicSpecElement = (Element) musicspec.item(i);
            try {
                releaseDate = requireNonBlank(getTagValue("releasedate", musicSpecElement));
            } catch (NullPointerException e) {
                LOGGER.log(Level.DEBUG, "Release date not found as tag for object {}", musicSpecElement);
            }

        }
        if (releaseDate == null) {
            LOGGER.log(Level.WARN, "Could not parse ReleaseDate");
        }
        return releaseDate;
    }

    /**
     * parses people of an element
     * @param element Element to parse
     * @return people as list
     */
    private static List<Person> parsePeople(Element element) {
        List<Person> people = new ArrayList<>();

        // Liste von Rollen und ihren XML-Tags
        String[] roles = { "actor", "artist", "author", "creator", "director" };

        for (String role : roles) {
            String groupTag = role + "s"; // z.B. "actors"

            NodeList groupList = element.getElementsByTagName(groupTag);
            for (int i = 0; i < groupList.getLength(); i++) {
                Element group = (Element) groupList.item(i);

                NodeList personTags = group.getElementsByTagName(role);
                for (int j = 0; j < personTags.getLength(); j++) {
                    Element personElement = (Element) personTags.item(j);

                    // Versuche zuerst den Textinhalt (z. B. <actor>James Last</actor>)
                    String name = personElement.getTextContent().trim();

                    // Wenn leer, versuche das Attribut "name" (z. B. <actor name="Heino"/>)
                    if (name.isBlank()) {
                        name = personElement.getAttribute("name").trim();
                    }

                    // Wenn gefunden, hinzufügen
                    if (!name.isBlank()) {
                        people.add(new Person(name, role));
                    }
                }
            }
        }

        if (people.isEmpty()) {
            LOGGER.log(Level.WARN, "No people found in <item>");
        }
        for (Person person : people) {
            System.out.println(person.getName() + person.getRole());
        }
        return people;
    }

    /**
     * parses labels of an element
     * @param element Element to parse
     * @return labels as list
     */
    private static List<Label> parseLabels(Element element) {
        NodeList labels = element.getElementsByTagName("labels");
        List<Label> labelsList = new ArrayList<>();
        for (int i = 0; i < labels.getLength(); i++) {
            Element labelElement = (Element) labels.item(i);
            try {
                labelsList.add(new Label(requireNonBlank(getTagValue("label", labelElement))));
                LOGGER.log(Level.DEBUG, "Found Label as Tag");
            } catch (NullPointerException e) {
                LOGGER.log(Level.DEBUG, "Label name not found as tag for object {}", labelElement);
            }
            try {
                labelsList.add(new Label(requireNonBlank(labelElement.getAttribute("name"))));
                LOGGER.log(Level.DEBUG, "Found Label as Attribute");
            } catch (NullPointerException e) {
                LOGGER.log(Level.DEBUG, "Label name not found as Attribute for object {}", labelElement);
            }
        }
        if (labelsList.isEmpty()) {
            LOGGER.log(Level.WARN, "No labels found");
        }
        return labelsList;
    }


    /**
     * parses titles of an element
     * @param element Element to parse
     * @return Titles as list
     */
    private static List<MusicTitle> parseTitles(Element element) {
        NodeList titles = element.getElementsByTagName("tracks");
        List<MusicTitle> titlesList = new ArrayList<>();
        for (int i = 0; i < titles.getLength(); i++) {
            Element titleElement = (Element) titles.item(i);
            titlesList.add(new MusicTitle(getTagValue("title", titleElement)));
        }
        if (titlesList.isEmpty()) {
            LOGGER.log(Level.WARN, "No song titles found");
        }
        return titlesList;
    }

    /**
     * parses similar products of an element
     * @param element Element to parse
     * @return list of product-objects
     */
    private static List<Product> parseSimilars(Element element) {
        Node similars = element.getElementsByTagName("similars").item(0);
        NodeList similarElements = ((Element) similars).getElementsByTagName("sim_product");
        List<Product> similarsList = new ArrayList<>();
        for (int i = 0; i < similarElements.getLength(); i++) {
            Element similarElement = (Element) similarElements.item(i);
            try {
                // System.out.println(getTagValue("asin", similarElement));
                similarsList.add(new Product(requireNonBlank(requireNonBlank(getTagValue("asin", similarElement)))));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Similars tag found, but could not read ASIN from Sub-Tag");
            }
        }
        NodeList similarItems = ((Element) similars).getElementsByTagName("item");
        for (int i = 0; i < similarItems.getLength(); i++) {
            Element similarElement = (Element) similarItems.item(i);
            try {
                // System.out.println(similarElement.getAttribute("asin"));
                similarsList.add(new Product(requireNonBlank(similarElement.getAttribute("asin"))));
            } catch (NullPointerException e) {
                LOGGER.log(Level.WARN, "Similars tag found, but could not read ASIN from Attribute");
            }
        }
        if (similarsList.isEmpty()) {
            LOGGER.log(Level.WARN, "No similars found");
        }
        return similarsList;
    }

    /**
     * parses condition of an element
     * @param element Element to parse
     * @return condition as string
     */
    private static String parseCondition(Element element) {
        Node price = element.getElementsByTagName("price").item(0);
        Element priceElement = (Element) price;
        String condition = null;
        try {
            condition = priceElement.getAttribute("state");
        } catch (NullPointerException e) {
            LOGGER.log(Level.DEBUG, "Price tag found, but could not read state from Attribute");
        }
        if (condition == null) {
            LOGGER.log(Level.WARN, "No Condition found");
        }
        return condition;
    }

    /**
     * parses price of an element
     * @param element Element to parse
     * @return price as double
     */
    private static double parsePrice(Element element) {
        Node price = element.getElementsByTagName("price").item(0);
        Element priceElement = (Element) price;
        double doublePrice = -1.0;
        try {
            double priceValue = Double.parseDouble(requireNonBlank(getTagValue("price", element)));
            doublePrice = priceValue * Double.parseDouble(priceElement.getAttribute("mult"));
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.DEBUG, "No valid price provided: ");
        }
        if (doublePrice == -1.0) {
            LOGGER.log(Level.WARN, "Could not parse Price");
        }
        return doublePrice;
    }

    /**
     * parses format of an element
     * @param element Element to parse
     * @return format as string
     */
    private static String parseFormat(Element element) {
        NodeList dvdspec = element.getElementsByTagName("dvdspec");
        String format = null;
        for (int i = 0; i < dvdspec.getLength(); i++) {
            Element musicSpecElement = (Element) dvdspec.item(i);
            try {
                format = requireNonBlank(getTagValue("format", musicSpecElement));
            } catch (NullPointerException e) {
                LOGGER.log(Level.DEBUG, "Format tag found, but could not read format from Attribute");
            }
        }
        if (format == null) {
            LOGGER.log(Level.WARN, "No format found");
        }
        return format;
    }

    /**
     * parses region code of an element
     * @param element Element to parse
     * @return region code as int
     */
    private static Integer parseRegionCode(Element element) {
        Node dvdspec = element.getElementsByTagName("dvdspec").item(0);
        Element dvdspecElement = (Element) dvdspec;
        Integer regionCode = null;
        try {
            regionCode = Integer.parseInt(requireNonBlank(getTagValue("regioncode", dvdspecElement)));
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.warn("No valid region-code provided");
        }
        return regionCode;
    }

    /**
     * parses runtime of an element
     * @param element Element to parse
     * @return runtime as int
     */
    private static int parseRuntime(Element element) {
        Node dvdspec = element.getElementsByTagName("dvdspec").item(0);
        Element dvdspecElement = (Element) dvdspec;
        int runtime = -1;
        try {
            runtime = Integer.parseInt(requireNonBlank(getTagValue("runningtime", dvdspecElement)));
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.debug("No valid runtime provided", e);
        }
        if (runtime == -1) {
            LOGGER.log(Level.WARN, "No valid runtime found");
        }
        return runtime;
    }
    /**
     * parses publishers of an element
     * @param element Element to parse
     * @return publishers as list
     */
    private static List<Verlag> parsePublishers(Element element) {
        NodeList publishers = element.getElementsByTagName("publishers");
        List<Verlag> publishersList = new ArrayList<>();
        for (int i = 0; i < publishers.getLength(); i++) {
            Element publisherElement = (Element) publishers.item(i);
            try {
                publishersList.add(new Verlag(requireNonBlank(getTagValue("publisher", publisherElement))));
            } catch (NullPointerException e) {
                LOGGER.log(Level.DEBUG, "No valid publisher provided in Tag", e);
            } try {
                publishersList.add(new Verlag(requireNonBlank(publisherElement.getAttribute("name"))));
            } catch (NullPointerException e) {
                LOGGER.log(Level.DEBUG, "No valid publisher provided in Attribute name", e);
            }
        }
        if (publishersList.isEmpty()) {
            LOGGER.log(Level.WARN, "No publishers found");
        }
        return publishersList;
    }

    /**
     * parses pages of an element
     * @param element Element to parse
     * @return pages as int
     */
    private static int parsePages(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        int pages = -1;
        try {
            pages = Integer.parseInt(Objects.requireNonNull(getTagValue("runningtime", bookspecElement)));
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.log(Level.WARN, "Could not parse pages");
        }
        return pages;
    }

    /**
     * parses publication date of an element
     * @param element Element to parse
     * @return date as string
     */
    private static String parsePublicationDate(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        Node publicationElement = bookspecElement.getElementsByTagName("publication").item(0);
        String publicationDate = null;
        try {
            publicationDate = requireNonBlank(((Element) publicationElement).getAttribute("date"));
        } catch (NullPointerException e) {
            LOGGER.log(Level.DEBUG, "Publication Date tag found, but could not read date");
        }
        if (publicationDate == null) {
            LOGGER.log(Level.WARN, "No valid publication date");
        }
        return publicationDate;
    }

    /**
     * parses isbn of an element
     * @param element Element to parse
     * @return isbn as string
     */
    private static String parseIsbn(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        Node publicationElement = bookspecElement.getElementsByTagName("isbn").item(0);
        String isbn = null;
        try {
            isbn = requireNonBlank(((Element) publicationElement).getAttribute("val"));
        } catch (NullPointerException e) {
            LOGGER.log(Level.DEBUG, "Isbn tag found, but could not read isbn");
        }
        if (isbn == null) {
            LOGGER.log(Level.WARN, "No valid ISBN found");
        }
        return isbn;
    }

    /**
     * parses if an element is audiobook
     * @param element Element to parse
     * @return audiobook as boolean
     */
    private static boolean parseAudiobook(Element element) {
        Node bookspec = element.getElementsByTagName("bookspec").item(0);
        Element bookspecElement = (Element) bookspec;
        boolean audiobook = false;
        try {
            audiobook = Objects.equals(getTagValue("binding", bookspecElement), "CD");
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.log(Level.WARN, "Could not specifiy if Audiobook or not");
        }

        return audiobook;
    }

    /**
     * Wrapper for getting the Value of a Tag
     * @param tag Tag to retrieve
     * @param element Element to search tag on
     * @return value as string
     */
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    /**
     * wrapper function for strings to get a non-empty or null string
     * @param string to analyse for emptiness
     * @return string or null
     */
    private static String requireNonBlank(String string) {
        Objects.requireNonNull(string);
        if (string.trim().isBlank()) {
            throw new NullPointerException("String cannot be empty");
        }
        return string;
    }
}
