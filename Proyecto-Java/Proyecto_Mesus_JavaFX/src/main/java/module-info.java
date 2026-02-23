module es.cifpcarlos3.proyecto_mesus_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires jdk.compiler;
    requires javafx.graphics;
    requires cloudinary.core;
    requires javafx.base;
    requires google.maps.services;
    requires javafx.web;
    requires jdk.jsobject;

    opens es.cifpcarlos3.proyecto_mesus_javafx to javafx.fxml;

    exports es.cifpcarlos3.proyecto_mesus_javafx;

    exports es.cifpcarlos3.proyecto_mesus_javafx.controllers;

    opens es.cifpcarlos3.proyecto_mesus_javafx.controllers to javafx.fxml;

    exports es.cifpcarlos3.proyecto_mesus_javafx.models;

    opens es.cifpcarlos3.proyecto_mesus_javafx.models to com.fasterxml.jackson.databind;

    exports es.cifpcarlos3.proyecto_mesus_javafx.services;
}