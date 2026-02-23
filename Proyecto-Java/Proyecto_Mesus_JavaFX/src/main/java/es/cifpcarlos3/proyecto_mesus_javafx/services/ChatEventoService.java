package es.cifpcarlos3.proyecto_mesus_javafx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.cifpcarlos3.proyecto_mesus_javafx.models.ChatEvento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Evento;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChatEventoService {
    private static final String BASE_URL = "https://proyecto-mesus.onrender.com/api/chats_evento";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public ChatEventoService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    // llama a la api para obtener un chat de evento por id
    public ChatEvento getChatEventoById(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), ChatEvento.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new Exception("Error al obtener chat de evento: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener el chat de un evento
    public ChatEvento getChatEventoByEventoId(int idEvento) throws Exception {
        String url = BASE_URL + "/evento/" + idEvento;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), ChatEvento.class);
        } else {
            throw new Exception("Error al obtener chat de evento: HTTP " + response.statusCode());
        }
    }
    // llama a la api para crear un nuevo chat de evento
    public ChatEvento createChatEvento(int idEvento) throws Exception {
        String url = BASE_URL + "/evento/" + idEvento;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), ChatEvento.class);
        } else {
            throw new Exception("Error al crear chat de evento: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
}
