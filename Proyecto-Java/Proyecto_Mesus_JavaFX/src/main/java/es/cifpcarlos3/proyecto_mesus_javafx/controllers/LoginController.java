package es.cifpcarlos3.proyecto_mesus_javafx.controllers;

import com.sun.tools.javac.Main;
import es.cifpcarlos3.proyecto_mesus_javafx.MainApplication;
import es.cifpcarlos3.proyecto_mesus_javafx.models.Usuario;
import es.cifpcarlos3.proyecto_mesus_javafx.services.UsuarioService;
import es.cifpcarlos3.proyecto_mesus_javafx.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField textFieldUser;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLoginGoogle;
    @FXML
    private Label textCrearCuenta;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void login() {
        String user = textFieldUser.getText();
        String password = textFieldPassword.getText();

        if (user == null || user.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, rellene todos los campos.");
            return;
        }

        try {
            Usuario usuario = usuarioService.login(user, password);

            if (usuario != null) {
                SessionManager.setUsuarioActual(usuario);
                MainApplication.setRoot("menu_view");

            } else {
                showAlert(Alert.AlertType.ERROR, "Error de autenticación", "Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar con el servidor de la API.");
        }
    }

    @FXML
    private void loginGoogle() {
        showAlert(Alert.AlertType.INFORMATION, "Aviso", "Funcionalidad no implementada");
    }

    @FXML
    private void loadRegister() throws IOException {
        MainApplication.setRoot("register_view");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
