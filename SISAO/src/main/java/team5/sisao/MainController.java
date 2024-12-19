package team5.sisao;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import java.util.ArrayList;
import java.util.Comparator;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @noinspection ALL
 * <p>
 * Euzü bi kelimatillahitammati min şerri külli şeytanin ve hammatin ve min şerri kulli aynin lamme.
 * AMIN
 */
//^^^^this is supresser for unused vars and methods
public class MainController {

    public ImageView logo;

    @FXML // ADD NEW COURSE
    public TextField txtfieldAddNewCourseName, txtfieldAddNewCourseLecturer;
    public VBox vboxAddNewCourse, vboxAddNewCourseStudents, vboxAddNewCourseSchedule, vboxViewStudentSchedule;
    public TextField txtfieldAddNewCourseSearchStudent;
    public ListView<String> listviewAddNewCourseSelectedStudents;
    public ListView<String> listviewAddNewCourseSearchStudent;
    public Button btnAddNewCourse, btnAddStudentToCourse, btnAddNewCourseConfirm, btnAddNewCourseStudentConfirm, btnAddNewCourseScheduleConfirm;
    public GridPane gridpaneAddNewCourseSchedule, gridPaneStudentSchedule, gridPaneClassroomSchedule;
    public ChoiceBox<String> choiceboxAddNewCourseDay, choiceboxAddNewCourseHour;
    public ChoiceBox<Integer> choiceboxAddNewCourseDuration;
    private String courseName, courseLecturer, courseDay, courseHour;
    private int courseDuration;
    private ArrayList<String> courseEnrollment;
    // ADD STUDENT TO COURSE
    @FXML
    public VBox vboxAddStudent;
    public TextField txtfieldAddStudentSearch;
    public ChoiceBox<String> choiceboxAddStudentCourse;
    public ListView<String> listviewAddStudentSelected;
    public ListView<String> listviewAddNewStudentSearch;
    public Button btnAddStudentConfirm;
    private ObservableList<String> addStudentEnrollment;
    private ObservableList<String> addStudentNotEnrolled;
    private Course addStudentCourse;
    private Classroom addStudentClassroom;
    private int addStudentClassroomCapacity;
    // WITDRAW STUDENT FROM COURSE
    @FXML
    public VBox vboxWithdrawStudent;
    public TextField txtfieldWithdrawStudentSearch;
    public ChoiceBox<String> choiceboxWithdrawStudentCourse;
    public ListView<String> listviewWithdrawStudentSelected;
    public ListView<String> listviewWithdrawStudentSearch;
    public Button btnWithdrawStudent;
    public Button btnWithdrawStudentConfirm;
    private ObservableList<String> withdrawStudentEnrollment;
    private Course withdrawStudentCourse;
    private Classroom withdrawStudentClassroom;


    @FXML
    public VBox vboxMain, vboxLeftMain, vboxViewCourses, vboxViewClassrooms, vboxViewStudents, vboxViewClassroomSchedule, vboxViewCourseAttendance;
    public Button btnViewClassrooms, btnViewStudents, btnViewCourses, btnStudentGoBack, btnClassroomGoBack, btnAttendanceGoBack;
    public Button btnHelp;
    public Label labelStudentSchedule, labelClassroomSchedule, labelCourseAttendance;
    public TextArea textAreaCourses, textAreaClassrooms, textAreaStudents;
    public TableView<Course> tableViewCourses;
    public TableView<Classroom> tableViewClassrooms;
    public TableView<String> tableViewStudents, tableViewCourseAttendance;
    public TableColumn<Course, String> columnCourseName, columnLecturer, columnDay, columnClassroom, columnAttendees;
    public TableColumn<Course, Integer> columnStartHour, columnDuration;
    public TableColumn<Classroom, String> columnClassroomName, columnClassroomSchedule;
    public TableColumn<Classroom, Integer> columnCapacity;
    public TableColumn<String, String> columnStudentName, columnStudentSchedule, columnScheduleStudentName;

