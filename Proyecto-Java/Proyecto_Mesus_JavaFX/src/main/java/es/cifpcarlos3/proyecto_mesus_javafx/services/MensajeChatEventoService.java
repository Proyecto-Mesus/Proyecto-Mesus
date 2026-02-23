package es.cifpcarlos3.proyecto_mesus_javafx.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.cifpcarlos3.proyecto_mesus_javafx.models.ChatEvento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Coleccion;
import es.cifpcarlos3.proyecto_mesus_javafx.models.MensajeChatEvento;
import es.cifpcarlos3.proyecto_mesus_javafx.models.PageResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MensajeChatEventoService {
    private static final String BASE_URL = "http://localhost:8080/api/mensajes_chats_evento";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public MensajeChatEventoService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    // llama a la api para obtener un mensaje de chat de evento por id
    public MensajeChatEvento getMensajeChatEventoById(int id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), MensajeChatEvento.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new Exception("Error al obtener mensaje de chat de evento: HTTP " + response.statusCode());
        }
    }
    // llama a la api para obtener todos los mensajes de un chat de un evento
    public List<MensajeChatEvento> getMensajesChatEventoByChatEventoId(int idChatEvento) throws Exception {
        String url = BASE_URL + "/chat/" + idChatEvento;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            PageResponse<MensajeChatEvento> page =
                    objectMapper.readValue(response.body(),
                            new TypeReference<PageResponse<MensajeChatEvento>>() {});

            return page.getContent();
        } else {
            throw new Exception("Error al obtener mensajes de chat de evento: HTTP " + response.statusCode());
        }
    }
    // llama a la api para enviar mensaje a un chat
    public void createMensajeChatEvento(int idChat, MensajeChatEvento mensajeChatEvento) throws Exception {
        String url = BASE_URL + "/chat/" + idChat;
        String requestBody = objectMapper.writeValueAsString(mensajeChatEvento);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new Exception("Error al crear mensaje de chat de evento: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
}
