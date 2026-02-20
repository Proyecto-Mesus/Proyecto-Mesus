package es.cifpcarlos3.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats_evento")
public class ChatEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat")
    private int id;

    @OneToOne
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento")
    private Evento evento;

    @JsonIgnore
    @OneToMany(
            mappedBy = "chatEvento",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MensajeChatEvento> mensajes;

}
