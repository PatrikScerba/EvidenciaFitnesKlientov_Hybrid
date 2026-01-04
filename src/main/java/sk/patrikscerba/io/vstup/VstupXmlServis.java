package sk.patrikscerba.io.vstup;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sk.patrikscerba.io.log.AppLogServis;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

public class VstupXmlServis {

    private final AppLogServis applog = new AppLogServis();
    private static final String XML_SUBOR = "data/vstupy.xml";

    // XML slúži ako offline evidencia vstupov v hybridnom režime aplikácie
    private Document nacitajXml() throws Exception {
        File file = new File(XML_SUBOR);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        // Ak súbor neexistuje, vytvoríme nový XML dokument s koreňovým elementom vstupy
        if (!file.exists()) {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("vstupy");
            document.appendChild(root);

            ulozXml(document);
            return document;
        }

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return documentBuilder.parse(file);
    }

    // Uloženie XML dokumentu do súboru
    private void ulozXml(Document document) throws Exception {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(document), new StreamResult(new File(XML_SUBOR)));
    }

    //Kontrola či mal klient dnes vstup
    public boolean malDnesVstup(int klientId, LocalDate datum) {
        try {
            Document document = nacitajXml();
            NodeList list = document.getElementsByTagName("vstup");

            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);

                int id = Integer.parseInt(element.getAttribute("klientId"));
                LocalDate vstupDatum = LocalDate.parse(element.getAttribute("datum"));

                if (id == klientId && vstupDatum.equals(datum)) {
                    return true;
                }
            }

        } catch (Exception e) {
            applog.error("Chyba pri kontrole dnešného vstupu v XML: ",e);
        }
        return false;
    }

    //Zapísanie nového vstupu do XML
    public void zapisVstupXML(int klientId, LocalDate datum, LocalTime cas) {

        try {
            Document document = nacitajXml();

            //Poistka ak by náhodou koreňový element neexistoval(vytvoríme nový)
            Element root = document.getDocumentElement();

            if (root == null) {
                root = document.createElement("vstupy");
                document.appendChild(root);
            }

            Element elementNovy = document.createElement("vstup");
            elementNovy.setAttribute("klientId", String.valueOf(klientId));
            elementNovy.setAttribute("datum", datum.toString());
            elementNovy.setAttribute("cas", cas.toString());

            root.appendChild(elementNovy);
            ulozXml(document);

        } catch (Exception e) {
            applog.error("Chyba pri zápise vstupu do XML: ",e);
        }
    }
}




