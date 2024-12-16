package team5.sisao;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @noinspection ALL
 */
//^^^^this is supresser for unused vars and methods
public class MainController {

    public ImageView logo;
    private static ClassroomManager classroomManager;
    private static CourseManager courseManager;
    @FXML
    public TextField txtfieldAddNewCourseName, txtfieldAddNewCourseLecturer;
    public VBox vboxAddNewCourse, vboxAddNewCourseStudents, vboxAddNewCourseSchedule;
    public TextField txtfieldAddNewCourseSearchStudent;
    public ListView<String> listviewAddNewCourseSelectedStudents;
    public ListView<String> listviewAddNewCourseSearchStudent;
    public Button btnAddNewCourse, btnAddStudentToCourse, btnAddNewCourseConfirm, btnAddNewCourseStudentConfirm, btnAddNewCourseScheduleConfirm;
    public GridPane gridpaneAddNewCourseSchedule;
    public ChoiceBox<String> choiceboxAddNewCourseDay, choiceboxAddNewCourseHour;
    public ChoiceBox<Integer> choiceboxAddNewCourseDuration;
    private String courseName, courseLecturer, courseDay, courseHour;
    private int courseDuration;
    private ArrayList<String> courseEnrollment;

    @FXML
    private VBox vboxMain, vboxLeftMain, vboxViewCourses;
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

    private ArrayList<VBox> vboxList = new ArrayList<>();

    private ArrayList<Course> coursesList = new ArrayList<>();
    private ObservableList<Course> observableCoursesList;

    private DatabaseManager db;


    //initializes logo
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/team5/sisao/logo.png").toExternalForm()));
        this.db = new DatabaseManager();
        this.classroomManager = new ClassroomManager(this.db);
        this.courseManager = new CourseManager(this.db);
        vboxList = new ArrayList<VBox>();
        // Only add the vboxes which you want to disable to the vboxList
        // Never put main vboxes
        // My idea was to simulate screen change by disabling and enabling vboxes
        // could be stupid
        // These vboxes are stored in a stackpane in FXML so they are on top of each other
        vboxList.add(vboxViewCourses);

