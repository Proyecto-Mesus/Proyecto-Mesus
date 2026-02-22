package es.cifpcarlos3.proyecto_mesus_javafx.services;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
public class CartaService {
    private static final String BASE_URL = "http://localhost:8080/api/cartas";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public CartaService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    // llama a la api para obtener una carta por id
    public Carta getCartaById(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Carta.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new Exception("Error al obtener carta: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener todas las cartas de una colección
    public List<Carta> getCartasByColeccionId(int coleccionId) throws Exception {
        String url = BASE_URL + "/coleccion/" + coleccionId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Carta.class));
        } else {
            throw new Exception("Error al obtener cartas: HTTP " + response.statusCode());
        }
    }
    // llama a la api para crear una nueva carta
    public Carta createCarta(Carta carta) throws Exception {
        String requestBody = objectMapper.writeValueAsString(carta);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), Carta.class);
        } else {
            throw new Exception("Error al crear carta: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
    // llama a la api para borrar una carta
    public void deleteCarta(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new Exception("Error al borrar carta: HTTP " + response.statusCode());
        }
    }
    // llama a la api para actualizar los datos de una carta
    public Carta updateCarta(int id, Carta cartaActualizada) throws Exception {
        String url = BASE_URL + "/" + id;
        String requestBody = objectMapper.writeValueAsString(cartaActualizada);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Carta.class);
        } else {
            throw new Exception("Error al actualizar carta: HTTP " + response.statusCode());
        }
    }
}