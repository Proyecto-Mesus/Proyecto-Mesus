package es.cifpcarlos3.proyecto_mesus_javafx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UsuarioService {

    private static final String BASE_URL = "http://localhost:8080/api/usuarios";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UsuarioService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        // permite usar LocalDateTime
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // llama a la api para hacer login
    public Usuario login(String user, String password) throws Exception {
        String url = BASE_URL + "/login/usuario/" + user + "/password/" + password;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // parsea el JSON a un Usuario
            return objectMapper.readValue(response.body(), Usuario.class);
        } else if (response.statusCode() == 401 || response.statusCode() == 404) {
            return null; // contraseña incorrecta o usuario no encontrado
        } else {
            throw new Exception("Error del servidor: HTTP " + response.statusCode());
        }
    }

    // llama a la api para crear un usuario
    public Usuario createUsuario(Usuario usuario) throws Exception {
        // parsea el Usuario a un JSON
        String requestBody = objectMapper.writeValueAsString(usuario);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            // devuelve el usuario creado
            return objectMapper.readValue(response.body(), Usuario.class);
        } else {
            throw new Exception("Error al crear usuario: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    // llama a la api y devuelve todos los usuarios cuyo nombre contenga un valor
    public List<Usuario> getUsuariosByNombre(String nombre) throws Exception {
        String url = BASE_URL + "/buscar/" + nombre;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            //  parsea el JSON a un Usuario
            return objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Usuario.class)
            );
        } else {
            throw new Exception("Error al buscar usuarios: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

}
