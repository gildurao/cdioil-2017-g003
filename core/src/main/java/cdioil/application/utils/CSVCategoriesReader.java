package cdioil.application.utils;

import cdioil.domain.Category;
import cdioil.domain.MarketStructure;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import cdioil.files.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Imports Categories from .csv files.
 *
 * @author Rita Gonçalves (1160912)
 */
public class CSVCategoriesReader implements CategoriesReader {

    /**
     * Splitter of the columns of the file.
     */
    private static final String SPLITTER = ";";

    /**
     * Number of the line that contains the identifiers of the columns.
     */
    private static final int IDENTIFIERS_LINE = 0;

    /**
     * Capacity of the StringBuilder that stores the path of the Category.
     */
    private static final int CAPACITY = 128;
     /**
     * String with the identifier to category.
     */
    private static final String CATEGORY = "categoria";
     /**
     * String with the identifier to list of the scats.
     */
    private static final String LIST_SCAT = "lista_scat";
     /**
     * String with the identifier to lists of the ubs.
     */
    private static final String LIST_UB = "lista_ub";
    /**
     * String with the identifier to lists of the categories.
     */
    private static final String LIST_CATEGORIES = "lista_categorias";
     /**
     * String with the CAT identifier.
     */
    private static final String DEC_CAT = "descritivo_cat";
     /**
     * String with the  SCAT identifier.
     */
    private static final String DEC_SCAT = "descritivo_scat";
     /**
     * String with the  ID identifier.
     */
    private static final String ID = "id";
     /**
     * String with the  DC identifier.
     */
    private static final String DC = "DC";
     /**
     * String with the  UN identifier.
     */
    private static final String UN = "UN";
     /**
     * String with the  CAT identifier.
     */
    private static final String CAT = "CAT";
     /**
     * String with the  SCAT identifier.
     */
    private static final String SCAT = "SCAT";
     /**
     * String with the  UB identifier.
     */
    private static final String UB = "UB";
    /**
     * File to read.
     */
    private final String file;
    /**
     * String with the file path of the converted file (from JSON to XML).
     */
    private static final String CAT_OUTPUT_PATH = "csv_category_output.xml";

    private final XMLCategoriesReader xmlCatReader = new XMLCategoriesReader(CAT_OUTPUT_PATH);

    /**
     * Creates an instance of CSVCategoriesReader, receiving the name of the file to read.
     *
     * @param file File to read
     */
    public CSVCategoriesReader(String file) {
        this.file = file;
    }

    /**
     * Returns the number of Categories in the list of Categories.
     *
     * @return the number of Categories that were read
     */
    @Override
    public int getNumberOfCategoriesRead() {
        return xmlCatReader.getNumberOfCategoriesRead();
    }

    /**
     * Imports Categories from a .csv file.
     *
     * @return List with the Categories that were read. Null if the file is not valid
     */
    @Override
    public MarketStructure readCategories() {
        List<String> fileContent = FileReader.readFile(new File(file));

        MarketStructure em = new MarketStructure();

        List<Category> lc = new LinkedList<>();
        for (int i = IDENTIFIERS_LINE + 1; i < fileContent.size(); i++) {
            String[] line = fileContent.get(i).split(SPLITTER);
            StringBuilder path = new StringBuilder(CAPACITY);

            if (line.length != 0) { //Doesn't read empty lines
                try {
                    path.append(line[0]).
                            append(Category.Sufixes.SUFIX_DC);
                    Category c = new Category(line[1], path.toString().trim());
                    if (em.addCategory(c)) {
                        lc.add(c);
                    }

                    path.append("-").append(line[2]).
                            append(Category.Sufixes.SUFIX_UN);
                    Category c1 = new Category(line[3], path.toString().trim());
                    if (em.addCategory(c1)) {
                        lc.add(c1);
                    }

                    path.append("-").append(line[4]).
                            append(Category.Sufixes.SUFIX_CAT);
                    Category c2 = new Category(line[5], path.toString().trim());
                    if (em.addCategory(c2)) {
                        lc.add(c2);
                    }

                    path.append("-").append(line[6]).
                            append(Category.Sufixes.SUFIX_SCAT);
                    Category c3 = new Category(line[7], path.toString().trim());
                    if (em.addCategory(c3)) {
                        lc.add(c3);
                    }

                    path.append("-").append(line[8]).
                            append(Category.Sufixes.SUFIX_UB);
                    Category c4 = new Category(line[9], path.toString().trim());
                    if (em.addCategory(c4)) {
                        lc.add(c4);
                    }

                } catch (IllegalArgumentException ex) {
                    System.out.println("The category in the line " + i + " is not valid.");
                }
            }
        }
        writeXMLfile(em);
        return xmlCatReader.readCategories();
        //return em;
    }

