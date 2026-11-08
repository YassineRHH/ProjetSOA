package serveur;

import jakarta.xml.ws.Endpoint;
import service.Cinema;

public class ServeurJWS {
    public static void main(String[] args) {
        String url = "http://localhost:8686/cinema";
        Endpoint.publish(url, new Cinema());
        System.out.println("Service SOAP publié à : " + url);
        System.out.println("WSDL : " + url + "?wsdl");
    }
}