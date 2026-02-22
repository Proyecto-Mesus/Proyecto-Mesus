package es.cifpcarlos3.proyecto_mesus_javafx.services;

import es.cifpcarlos3.proyecto_mesus_javafx.models.Juego;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class JuegoService {
    private static final String BASE_URL = "http://localhost:8080/api/juegos";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JuegoService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // llama a la api y obtiene todos los juegos
    public List<Juego> getAllJuegos() throws Exception {
        String url = BASE_URL;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Juego.class));
        } else {
            throw new Exception("Error al obtener juegos: HTTP " + response.statusCode());
        }
    }
}
