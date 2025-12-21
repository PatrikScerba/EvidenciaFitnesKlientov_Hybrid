package sk.patrikscerba.io.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.patrikscerba.model.Klient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XMLNacitanieServis {

    private static final  String PRIECINOK_DATA  = "data";
    private static final  String SUBOR_KLIENTI_XML = "klienti.xml";

    //Načítanie klientov zo súboru XML
    public List<Klient> nacitajKlientovZoXML() {
        List<Klient> klienti = new ArrayList<>();

        Path cestaKSuboru = Path.of(PRIECINOK_DATA, SUBOR_KLIENTI_XML);
        File xmlSubor = cestaKSuboru.toFile();

        if (!xmlSubor.exists() || xmlSubor.length() == 0) {
            return klienti;
        }
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse(xmlSubor);
            document.getDocumentElement().normalize();

            NodeList zoznamKlientov = document.getElementsByTagName("klient");

            for (int i = 0; i < zoznamKlientov.getLength(); i++) {

                Node node = zoznamKlientov.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                Element element = (Element) node;

                int id = Integer.parseInt(getText(element, "id"));

                String krstneMeno = getText(element, "krstneMeno");
                String priezvisko = getText(element, "priezvisko");
                String email = getText(element, "email");
                String telefonneCislo = getText(element, "telefonneCislo");
                String adresa = getText(element, "adresa");

                LocalDate datumNarodenia = LocalDate.parse(getText(element, "datumNarodenia"));
                LocalDate datumRegistracie= LocalDate.parse(getText(element, "datumRegistracie"));

                Klient klient = new Klient(id, krstneMeno, priezvisko, datumNarodenia,
                        telefonneCislo, adresa, email, datumRegistracie);

                klienti.add(klient);
            }
        }
        catch (Exception e){
            System.err.println("Chyba pri načítaní klientov zo súboru XML. Vráti prázdny zoznam" + e.getMessage());
        }
        return klienti;
    }

    //Vyhľadanie klienta v XML podľa ID
    public Klient najdiKlientaVXmlPodlaId(int id) {

        List<Klient> klienti = nacitajKlientovZoXML();

        for (Klient k : klienti) {
            if (k.getId() == id) {
                return k;
            }
        }
        return null;
    }

    // Pomocná metóda na získanie textového obsahu z elementu podľa tagu
    private String getText (Element element, String tag){
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
}



