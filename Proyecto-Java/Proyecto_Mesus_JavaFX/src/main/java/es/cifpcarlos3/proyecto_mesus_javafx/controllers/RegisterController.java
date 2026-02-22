package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import es.cifpcarlos3.proyecto_mesus_javafx.MainApplication;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import es.cifpcarlos3.proyecto_mesus_javafx.services.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML
    private TextField textFieldUser;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldConfirmPassword;
    @FXML
    private Button btnRegister;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void register() {
        String user = textFieldUser.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        String confirmPassword = textFieldConfirmPassword.getText();

        if (user == null || user.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Campos incompletos",
                    "Por favor, rellene todos los campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Error", "Las contraseñas no coinciden.");
            return;
        }

        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(user);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(password);

            Usuario creado = usuarioService.createUsuario(nuevoUsuario);

            if (creado != null) {
                showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Registro exitoso",
                        "¡Usuario " + creado.getNombreUsuario() + " registrado correctamente!");

                MainApplication.setRoot("login_view");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error de registro",
                    "No se pudo registrar el usuario. Comprueba si el usuario o email ya existen.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
