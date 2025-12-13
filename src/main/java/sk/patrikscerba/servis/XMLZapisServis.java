package sk.patrikscerba.servis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sk.patrikscerba.model.Klient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class XMLZapisServis {

    // Priečinok a názov súboru pre ukladanie dát
    private static final  String PRIECINOK_DATA = "data";
    private static final  String SUBOR_KLIENTI_XML = "klienti.xml";

    // Konštruktor, ktorý zabezpečí vytvorenie priečinka pre dáta, ak ešte neexistuje
    public XMLZapisServis() {

        try {
            Path priecinok = Path.of(PRIECINOK_DATA);
            if (!Files.exists(priecinok)) {
                Files.createDirectory(priecinok);
            }
        }catch (Exception e ){
            throw new RuntimeException("Chyba pri vytváraní priečinka pre dáta/", e);
        }
    }

    //Uloženie klienta na koniec XML súboru
    public void ulozKlienta(Klient klient){
        try{
            Path xmlCesta = Path.of(PRIECINOK_DATA, SUBOR_KLIENTI_XML);
            File xmlSubor = xmlCesta.toFile();

            //Príprava na zápis do XML(DOM)
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;

            Element root;

            // Ak súbor neexistuje alebo je prázdny, vytvoríme nový dokument s koreňovým elementom "klienti"
            if (!xmlSubor.exists() || xmlSubor.length() == 0) {
                document = documentBuilder.newDocument();
                root = document.createElement("klienti");
                document.appendChild(root);
            } else {
                document = documentBuilder.parse(xmlSubor);
                root = document.getDocumentElement();
            }
            document.getDocumentElement().normalize();

            // Vytvorenie elementu "klient" a pridanie pod-elementov
            Element klientElement = document.createElement("klient");

            pridajElement(document, klientElement,"id", String.valueOf(klient.getId()));
            pridajElement(document, klientElement,"krstneMeno", klient.getKrstneMeno());
            pridajElement(document, klientElement,"priezvisko", klient.getPriezvisko());
            pridajElement(document, klientElement,"email", klient.getEmail());
            pridajElement(document, klientElement,"telefonneCislo", klient.getTelefonneCislo());
            pridajElement(document, klientElement, "adresa", klient.getAdresa());

            pridajElement(document, klientElement,"datumNarodenia",
                    klient.getDatumNarodenia() != null ? klient.getDatumNarodenia().toString() : "");

            pridajElement(document, klientElement, "datumRegistracie",
                    klient.getDatumRegistracie() != null ? klient.getDatumRegistracie().toString() : "");

            // Pridanie elementu do root elementu
            root.appendChild(klientElement);

            // Zápis dokumentu do XML súboru
            zapisXML(document, xmlSubor);
        } catch (Exception e) {
            throw new RuntimeException("Chyba pri ukladaní klienta do XML: ", e);
        }

    }

    // Pomocná metóda na pridanie elementu s textovým obsahom
    private void pridajElement(Document document, Element parent, String nazov, String hodnota) {
        Element element = document.createElement(nazov);
        element.appendChild(document.createTextNode(hodnota != null ? hodnota : ""));
        parent.appendChild(element);
    }

    // Zapísanie XML dokumentu do XML súboru (nastavené formátovanie + UTF-8)
    private void zapisXML(Document document, File file ) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        //transformer nastavenia  pre čitateľnosť a správne kódovanie
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(document), new StreamResult(file));
    }
}






