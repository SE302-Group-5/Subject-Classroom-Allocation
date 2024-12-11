package team5.sisao;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.ArrayList;
import static javafx.collections.FXCollections.observableArrayList;

/**
 * @noinspection ALL
 */
//^^^^this is supresser for unused vars and methods
public class MainController {

    public ImageView logo;
    //Buttons
    @FXML
    private Button btnViewClassrooms, btnViewStudents, btnViewCourses;
    @FXML
    private Button btnHelp;
    @FXML
    private TextArea textAreaCourses;
    @FXML
    private TableView<Course> tableViewCourses;
    @FXML
    private TableColumn<Course, String> columnCourseName;
    @FXML
    private TableColumn<Course, Integer> columnStartHour;
    @FXML
    private TableColumn<Course, Integer> columnDuration;
    @FXML
    private TableColumn<Course, String> columnLecturer;
    @FXML
    private TableColumn<Course, String> columnDay;
    @FXML
    private TableColumn<Course, String> columnClassroom;
    @FXML
    private TableColumn<Course, Integer> columnAttendees;


    private ArrayList<Course> coursesList = new ArrayList<>();
    private ObservableList<Course> observableCoursesList;

    private DatabaseManager db;

    //initializes logo
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/team5/sisao/logo.png").toExternalForm()));
        this.db = new DatabaseManager();

    }

    //View methods
    public void ViewClassrooms() {
        System.out.println("Classrooms button clicked");
    }

    public void ViewStudents() {
        System.out.println("Students button clicked");
    }

    public void ViewCourses() {

        System.out.println("Courses button clicked");
        coursesList = this.db.getCourses();
        observableCoursesList = observableArrayList(coursesList);
        // Bind the ObservableList to the TableView
        tableViewCourses.setItems(observableCoursesList);

        //Make the TextArea and TableView visible
        textAreaCourses.setVisible(true);
        tableViewCourses.setVisible(true);
        textAreaCourses.setText("Here is the list of available courses:");
        //lists all available courses thats retrieved from the database in tableview
        setTableViewCourses();
    }

    public void ViewHelp() {
        System.out.println("Help button clicked");
    }

    //sets the table of courses as TableView
    public void setTableViewCourses() {
        // Set up the columns to display data
        columnCourseName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        columnStartHour.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStartHour()).asObject());
        columnDuration.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDuration()).asObject());
        columnLecturer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLecturer()));
        columnDay.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDayString()));
        columnClassroom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClassroom()));
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