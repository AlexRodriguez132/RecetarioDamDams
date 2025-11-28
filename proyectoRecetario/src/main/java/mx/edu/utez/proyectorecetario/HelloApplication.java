package mx.edu.utez.proyectorecetario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.edu.utez.proyectorecetario.util.Email;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/mx/edu/utez/proyectorecetario/view/landingpage-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestor de Recetas - LandingPage");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
            launch();
    }

}