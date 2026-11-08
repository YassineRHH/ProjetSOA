package service;

import jakarta.jws.WebService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "service.ICinema",
        serviceName = "CinemaWSService",
        portName = "CinemaWSPort")
public class Cinema implements ICinema {

    @Override
    public String validerCatalogue() {
        try (InputStream xsd = getClass().getResourceAsStream("/films.xsd");
             InputStream xml = getClass().getResourceAsStream("/films.xml")) {
            Schema schema = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new StreamSource(xsd));
            schema.newValidator().validate(new StreamSource(xml));
            return "films.xml est valide par rapport à films.xsd";
        } catch (Exception e) {
            return "Document invalide : " + e.getMessage();
        }
    }

    @Override
    public List<Film> listerFilms() {
        return lireFilms(charger());
    }

    @Override
    public Film getFilmParId(String id) {
        for (Film f : lireFilms(charger())) {
            if (f.getId().equals(id)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Film introuvable : " + id);
    }

    @Override
    public List<Film> filmsParGenre(String genre) {
        List<Film> resultat = new ArrayList<>();
        for (Film f : lireFilms(charger())) {
            if (f.getGenre().equalsIgnoreCase(genre)) {
                resultat.add(f);
            }
        }
        return resultat;
    }

    @Override
    public List<String> listerSeances() {
        Document doc = charger();

        Map<String, String> titres = new HashMap<>();
        for (Film f : lireFilms(doc)) {
            titres.put(f.getId(), f.getTitre());
        }

        Map<String, String> salles = new HashMap<>();
        NodeList nl = doc.getElementsByTagName("salle");
        for (int i = 0; i < nl.getLength(); i++) {
            Element s = (Element) nl.item(i);
            salles.put(s.getAttribute("numero"), texte(s, "nom"));
        }

        List<String> resultat = new ArrayList<>();
        NodeList seances = doc.getElementsByTagName("seance");
        for (int i = 0; i < seances.getLength(); i++) {
            Element s = (Element) seances.item(i);
            resultat.add(titres.get(s.getAttribute("filmRef"))
                    + " | " + salles.get(s.getAttribute("salleRef"))
                    + " | " + texte(s, "date")
                    + " | " + texte(s, "prix"));
        }
        return resultat;
    }

    // ---------- méthodes privées (non exposées) ----------

    private Document charger() {
        try (InputStream in = getClass().getResourceAsStream("/films.xml")) {
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(in);
        } catch (Exception e) {
            throw new RuntimeException("Lecture de films.xml impossible", e);
        }
    }

    private List<Film> lireFilms(Document doc) {
        List<Film> films = new ArrayList<>();
        NodeList nl = doc.getElementsByTagName("film");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            Film f = new Film();
            f.setId(e.getAttribute("id"));
            f.setDisponible(Boolean.parseBoolean(e.getAttribute("disponible")));
            f.setTitre(texte(e, "titre"));
            f.setGenre(texte(e, "genre"));
            f.setAnnee(Integer.parseInt(texte(e, "annee")));
            f.setRealisateur(texte(e, "realisateur"));
            films.add(f);
        }
        return films;
    }

    private String texte(Element parent, String balise) {
        return parent.getElementsByTagName(balise).item(0).getTextContent().trim();
    }
}