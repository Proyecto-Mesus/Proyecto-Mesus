package es.cifpcarlos3.proyecto_mesus_javafx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Carta;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EventoService {
    private static final String BASE_URL = "https://proyecto-mesus.onrender.com/api/eventos";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public EventoService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    // llama a la api para obtener un evento por id
    public Evento getEventoById(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Evento.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new Exception("Error al obtener evento: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener todos los eventos disponibles
    public List<Evento> getEventos() throws Exception {
        String url = BASE_URL;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Evento.class));
        } else {
            throw new Exception("Error al obtener eventos: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener todos los eventos creados por un usuario
    public List<Evento> getEventosByCreadorId(int creadorId) throws Exception {
        String url = BASE_URL + "/usuario/" + creadorId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Evento.class));
        } else {
            throw new Exception("Error al obtener eventos: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener todos los eventos a los que un usuario está inscrito
    public List<Evento> getEventosByUsuarioId(int usuarioId) throws Exception {
        String url = BASE_URL + "/inscritos/usuario/" + usuarioId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Evento.class));
        } else {
            throw new Exception("Error al obtener eventos: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener todos los eventos a los que un usuario está inscrito
    public List<Evento> getEventosByNotUsuarioId(int usuarioId) throws Exception {
        String url = BASE_URL + "/no_inscritos/usuario/" + usuarioId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Evento.class));
        } else {
            throw new Exception("Error al obtener eventos: HTTP " + response.statusCode());
        }
    }
    // llama a la api para crear un nuevo evento
    public Evento createEvento(Evento evento) throws Exception {
        String requestBody = objectMapper.writeValueAsString(evento);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), Evento.class);
        } else {
            throw new Exception("Error al crear evento: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
    // llama a la api para borrar un evento
    public void deleteEvento(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new Exception("Error al borrar evento: HTTP " + response.statusCode());
        }
    }
    // llama a la api para actualizar los datos de una carta
    public Evento updateEvento(int id, Evento eventoActualizado) throws Exception {
        String url = BASE_URL + "/" + id;
        String requestBody = objectMapper.writeValueAsString(eventoActualizado);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Evento.class);
        } else {
            throw new Exception("Error al actualizar carta: HTTP " + response.statusCode());
        }
    }
    // llama a la api para añadir un usuario a un evento
    public void addUsuario(int eventoId, int usuarioId) throws Exception {
        String url = BASE_URL + "/" + eventoId + "/usuario/" + usuarioId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Error al añadir el usuario al evento: HTTP " + response.statusCode());
        }
    }
}