    @FXML
    private ArrayList<VBox> vboxList = new ArrayList<>();

    private ArrayList<Classroom> classroomsList = new ArrayList<>();
    private ArrayList<Course> coursesList = new ArrayList<>();
    private ArrayList<String> studentsList = new ArrayList<>();
    private ArrayList<String> attendanceList = new ArrayList<>();
    private ObservableList<Course> observableCoursesList;
    private ObservableList<Classroom> observableClassroomsList;
    private ObservableList<String> observableStudentsList;
    private ObservableList<String> observableAttendanceList;

    private DatabaseManager db;
    private static ClassroomManager classroomManager;
    private static CourseManager courseManager;

    @FXML
    private VBox vboxChangeClassroom;
    @FXML
    private Button btnChangeClassroom, btnChangeClassroomConfirm;

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
        vboxList.add(vboxViewClassrooms);
        vboxList.add(vboxViewStudents);
        vboxList.add(vboxAddNewCourse);
        vboxList.add(vboxAddNewCourseStudents);
        vboxList.add(vboxAddNewCourseSchedule);
        vboxList.add(vboxAddStudent);
        vboxList.add(vboxWithdrawStudent);
        vboxList.add(vboxViewStudentSchedule);
        vboxList.add(vboxViewClassroomSchedule);
        vboxList.add(vboxViewCourseAttendance);
        vboxList.add(vboxChangeClassroom);


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
        clearAllTables();
        disableAllVboxes();
        enableVbox(vboxViewClassrooms);
        // Above part this necessary for all the vboxes that will be visible or invisible
        // according to user input
        System.out.println("Classrooms button clicked");

        classroomsList = this.db.getClassrooms();
        observableClassroomsList = observableArrayList(classroomsList);
        if (observableClassroomsList != null && !observableClassroomsList.isEmpty()) {
            tableViewClassrooms.setItems(observableClassroomsList);
        }
        //Make the TextArea and TableView visible
        textAreaClassrooms.setVisible(true);
        tableViewClassrooms.setVisible(true);
        textAreaClassrooms.setText("Here is the list of all classrooms: (Click to View)");
        //lists all available classrooms thats retrieved from the database in tableview
        setTableViewClassrooms();

        final Boolean[] isSelected = {false};
        ChangeListener<Classroom> listener = (observable, oldValue, newValue) -> {
            if (newValue != null && !isSelected[0]) {
                Classroom selectedClassroom = newValue;
                System.out.println("Selected: " + selectedClassroom.getClassroomName());
                showClassroomSchedule(selectedClassroom.getClassroomName());

                isSelected[0] = true;

            }
        };
        // Add the listener to the TableView
        tableViewClassrooms.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public void showClassroomSchedule(String classroomName) {
        // Make the schedule visible
        disableAllVboxes();
        enableVbox(vboxViewClassroomSchedule);
        labelClassroomSchedule.setText("Schedule for " + classroomName + ":");

        Schedule schedule = this.db.getSchedule(classroomName);
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 16; hour++) {
                String cell = schedule.getWeeklyProgram()[day][hour];
                if (!cell.equals("N") || !cell.equals(null) || !cell.equals("")) {
                    Label label = new Label(cell);
                    gridPaneClassroomSchedule.add(label, day + 1, hour + 1);
                    GridPane.setHalignment(label, HPos.CENTER);  // Horizontal center
                    GridPane.setValignment(label, VPos.CENTER);  // Vertical center

                    // Optionally set alignment for the entire GridPane to center its contents
                    gridPaneClassroomSchedule.setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        }
        // if any other button is clicked on the navigation bar or go back button the gridpane should be cleared
        btnClassroomGoBack.setOnAction(event -> {
            clearAllTables();
            ViewClassrooms();
        });
    }

