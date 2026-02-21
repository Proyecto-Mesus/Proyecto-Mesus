package es.cifpcarlos3.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mensajes_chats_evento")
public class MensajeChatEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private int id;

    @Column(nullable = false, length = 500)
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "id_chat", nullable = false)
    private ChatEvento chatEvento;

    @com.fasterxml.jackson.annotation.JsonProperty("nombreRemitente")
    @Column(name = "nombre_remitente", nullable = false)
    private String nombreRemitente;

    @com.fasterxml.jackson.annotation.JsonProperty("idUsuario")
    @Column(name = "id_usuario", nullable = true)
    private Integer idUsuario;

}
