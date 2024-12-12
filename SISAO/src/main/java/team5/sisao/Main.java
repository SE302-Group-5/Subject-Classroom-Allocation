package team5.sisao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main extends Application {

    private static Stage primaryStage;

    //Main menu
    @Override
    public void start(Stage stage) throws Exception {
        //get the main fxml file
        try {
            URL location = getClass().getResource("/team5/sisao/main.fxml");
            if (location == null) {
                throw new RuntimeException("FXML doc cannot be found please check the doc location.");
            }
            //load the main menu
            Parent root = FXMLLoader.load(location);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("SISAO");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) throws IOException {
        DatabaseManager db = new DatabaseManager();
        db.boot();
        System.out.println("DB connection established");
        ArrayList<String> s = new ArrayList<>();
        s.add("BORA_ALTAŞ");
        Course eren = new Course("MATTTTAAAA302","Monday 8:30","3","John","M01",s);
ScheduleManager sm = new ScheduleManager(db);
        System.out.println("An example course is created for testing purposes ");
        sm.addCourse(eren);

        launch(args);
    }
}
