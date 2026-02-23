package es.cifpcarlos3.proyecto_mesus_javafx.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponse<T> {
        private List<T> content;
        private int totalPages;
        private long totalElements;
        private int size;
        private int number;
        private boolean first;
        private boolean last;

        public List<T> getContent() { return content; }
}
