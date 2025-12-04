module mx.edu.utez.proyectorecetario {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires jakarta.mail;
    requires layout;
    requires kernel;
    requires io;


    opens mx.edu.utez.proyectorecetario to javafx.fxml;
    opens mx.edu.utez.proyectorecetario.controller to javafx.fxml; // <<-- abrir el paquete del LoginController
    exports mx.edu.utez.proyectorecetario;
}