    public void writeXMLfile(MarketStructure em) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            writeCategories(doc, em);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(CAT_OUTPUT_PATH);
            try {
                transformer.transform(source, result);
            } catch (TransformerException ex) {
                Logger.getLogger(XMLTemplateWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException | TransformerConfigurationException ex) {
            Logger.getLogger(XMLTemplateWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeCategories(Document doc, MarketStructure em) {
        Element rootElement = doc.createElement(LIST_CATEGORIES);
        doc.appendChild(rootElement);
        String idDC = "";
        String idUN = "";
        String idCAT = "";
        String idSCAT = "";
        String idUB = "";
        Element listaSCATElem = doc.createElement("x");
        Element listaUBElem = doc.createElement("x");
        for (Category cat : em.getLeaves()) {
            if (!cat.categoryPathIdentifiers().get(0).replace(DC, "").equalsIgnoreCase(idDC)
                    || !cat.categoryPathIdentifiers().get(1).replace(UN, "").equalsIgnoreCase(idUN)
                    || !cat.categoryPathIdentifiers().get(2).replace(CAT, "").equalsIgnoreCase(idCAT)) {

                Element categoryElement = doc.createElement(CATEGORY);
                rootElement.appendChild(categoryElement);
                Element dcElem = doc.createElement(DC.toLowerCase());

                Attr attrDC = doc.createAttribute(ID);
                idDC = cat.categoryPathIdentifiers().get(0).replace(DC, "");
                dcElem.setTextContent(cat.categoryName());
                attrDC.setValue(idDC);
                dcElem.setAttributeNode(attrDC);
                categoryElement.appendChild(dcElem);
                Element unElem = doc.createElement(UN.toLowerCase());
                unElem.setTextContent(cat.categoryName());
                Attr attrUN = doc.createAttribute(ID);
                idUN = cat.categoryPathIdentifiers().get(1).replace(UN, "");
                attrUN.setValue(idUN);
                unElem.setAttributeNode(attrUN);
                categoryElement.appendChild(unElem);

                Element catElem = doc.createElement(CAT.toLowerCase());
                Attr attrCAT = doc.createAttribute(ID);
                idCAT = cat.categoryPathIdentifiers().get(2).replace(CAT, "");
                attrCAT.setValue(idCAT);
                catElem.setAttributeNode(attrCAT);

                Element elementDecCAT = doc.createElement(DEC_CAT);
                elementDecCAT.appendChild(doc.createTextNode(cat.categoryName()));

                catElem.appendChild(elementDecCAT);

                categoryElement.appendChild(catElem);

                if (cat.categoryPathIdentifiers().size() > 3) {
                    listaSCATElem = doc.createElement(LIST_SCAT);
                    catElem.appendChild(listaSCATElem);
                }

                Element scatElem = doc.createElement(SCAT.toLowerCase());
                listaSCATElem.appendChild(scatElem);
                Attr attrSCAT = doc.createAttribute(ID);
                idSCAT = cat.categoryPathIdentifiers().get(3).replace(SCAT, "");
                attrSCAT.setValue(idSCAT);
                scatElem.setAttributeNode(attrSCAT);
                Element elementDecSCAT = doc.createElement(DEC_SCAT);
                elementDecSCAT.appendChild(doc.createTextNode(cat.categoryName()));
                scatElem.appendChild(elementDecSCAT);
                if (cat.categoryPathIdentifiers().size() > 4) {
                    listaUBElem = doc.createElement(LIST_UB);
                    scatElem.appendChild(listaUBElem);
                    Element ubElem = doc.createElement(UB.toLowerCase());
                    listaUBElem.appendChild(ubElem);
                    ubElem.setTextContent(cat.categoryName());

                    Attr attrUB = doc.createAttribute(ID);
                    idUB = cat.categoryPathIdentifiers().get(4).replace(UB, "");
                    attrUB.setValue(idUB);
                    ubElem.setAttributeNode(attrUB);
                }

            } else if (cat.categoryPathIdentifiers().get(0).replace(DC, "").equalsIgnoreCase(idDC)
                    && cat.categoryPathIdentifiers().get(1).replace(UN, "").equalsIgnoreCase(idUN)
                    && cat.categoryPathIdentifiers().get(2).replace(CAT, "").equalsIgnoreCase(idCAT)) {
                if (!cat.categoryPathIdentifiers().get(3).replace(SCAT, "").equalsIgnoreCase(idSCAT)) {
                    Element scatElem = doc.createElement(SCAT.toLowerCase());
                    listaSCATElem.appendChild(scatElem);
                    Attr attrSCAT = doc.createAttribute(ID);
                    idSCAT = cat.categoryPathIdentifiers().get(3).replace(SCAT, "");
                    attrSCAT.setValue(idSCAT);
                    scatElem.setAttributeNode(attrSCAT);
                    Element elementDecSCAT = doc.createElement(DEC_SCAT);
                    elementDecSCAT.appendChild(doc.createTextNode(cat.categoryName()));
                    scatElem.appendChild(elementDecSCAT);
                    if (cat.categoryPathIdentifiers().size() > 4) {
                        listaUBElem = doc.createElement(LIST_UB);
                        scatElem.appendChild(listaUBElem);
                        Element ubElem = doc.createElement(UB.toLowerCase());
                        listaUBElem.appendChild(ubElem);
                        ubElem.setTextContent(cat.categoryName());

                        Attr attrUB = doc.createAttribute(ID);
                        idUB = cat.categoryPathIdentifiers().get(4).replace(UB, "");
                        attrUB.setValue(idUB);
                        ubElem.setAttributeNode(attrUB);
                    }
                } else {
                    if (cat.categoryPathIdentifiers().size() > 4) {
                        Element ubElem = doc.createElement(UB.toLowerCase());
                        listaUBElem.appendChild(ubElem);
                        ubElem.setTextContent(cat.categoryName());

                        Attr attrUB = doc.createAttribute(ID);
                        idUB = cat.categoryPathIdentifiers().get(4).replace(UB, "");
                        attrUB.setValue(idUB);
                        ubElem.setAttributeNode(attrUB);
                    }
                }

            }
        }
    }
}
