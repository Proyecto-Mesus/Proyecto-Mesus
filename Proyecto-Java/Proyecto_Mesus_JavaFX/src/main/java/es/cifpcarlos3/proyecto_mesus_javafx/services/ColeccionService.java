package es.cifpcarlos3.proyecto_mesus_javafx.services;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
public class ColeccionService {
    private static final String BASE_URL = "http://localhost:8080/api/colecciones";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public ColeccionService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    // llama a la api para obtener una colección por id
    public Coleccion getColeccionById(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Coleccion.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new Exception("Error al obtener colección: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener todas las colecciones de un usuario
    public List<Coleccion> getColeccionesByUsuarioId(int usuarioId) throws Exception {
        String url = BASE_URL + "/usuario/" + usuarioId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Coleccion.class));
        } else {
            throw new Exception("Error al obtener colecciones: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener solo las colecciones públicas de un usuario
    public List<Coleccion> getColeccionesPublicasByUsuarioId(int usuarioId) throws Exception {
        String url = BASE_URL + "/publicas/usuario/" + usuarioId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Coleccion.class));
        } else {
            throw new Exception("Error al obtener colecciones públicas: HTTP " + response.statusCode());
        }
    }
    // llama a la api para crear una nueva colección
    public Coleccion createColeccion(Coleccion coleccion) throws Exception {
        String requestBody = objectMapper.writeValueAsString(coleccion);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), Coleccion.class);
        } else {
            throw new Exception("Error al crear colección: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
    // llama a la api para borrar una colección
    public void deleteColeccion(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new Exception("Error al borrar colección: HTTP " + response.statusCode());
        }
    }
    // llama a la api para actualizar los datos de una colección
    public Coleccion updateColeccion(int id, Coleccion coleccionActualizada) throws Exception {
        String url = BASE_URL + "/" + id;
        String requestBody = objectMapper.writeValueAsString(coleccionActualizada);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Coleccion.class);
        } else {
            throw new Exception("Error al actualizar colección: HTTP " + response.statusCode());
        }
    }
}