        vboxList.add(vboxAddNewCourse);
        vboxList.add(vboxAddNewCourseStudents);
        vboxList.add(vboxAddNewCourseSchedule);
        disableAllVboxes();
    }

    private void disableVbox(VBox vbox) {
        vbox.setDisable(true);
        vbox.setVisible(false);
    }

    private void enableVbox(VBox vbox) {
        vbox.setDisable(false);
        vbox.setVisible(true);
    }

    private void disableAllVboxes() {
        for (VBox vbox : vboxList) {
            disableVbox(vbox);
        }
    }

    //View methods
    public void ViewClassrooms() {
        System.out.println("Classrooms button clicked");
    }

    public void ViewStudents() {
        System.out.println("Students button clicked");
    }

    public void ViewCourses() {
        disableAllVboxes();
        enableVbox(vboxViewCourses);
        // Above part this necessary for all the vboxes that will be visible or invisible
        // according to user input

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

    public void addNewCourseSetNameAndLecturer() {
        disableAllVboxes();
        enableVbox(vboxAddNewCourse);
        // Above part this necessary for all the vboxes that will be visible or invisible
        // according to user input
        listviewAddNewCourseSelectedStudents.getItems().clear();
        listviewAddNewCourseSearchStudent.getItems().clear();
        txtfieldAddNewCourseName.clear();
        txtfieldAddNewCourseLecturer.clear();
        // Clearing operations above are useful when user presses Add New Course again
        // This makes it seem like the screen was reloaded

        btnAddNewCourseConfirm.setDisable(true);

        System.out.println("Add New Course button clicked");

        txtfieldAddNewCourseName.textProperty().addListener((observable, oldValue, newValue) -> toggleAddNewCourseConfirmButton());
        txtfieldAddNewCourseLecturer.textProperty().addListener((observable, oldValue, newValue) -> toggleAddNewCourseConfirmButton());
        // Goal here was to make the button disabled when fields are empty
        // Could have just made it so that the button is enabled but does nothing
    }

    public void toggleAddNewCourseConfirmButton() {
        if (!txtfieldAddNewCourseName.getText().isBlank() && !txtfieldAddNewCourseLecturer.getText().isBlank()) {

            btnAddNewCourseConfirm.setDisable(false);

            // Enable the button
        } else {
            btnAddNewCourseConfirm.setDisable(true);
        }
    }

    public void addNewCourseSetStudents() {

        disableAllVboxes();
        enableVbox(vboxAddNewCourseStudents);
        // Above part this necessary for all the vboxes that will be visible or invisible
        // according to user input

        courseName = txtfieldAddNewCourseName.getText();
        courseLecturer = txtfieldAddNewCourseLecturer.getText();

        ObservableList<String> students = observableArrayList(db.getStudents());
        ObservableList<String> selectedStudents = observableArrayList();
        txtfieldAddNewCourseSearchStudent.clear();

        txtfieldAddNewCourseSearchStudent.textProperty().addListener((observable, oldValue, newValue) -> {
            //       System.out.println("Search text changed: " + newValue);  // Debug line

            ObservableList<String> filteredItems = observableArrayList();


            if (!newValue.isBlank()) {
                for (String item : students) {
                    if (item.toLowerCase().contains(newValue.toLowerCase())) {
                        filteredItems.add(item);
                    }
                }
            } else {
                // If search is empty, show all students
                filteredItems.clear();
            }

            // Debugging: Check the filtered items
            //      System.out.println("Filtered items: " + filteredItems);  // Debug line

            // Set the filtered items in the ListView
            listviewAddNewCourseSearchStudent.setItems(filteredItems);
            listviewAddNewCourseSelectedStudents.setItems(selectedStudents);
        });

        listviewAddNewCourseSearchStudent.setOnMouseClicked(event -> {
            // Get the selected item from the ListView directly (no need to use toString())
            String selectedItem = listviewAddNewCourseSearchStudent.getSelectionModel().getSelectedItem();

            // Ensure the item is not null and it isn't already in the selected students list
            if (selectedItem != null && !selectedStudents.contains(selectedItem)) {
                selectedStudents.add(selectedItem);
                //      System.out.println("Added to selected: " + selectedItem);  // Debugging line
            }
        });

        listviewAddNewCourseSelectedStudents.setOnMouseClicked(event -> {
            String selectedItem = listviewAddNewCourseSelectedStudents.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedStudents.remove(selectedItem);
            }
        });
        btnAddNewCourseStudentConfirm.setOnAction(event -> {

            if (selectedStudents.size() > 0) {
                addNewCourseSetSchedule();
            }


        });

    }

    public void addNewCourseSetSchedule() {

        choiceboxAddNewCourseDay.setValue("");
        choiceboxAddNewCourseHour.setValue("");

        if (choiceboxAddNewCourseDay.getItems().size() > 0) {
            choiceboxAddNewCourseDay.getItems().clear();

        }
        clearGridPaneCells();
        disableAllVboxes();
        enableVbox(vboxAddNewCourseSchedule);
        courseDuration = 0;
        courseEnrollment = new ArrayList<String>();

        for (String s : listviewAddNewCourseSelectedStudents.getItems()) {
            courseEnrollment.add(s);
            //  System.out.println(s);
        }

        Schedule commonSchedule = db.getCommonFreeHours(courseEnrollment);
//        for(int i=0;i<16;i++){
//            commonSchedule.getWeeklyProgram()[0][i]="N";
//        }
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 16; hour++) {
                if (!commonSchedule.getWeeklyProgram()[day][hour].equals("N")) {
                    Label label = new Label("Available");
                    gridpaneAddNewCourseSchedule.add(label, day + 1, hour + 1);
                    //   System.out.println(day + " " + hour);
                }

            }
        }
        choiceboxAddNewCourseDay.getItems().addAll(commonSchedule.getAvailableDaysCommonSchedule());


        choiceboxAddNewCourseDay.setOnAction(event -> {


            String selectedDay = choiceboxAddNewCourseDay.getValue();
            courseDay = selectedDay;

            choiceboxAddNewCourseHour.getItems().clear();
            choiceboxAddNewCourseHour.setValue("");
            choiceboxAddNewCourseDuration.getItems().clear();
            choiceboxAddNewCourseDuration.setValue(0);

            ArrayList<String> availableHours = commonSchedule.getAvailableHoursCommonSchedule(selectedDay);


            choiceboxAddNewCourseHour.getItems().addAll(availableHours);
        });


        choiceboxAddNewCourseHour.setOnAction(event -> {


            String selectedHour = choiceboxAddNewCourseHour.getValue();
            courseHour = selectedHour;
            if (choiceboxAddNewCourseHour.getItems().size() > 0) {
                choiceboxAddNewCourseDuration.getItems().clear();
                ArrayList<Integer> availableHours = commonSchedule.getAvailableDurationCommonSchedule(courseDay, selectedHour);

                choiceboxAddNewCourseDuration.getItems().addAll(availableHours);
            }


        });
        choiceboxAddNewCourseDuration.setOnAction(event -> {
            if (choiceboxAddNewCourseDuration.getValue() != null) {
                courseDuration = choiceboxAddNewCourseDuration.getValue();
            }

        });
        btnAddNewCourseScheduleConfirm.setOnAction(event -> {
            if (courseDuration != 0) {
                addNewCourseFinal();
            }
        });
    }

    private void clearGridPaneCells() {
        if (gridpaneAddNewCourseSchedule.getChildren().size() > 0) {
            // Iterate over all the rows (1 to 16) and columns (1 to 7)
            for (int day = 1; day <= 7; day++) {  // Day (columns) from 1 to 7
                for (int hour = 1; hour <= 16; hour++) {  // Hour (rows) from 1 to 16
                    // Iterate over all child nodes of the GridPane
                    for (Node node : gridpaneAddNewCourseSchedule.getChildren()) {
                        // Get the row and column indices of each node
                        Integer rowIndex = GridPane.getRowIndex(node);
                        Integer columnIndex = GridPane.getColumnIndex(node);

                        // Default to 0 if row or column indices are not set
                        rowIndex = (rowIndex == null) ? 0 : rowIndex;
                        columnIndex = (columnIndex == null) ? 0 : columnIndex;

                        // If the current node is at the specified row and column, remove it
                        if (rowIndex == hour && columnIndex == day) {
                            gridpaneAddNewCourseSchedule.getChildren().remove(node);
                            break;  // Stop searching after removing the node
                        }
                    }
                }
            }
        }
    }

    public void addNewCourseFinal() {

        courseName = courseName.toUpperCase();
        courseName = courseName.replaceAll(" ", "");
        courseName = courseName.replaceAll("[^\\w\\s]", "c");
        if (courseName.isBlank()) {
            courseName = "course";
        }

        if (db.isCourseNameUnique(courseName)) {

            String classroom = "";
            Schedule s = new Schedule();
            int day = s.getDayInteger(courseDay);
            int hour = s.getStartHourInteger(courseHour);
            ArrayList<Classroom> availableClassrooms = classroomManager.getAvailableClassrooms(courseEnrollment.size(), day, hour, courseDuration);
            availableClassrooms.sort(Comparator.comparing(Classroom::getCapacity));

            if (availableClassrooms.size() > 0) {
                classroom = availableClassrooms.getFirst().getClassroomName();


                Course newCourse = courseManager.addCourse(courseName, courseDay, courseHour, courseDuration, courseLecturer, classroom, courseEnrollment);
                courseName = newCourse.getCourseName();

                db.updateSchedule(classroom, courseName, day, hour, courseDuration);
                //      System.out.println("Added new course NAME: " + courseName + " classroom: " + classroom + " day: " + courseDay + " coursehour: " + courseHour);
            } else {
                System.err.println("There are no available classroom with enough capacity");
            }
        } else {
            System.err.println("a course with that name already exists: " + courseName);
        }


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
}