    public void ViewStudents() {
        clearAllTables();
        disableAllVboxes();
        enableVbox(vboxViewStudents);
        // Above part this necessary for all the vboxes that will be visible or invisible
        // according to user input
        System.out.println("Students button clicked");
        studentsList = (ArrayList<String>) this.db.getStudents();
        observableStudentsList = observableArrayList(studentsList);
        // Bind the ObservableList to the TableView
        tableViewStudents.setItems(observableStudentsList);
        // Reset the selection model to clear any previous selections
        tableViewStudents.getSelectionModel().clearSelection();
        //Make the TextArea and TableView visible
        textAreaStudents.setVisible(true);
        tableViewStudents.setVisible(true);
        textAreaStudents.setText("Here is the list of all students: (Click to View)");
        //lists all available students thats retrieved from the database in tableview
        setTableViewStudents();

        final Boolean[] isSelected = {false};

        ChangeListener<String> selectionListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && !isSelected[0]) {
                    String selectedStudentName = newValue;
                    System.out.println("Selected: " + selectedStudentName);
                    showStudentSchedule(selectedStudentName);

                    isSelected[0] = true;

                    // Remove the listener after the first selection
                    tableViewStudents.getSelectionModel().selectedItemProperty().removeListener(this);
                }
            }
        };

        // Add the listener to the TableView
        tableViewStudents.getSelectionModel().selectedItemProperty().addListener(selectionListener);

    }

    public void showStudentSchedule(String studentName) {
        // Make the schedule visible
        disableAllVboxes();
        enableVbox(vboxViewStudentSchedule);
        labelStudentSchedule.setText("Schedule for " + formatStudentName(studentName) + ":");

        Schedule schedule = this.db.getSchedule(studentName);

        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 16; hour++) {
                String cell = schedule.getWeeklyProgram()[day][hour];
                if (!cell.equals("N")) {
                    Label label = new Label(cell);
                    gridPaneStudentSchedule.add(label, day + 1, hour + 1);
                    GridPane.setHalignment(label, HPos.CENTER);  // Horizontal center
                    GridPane.setValignment(label, VPos.CENTER);  // Vertical center
                    gridPaneStudentSchedule.setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        }
        // if any other button is clicked on the navigation bar or go back button the gridpane should be cleared

        btnStudentGoBack.setOnAction(event -> {
            clearAllTables();
            ViewStudents();
        });
    }

    public void clearAllTables() {
        clearGridPaneCells(gridPaneStudentSchedule);
        clearGridPaneCells(gridPaneClassroomSchedule);
    }

    private void clearGridPaneCells(GridPane gridPane) {
        if (gridPane.getChildren().size() > 0) {
            // Iterate over all the rows (1 to 16) and columns (1 to 7)
            for (int day = 1; day <= 7; day++) {  // Day (columns) from 1 to 7
                for (int hour = 1; hour <= 16; hour++) {  // Hour (rows) from 1 to 16
                    // Iterate over all child nodes of the GridPane
                    for (Node node : gridPane.getChildren()) {
                        // Get the row and column indices of each node
                        Integer rowIndex = GridPane.getRowIndex(node);
                        Integer columnIndex = GridPane.getColumnIndex(node);

                        // Default to 0 if row or column indices are not set
                        rowIndex = (rowIndex == null) ? 0 : rowIndex;
                        columnIndex = (columnIndex == null) ? 0 : columnIndex;

                        // If the current node is at the specified row and column, remove it
                        if (rowIndex == hour && columnIndex == day) {
                            gridPane.getChildren().remove(node);
                            break;  // Stop searching after removing the node
                        }
                    }
                }
            }
        }
    }

    public void ViewCourses() {
        clearAllTables();
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
        textAreaCourses.setText("Here is the list of available courses: (Click to View)");
        //lists all available courses thats retrieved from the database in tableview
        setTableViewCourses();

        final Boolean[] isSelected = {false};
        ChangeListener<Course> listener = (observable, oldValue, newValue) -> {
            if (newValue != null && !isSelected[0]) {
                Course selectedCourse = newValue;
                System.out.println("Selected: " + selectedCourse.getCourseName());
                showCourseAttendance(selectedCourse.getCourseName());
                isSelected[0] = true;

            }
        };
        // Add the listener to the TableView
        tableViewCourses.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public void showCourseAttendance(String courseName) {
        // Make the schedule visible
        disableAllVboxes();
        enableVbox(vboxViewCourseAttendance);
        labelCourseAttendance.setText("Attendance for " + courseName + ":");

        attendanceList = this.db.getEnrollment(courseName);
        observableAttendanceList = observableArrayList(attendanceList);
        tableViewCourseAttendance.setItems(observableAttendanceList);
        labelCourseAttendance.setVisible(true);
        tableViewCourseAttendance.setVisible(true);
        setTableViewAttendance();
        // if any other button is clicked on the navigation bar or go back button the gridpane should be cleared
        btnAttendanceGoBack.setOnAction(event -> {
            clearAllTables();
            ViewCourses();
        });
    }


    public void ViewHelp() {
        clearAllTables();
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

    //sets the table of classrooms as TableView
    public void setTableViewClassrooms() {
        // Set up the columns to display data
        columnClassroomName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClassroomName()));
        columnCapacity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());

    }

    //sets the table of students as TableView
    public void setTableViewStudents() {
        // Set up the columns to display data
        columnStudentName.setCellValueFactory(cellData -> new SimpleStringProperty(formatStudentName(cellData.getValue())));

    }

    public void setTableViewAttendance() {
        // Set up the columns to display data
        columnScheduleStudentName.setCellValueFactory(cellData -> new SimpleStringProperty(formatStudentName(cellData.getValue())));
        columnScheduleStudentName.setCellFactory(column -> {
            TableCell<String, String> cell = new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                    // Set text alignment to center
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        });
    }


    private String formatStudentName(String originalName) {
        // Replace underscores with spaces
        return originalName.replace("_", " ");
    }

    public void addNewCourseSetNameAndLecturer() {
        clearAllTables();
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


        System.out.println("Add New Course button clicked");

        btnAddNewCourseConfirm.setOnAction(event -> {
            if (!txtfieldAddNewCourseName.getText().isBlank() && !txtfieldAddNewCourseLecturer.getText().isBlank()) {

                courseName = txtfieldAddNewCourseName.getText();
                courseLecturer = txtfieldAddNewCourseLecturer.getText();
                courseName = courseName.toUpperCase();
                courseName = courseName.replaceAll(" ", "");
                courseName = courseName.replaceAll("[^\\w\\s]", "c");
                boolean isCourseNameUnique = false;
                try {
                    isCourseNameUnique = db.isCourseNameUnique(courseName);
                } catch (Exception e) {
                    showAlert("Error", courseName + " is not suitable, try a different name.");
                }


                if (isCourseNameUnique) {
                    addNewCourseSetStudents();
                } else {
                    showAlert("Error", "Course with the name " + courseName + " already exists.");
                }


            } else {
                showAlert("Error", "Please Enter the Course's and the Lecturer's Name.");
            }
        });
    }

    public void addNewCourseSetStudents() {

        disableAllVboxes();
        enableVbox(vboxAddNewCourseStudents);
        // Above part this necessary for all the vboxes that will be visible or invisible
        // according to user input

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
            } else {
                showAlert("Error", "Add Students to Course.");
            }

        });

    }

    public void addNewCourseSetSchedule() {

        choiceboxAddNewCourseDay.setValue("");
        choiceboxAddNewCourseHour.setValue("");


        if (choiceboxAddNewCourseDay.getItems().size() > 0) {
            choiceboxAddNewCourseDay.getItems().clear();

        }
        clearGridPaneCells(gridpaneAddNewCourseSchedule);
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
                    GridPane.setHalignment(label, HPos.CENTER);  // Horizontal center
                    GridPane.setValignment(label, VPos.CENTER);  // Vertical center
                    gridpaneAddNewCourseSchedule.setAlignment(javafx.geometry.Pos.CENTER);
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

                if (db.isCourseNameUnique(courseName)) {
                    addNewCourseFinal();
                } else {
                    showAlert("Error", "Course already added");
                }

            }
        });
    }


    public void addNewCourseFinal() {

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
            //    System.err.println("uuuuuuuu");
            db.updateSchedule(classroom, courseName, day, hour, courseDuration);


            //      System.out.println("Added new course NAME: " + courseName + " classroom: " + classroom + " day: " + courseDay + " coursehour: " + courseHour);
        } else {
            showAlert("Error", "There are no available classroom with enough capacity" + courseName);
            //  System.err.println("There are no available classroom with enough capacity");
        }


    }

    public void addStudent() {
        clearAllTables();
        disableAllVboxes();
        enableVbox(vboxAddStudent);

        choiceboxAddStudentCourse.setValue("");
        if (choiceboxAddStudentCourse.getItems().size() > 0) {
            choiceboxAddStudentCourse.getItems().clear();

        }
        if (listviewAddNewStudentSearch.getItems().size() > 0) {
            listviewAddNewStudentSearch.getItems().clear();
        }
        if (listviewAddStudentSelected.getItems().size() > 0) {
            listviewAddStudentSelected.getItems().clear();
        }
        if (!txtfieldAddStudentSearch.getText().isEmpty()) {
            txtfieldAddStudentSearch.clear();
        }
        ArrayList<Course> courses = db.getCourses();


        ArrayList<String> courseNames = new ArrayList<>(courses.size());
        for (Course c : courses) {
            courseNames.add(c.getCourseName());
        }
        choiceboxAddStudentCourse.getItems().addAll(courseNames);

        choiceboxAddStudentCourse.setOnAction(event -> {
            if (!choiceboxAddStudentCourse.getValue().isBlank()) {

                if (listviewAddNewStudentSearch.getItems().size() > 0) {
                    listviewAddNewStudentSearch.getItems().clear();
                }
                if (listviewAddStudentSelected.getItems().size() > 0) {
                    listviewAddStudentSelected.getItems().clear();
                }
                addStudentCourse = db.getCourse(choiceboxAddStudentCourse.getValue());

                addStudentEnrollment = observableArrayList(db.getEnrollment(addStudentCourse.getCourseName()));
                addStudentClassroom = db.getClassroom(addStudentCourse.getClassroom());

                addStudentClassroomCapacity = classroomManager.getCapacity(addStudentClassroom.getClassroomName());
                addStudentNotEnrolled = observableArrayList(db.getStudents());
                //  System.out.println("classroom capacity for: " + addStudentCourse.getCourseName() + "     " + addStudentClassroomCapacity);
                for (String student : addStudentEnrollment) {
                    if (addStudentNotEnrolled.contains(student)) {
                        addStudentNotEnrolled.remove(student);
                    }
                }
                ArrayList<String> addStudentNotEnrolledAvailable = new ArrayList<>(addStudentNotEnrolled);
                for (String student : addStudentNotEnrolled) {
                    if (!db.isAvailable(student, addStudentCourse.getDay(), addStudentCourse.getStartHour(), addStudentCourse.getDuration())) {
                        addStudentNotEnrolledAvailable.remove(student);
                        //            System.out.println("REMOVED: " + student);
                    }
                }
                addStudentNotEnrolled = observableArrayList(addStudentNotEnrolledAvailable);

            }

        });

        ObservableList<String> AddStudentselectedStudents = observableArrayList();
        txtfieldAddStudentSearch.clear();

        txtfieldAddStudentSearch.textProperty().

                addListener((observable, oldValue, newValue) ->

                {
                    //       System.out.println("Search text changed: " + newValue);  // Debug line
                    if (!choiceboxAddStudentCourse.getValue().isBlank()) {
                        ObservableList<String> filteredItems = observableArrayList();
                        if (!newValue.isBlank()) {
                            for (String item : addStudentNotEnrolled) {
                                if (item.toLowerCase().contains(newValue.toLowerCase())) {
                                    filteredItems.add(item);
                                }
                            }
                        } else {
                            // If search is empty, show all students
                            filteredItems.setAll(addStudentNotEnrolled);
                        }

                        listviewAddNewStudentSearch.setItems(filteredItems);
                        listviewAddStudentSelected.setItems(AddStudentselectedStudents);
                    }

                });

        listviewAddNewStudentSearch.setOnMouseClicked(event ->

        {
            if (listviewAddNewStudentSearch.getItems().size() > 0) {
                String selectedItem = listviewAddNewStudentSearch.getSelectionModel().getSelectedItem();

                int addedEnrollment = AddStudentselectedStudents.size() + addStudentEnrollment.size();
                if (selectedItem != null && !AddStudentselectedStudents.contains(selectedItem) && addedEnrollment < addStudentClassroomCapacity) {

                    AddStudentselectedStudents.add(selectedItem);
                } else {
                    System.err.println("    Not enough capcity in " + addStudentClassroom.getClassroomName());
                    System.err.print("  classroom capacity: " + addStudentClassroomCapacity);
                    System.err.print("  Current enrollment " + db.getEnrollmentCount(addStudentCourse.getCourseName()));
                }
            }


        });

        listviewAddStudentSelected.setOnMouseClicked(event ->

        {
            String selectedItem = listviewAddStudentSelected.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                AddStudentselectedStudents.remove(selectedItem);
            }
        });

        btnAddStudentConfirm.setOnAction(event ->

        {

            if (AddStudentselectedStudents.size() > 0) {

                for (String student : AddStudentselectedStudents) {
                    db.addStudentToCourse(student, addStudentCourse);
                }

                addStudent();
            }


        });


    }

    public void withdrawStudent() {
        clearAllTables();
        disableAllVboxes();
        enableVbox(vboxWithdrawStudent);

        choiceboxWithdrawStudentCourse.setValue("");
        if (choiceboxWithdrawStudentCourse.getItems().size() > 0) {
            choiceboxWithdrawStudentCourse.getItems().clear();

        }
        if (listviewWithdrawStudentSearch.getItems().size() > 0) {
            listviewWithdrawStudentSearch.getItems().clear();
        }
        if (listviewWithdrawStudentSelected.getItems().size() > 0) {
            listviewWithdrawStudentSelected.getItems().clear();
        }
        if (!txtfieldWithdrawStudentSearch.getText().isEmpty()) {
            txtfieldWithdrawStudentSearch.clear();
        }
        ArrayList<Course> courses = db.getCourses();


        ArrayList<String> courseNames = new ArrayList<>(courses.size());
        for (Course c : courses) {
            if (db.getEnrollmentCount(c.getCourseName()) > 0) {
                courseNames.add(c.getCourseName());
            }

        }
        choiceboxWithdrawStudentCourse.getItems().addAll(courseNames);

        choiceboxWithdrawStudentCourse.setOnAction(event -> {
            if (!choiceboxWithdrawStudentCourse.getValue().isBlank()) {

                if (listviewWithdrawStudentSearch.getItems().size() > 0) {
                    listviewWithdrawStudentSearch.getItems().clear();
                }
                if (listviewWithdrawStudentSelected.getItems().size() > 0) {
                    listviewWithdrawStudentSelected.getItems().clear();
                }
                withdrawStudentCourse = db.getCourse(choiceboxWithdrawStudentCourse.getValue());

                withdrawStudentEnrollment = observableArrayList(db.getEnrollment(withdrawStudentCourse.getCourseName()));
                withdrawStudentClassroom = db.getClassroom(withdrawStudentCourse.getClassroom());


                //  listviewAddNewStudentSearch.setItems(withdrawStudentEnrollment);
                withdrawStudentFillSearchList();
            }

        });

        ObservableList<String> WithdrawStudentselectedStudents = observableArrayList();
        txtfieldWithdrawStudentSearch.clear();


        txtfieldWithdrawStudentSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            //       System.out.println("Search text changed: " + newValue);  // Debug line
            if (!choiceboxWithdrawStudentCourse.getValue().isBlank()) {
                ObservableList<String> filteredItems = observableArrayList();

                if (!newValue.isBlank()) {
                    for (String item : withdrawStudentEnrollment) {
                        if (item.toLowerCase().contains(newValue.toLowerCase())) {
                            filteredItems.add(item);
                        }
                    }
                } else {
                    // If search is empty, show all students
                    filteredItems.addAll(withdrawStudentEnrollment);
                }
                listviewWithdrawStudentSearch.setItems(filteredItems);

            }

        });

        listviewWithdrawStudentSearch.setOnMouseClicked(event -> {
            if (listviewWithdrawStudentSearch.getItems().size() > 0) {
                String selectedItem = listviewWithdrawStudentSearch.getSelectionModel().getSelectedItem();
                if (!WithdrawStudentselectedStudents.contains(selectedItem)) {
                    WithdrawStudentselectedStudents.add(selectedItem);
                }
                listviewWithdrawStudentSelected.setItems(WithdrawStudentselectedStudents);
            }
        });

        listviewWithdrawStudentSelected.setOnMouseClicked(event -> {
            String selectedItem = listviewWithdrawStudentSelected.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                WithdrawStudentselectedStudents.remove(selectedItem);
            }
        });

        btnWithdrawStudentConfirm.setOnAction(event ->

        {
            if (WithdrawStudentselectedStudents.size() > 0) {

                for (String student : WithdrawStudentselectedStudents) {
                    db.withdrawStudentFromCourse(student, withdrawStudentCourse);
                }

                withdrawStudent();
                showAlert("Success", "Wİthdrawn Students.");
            }
        });


    }

    public void withdrawStudentFillSearchList() {
        if (listviewWithdrawStudentSearch.getItems().size() > 0) {
            listviewWithdrawStudentSearch.getItems().clear();
        }
        listviewWithdrawStudentSearch.setItems(withdrawStudentEnrollment);
    }

    @FXML
    private TextField txtCourseName, txtClassroomName;

    @FXML
    private void handleChangeClassroom() {

        clearAllTables();
        disableAllVboxes();
        enableVbox(vboxChangeClassroom);


            txtClassroomName.clear();
            txtCourseName.clear();

        btnChangeClassroomConfirm.setOnAction(event -> {
            if (!txtCourseName.getText().isBlank() && !txtClassroomName.getText().isBlank()) {
                String courseName = txtCourseName.getText();
                String classroomName = txtClassroomName.getText();
                changeClassroom(courseName, classroomName);
            } else {
                showAlert("Error", "Please provide both course and classroom names!");
                return;

            }
        });


    }

    public void changeClassroom(String courseName, String classroomName) {
        Course course = db.getCourse(courseName);
        Classroom classroom = db.getClassroom(classroomName);

        if (course == null || classroom == null) {
            showAlert("Error", "Classroom or course not found.");
            return;
        }

        int enrollment = db.getEnrollmentCount(courseName);
        if (classroom.getCapacity() >= enrollment) {
            int day = course.getDay();
            int startHour = course.getStartHour();
            int duration = course.getDuration();

            if (db.isAvailable(classroomName, day, startHour, duration)) {
                if (course.getClassroom() != null) {
                    db.updateSchedule(course.getClassroom(), "", day, startHour, duration);
                }

                db.updateSchedule(classroomName, courseName, day, startHour, duration);
                course.setClassroom(classroomName);

                try {
                    db.updateCourseClassroom(courseName, classroomName);
                    showAlert("Success", "Classroom successfully updated!");
                } catch (Exception e) {
                    showAlert("Error", "Failed to update the classroom: " + e.getMessage());
                }
            } else {
                showAlert("Error", "Classroom is not available at the specified time.");
            }
        } else {
            showAlert("Error", "Classroom does not have enough capacity.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}