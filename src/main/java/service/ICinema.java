package service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

@WebService(name = "CinemaWS")
public interface ICinema {

    @WebMethod(operationName = "validerCatalogue")
    String validerCatalogue();

    @WebMethod(operationName = "listerFilms")
    List<Film> listerFilms();

    @WebMethod(operationName = "getFilmParId")
    Film getFilmParId(@WebParam(name = "id") String id);

    @WebMethod(operationName = "filmsParGenre")
    List<Film> filmsParGenre(@WebParam(name = "genre") String genre);

    @WebMethod(operationName = "listerSeances")
    List<String> listerSeances();
}