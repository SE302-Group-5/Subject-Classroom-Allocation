package team5.sisao;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * @noinspection ALL
 */
//^^^^this is supresser for unused vars and methods
public class MainController {

    //Buttons
    @FXML
    private Button btnViewClassrooms, btnViewStudents, btnViewCourses;
    @FXML
    private Button btnHelp;

    //View methods
    public void ViewClassrooms() {
        System.out.println("Classrooms button clicked");
    }

    public void ViewStudents() {
        System.out.println("Students button clicked");
    }

    public void ViewCourses() {
        System.out.println("Courses button clicked");
    }

    public void ViewHelp() {
        System.out.println("Help button clicked");
    }

    // Other methods to be implemented
    public void handleAdd() {
        System.out.println("Add button clicked");

    }

    public void handleChange() {
        System.out.println("Change button clicked");

    }

    public void handleDelete() {
        System.out.println("Delete button clicked");

    }
}

//Class to change the current pane
//retrieves corresponding FXML doc
//e.g. classroomAdd.fxml
class SceneSwitcher {

    public static void switchScene(String fxmlFile) {
        try {
            Stage stage = Main.getPrimaryStage();
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource("/team5/sisao/main.fxml" + fxmlFile));
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}