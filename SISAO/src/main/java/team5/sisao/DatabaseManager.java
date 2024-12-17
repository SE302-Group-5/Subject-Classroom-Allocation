package team5.sisao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager {


    public static Connection databaseConnection;
    private boolean dbExists;

    // TRY OUT COMMAND
    public DatabaseManager() {


        try {
            FileReader searchDB = new FileReader("sisao.db");
            System.out.println("db already exists, connected to db\n\n");
            this.dbExists = true;
        } catch (FileNotFoundException e) {
            //   System.out.println("Created new database\n\n");
            dbExists = false;
        }


        try {
            databaseConnection = DriverManager.getConnection("jdbc:sqlite:sisao.db"); // this creates the database file

        } catch (SQLException e) {
            //e.printStackTrace();
            System.err.println("Error with database connection");
        }
    }


    public void boot() {

        if (!dbExists) {

            // Courses CVS File


            ArrayList<Course> courses = new ArrayList<>();
            ArrayList<String> CourseCVSlines = new ArrayList<>();
            Set allStudents = new HashSet(); // Using HashSet because it prevents duplicates. This HashSet stores all the students in the school

            File courseFile = new File("SISAO/Courses.csv");

            FileReader fileReader = null;
            try {
                fileReader = new FileReader(courseFile);
            } catch (FileNotFoundException e) {
                //  e.printStackTrace();
                System.err.println("Error with opening the Courses.csv");
            }
            BufferedReader reader = new BufferedReader(fileReader);

            String line = "";

            while (true) {
                try {
                    if ((line = reader.readLine()) == null) break;
                } catch (IOException e) {
                    //  e.printStackTrace();
                    System.err.println("Error with reading the Courses.csv");
                }
                CourseCVSlines.add(line);
            }
            CourseCVSlines.removeFirst(); // Removing the Headers of the CVS file team5.sisao.Course;TimeToStart;DurationInLectureHours;Lecturer;Students;;;;;;

            List<String> lineTokens;

            for (String courseLine : CourseCVSlines) {
                line = courseLine;
                lineTokens = Arrays.asList(line.split(";")); // Tokenizing the line by ";"
                ArrayList<String> attendees = new ArrayList<>(); // ArrayList for the enrollment of each course

                for (int x = 4; x < lineTokens.size(); x++) {
                    // x starts from 4 because the first tokens are as follows (example):
                    // SE115 Monday 9:25 4 İlker Korkmaz
                    // student names start after token 4
                    String student = lineTokens.get(x).replaceAll(" ", "_"); // table names should not include " "
                    attendees.add(student);
                    allStudents.add(student);
                }

                courses.add(new Course(lineTokens.get(0), lineTokens.get(1), lineTokens.get(2), lineTokens.get(3), attendees));
            }


            Object[] allStudentsArray = allStudents.toArray();      // Turning the hashset to array for easier data access


            createStudentsTable(allStudentsArray); // creates the Students table that stores every student in school

            System.out.println("Created Students Table");

            for (int i = 0; i < allStudentsArray.length; i++) {    // fills in the schedules of students
                Schedule schedule = new Schedule();
                String[][] weeklyProgram = schedule.getWeeklyProgram();
                String student = allStudentsArray[i].toString();
                for (int x = 0; x < courses.size(); x++) {
                    Course course = courses.get(x);
                    if (course.getAttandees().contains(student)) {
                        int day = course.getDay();
                        int startHour = course.getStartHour();
                        int duration = course.getDuration();
                        for (int y = startHour; y < startHour + duration; y++) {
                            weeklyProgram[day][y] = course.getCourseName();
                        }
                    }


                }
                schedule = new Schedule(weeklyProgram);
                createScheduleTable(student, schedule);
                System.out.println("Created Schedule Table of: " + student);
            }


            for (Course course : courses) {
                createEnrollmentTable(course);   // creating the enrollment tables for every course
                System.out.println("Created enrollment table for: " + course.getCourseName());
            }

            // ClassroomCapacity CVS File


            ArrayList<String> ClassroomCapacityCVSlines = new ArrayList<>();
            File classroomFile = new File("SISAO/ClassroomCapacity.csv");
            try {
                fileReader = new FileReader(classroomFile);
            } catch (FileNotFoundException e) {
                System.err.println("Error with opening the ClassroomCapacity.csv");
            }
            reader = new BufferedReader(fileReader);

            while (true) {
                try {
                    if ((line = reader.readLine()) == null) break;
                } catch (IOException e) {
                    System.err.println("Error with reading the ClassroomCapacity.csv");
                }
                ClassroomCapacityCVSlines.add(line);
            }

            ClassroomCapacityCVSlines.removeFirst(); // removing the header line

            ArrayList<Classroom> classrooms = new ArrayList<>(ClassroomCapacityCVSlines.size());


            for (String classroomLine : ClassroomCapacityCVSlines) {
                line = classroomLine;
                lineTokens = Arrays.asList(line.split(";"));

                classrooms.add(new Classroom(lineTokens.get(0), lineTokens.get(1)));
            }
            Schedule schedule = new Schedule(); // creates an empty schedule
            for (Classroom classroom : classrooms) {

                createScheduleTable(classroom.getClassroomName(), schedule); // creates empty schedule tables in database for classrooms

                System.out.println("Created Schedule Table of: " + classroom.getClassroomName());
            }

            createClassroomsTable(classrooms);

            System.out.println("Created Classrooms Table");
            classrooms.sort(Comparator.comparing(Classroom::getCapacity)); // sorts the classrooms by increasing capacity order


            // team5.sisao.Classroom allocation
            for (Course course : courses) {
                int day = course.getDay();
                int startHour = course.getStartHour();
                int duration = course.getDuration();
                for (Classroom classroom : classrooms) {
                    // if the classroom is empty and has enough capacity
                    int capacity = classroom.getCapacity();
                    int enrollment = course.getAttandees().size();
                    if (isAvailable(classroom.getClassroomName(), day, startHour, duration) && capacity >= enrollment) {
                        updateSchedule(classroom.getClassroomName(), course.getCourseName(), day, startHour, duration);
                        course.setClassroom(classroom.getClassroomName());
                        System.out.println("Assigned " + classroom.getClassroomName() + " with size " + capacity + " to " + course.getCourseName() + " with size " + enrollment);
                        //    System.out.println(classroom.getClassroomName() +" "+classroom.getCapacity() +"    " + course.getCourseName()+" "+getEnrollmentCount(course.getCourseName()));
                        break;
                    }
                }
            }
            createCoursesTable(courses); // creates the Courses table that stores every courses info

            System.out.println("Created Courses Table");
            try {
                databaseConnection.close();
            } catch (SQLException e) {
                System.err.println("could not close databse connection at the end of the boot() function");
            }
            try {
                databaseConnection = DriverManager.getConnection("jdbc:sqlite:sisao.db"); // this creates the database file

            } catch (SQLException e) {
                //e.printStackTrace();
                System.err.println("Error with database connection after closing the connection at the end of boot() function");
            }
        }

    }

    public boolean isAvailable(String name, int day, int startHour, int duration) {
        boolean retVal = false;
        StringBuilder query = new StringBuilder("SELECT day FROM " + name + " WHERE day=?");
        for (int i = startHour; i < duration + startHour; i++) {
            query.append(" AND hour" + i + "=?");
        }

        try {
            PreparedStatement select = databaseConnection.
                    prepareStatement(query.toString());
            select.setInt(1, day);

            for (int i = 0; i < duration; i++) {
                select.setString(i + 2, "");
            }
            ResultSet resultSet = select.executeQuery();


            if (resultSet.next()) {
                retVal = true;
            }
        } catch (SQLException e) {
            //    e.printStackTrace();
        }


        return retVal;
    }

    public void createEnrollmentTable(Course course) {
        String courseName = course.getCourseName();
        ArrayList<String> attandees = course.getAttandees();
        courseName.replaceAll(" ", "_");
        String createEnrollmentTable = "CREATE TABLE IF NOT EXISTS " + courseName + " (" +
                "student text);";
        try {
            var stmt = databaseConnection.prepareStatement(createEnrollmentTable);
            stmt.execute();

        } catch (SQLException e) {

            System.err.println("Error with enrollment table creation: " + courseName);
            course.setCourseName(courseName + "c");
            System.out.println("changed the course name to: " + course.getCourseName());
            createEnrollmentTable(course);
           // e.printStackTrace();
            //    throw new RuntimeException();
        }
        String sqlInsert = "INSERT INTO " + courseName + "(student) " +
                "values (?)";
        try {
            var pstmt = databaseConnection.prepareStatement(sqlInsert);


            for (int i = 0; i < attandees.size(); i++) {

                pstmt.setString(1, attandees.get(i));
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {

            System.err.println("Error with enrollment table insertion, table: " + courseName);
         //   e.printStackTrace();
            //    throw new RuntimeException();
        }


    }

    private void createClassroomsTable(ArrayList<Classroom> classrooms) {
        String createClassroomsTable =

                """
                        CREATE TABLE IF NOT EXISTS Classrooms (
                        classroomName text,
                        capacity INTEGER
                        );""";
        try {
            var stmt = databaseConnection.prepareStatement(createClassroomsTable);
            stmt.execute();
        } catch (SQLException e) {
            //  e.printStackTrace();
            System.out.println("Error with table creation, table: " + "Classrooms");

        }

        String sqlInsert = "INSERT INTO " + "Classrooms" + "(classroomName,capacity)" +
                " values (?,?)";


        try {
            var pstmt = databaseConnection.prepareStatement(sqlInsert);


            for (int i = 0; i < classrooms.size(); i++) {

                pstmt.setString(1, classrooms.get(i).getClassroomName());
                pstmt.setInt(2, classrooms.get(i).getCapacity());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            //   e.printStackTrace();
            System.err.println("Error with table insertion, table: " + "Classrooms");
        }

    }

    private void createStudentsTable(Object[] students) {

        String createStudentsTable =
                """
                        CREATE TABLE IF NOT EXISTS Students (
                        studentName text
                        );""";
        try {
            var stmt = databaseConnection.prepareStatement(createStudentsTable);
            stmt.execute();
        } catch (SQLException e) {
            //   e.printStackTrace();
            System.out.println("Error with table creation, table: " + "Students");

        }


        String sqlInsert = "INSERT INTO " + "Students" + "(studentName)" +
                " values (?)";


        try {
            var pstmt = databaseConnection.prepareStatement(sqlInsert);


            for (int i = 0; i < students.length; i++) {

                pstmt.setString(1, students[i].toString());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            //    e.printStackTrace();
            System.err.println("Error with table insertion, table: " + "Classrooms");
        }

    }

    private void createCoursesTable(ArrayList<Course> courses) {

        String createCoursesTable =
                """
                        CREATE TABLE IF NOT EXISTS Courses (
                        courseName text,
                        courseDay integer,
                        courseTime integer,
                        duration integer,
                        lecturer String,
                        classroom String
                        );""";

        try {
            var stmt = databaseConnection.prepareStatement(createCoursesTable);
            stmt.execute();
        } catch (SQLException e) {
            //  e.printStackTrace();
            System.out.println("Error with table creation, table: " + "Courses");
        }

        String sqlInsert = "INSERT INTO Courses" + "(courseName,courseDay,courseTime,duration,lecturer,classroom) " +
                "values (?,?,?,?,?,?)";
        try {
            var pstmt = databaseConnection.prepareStatement(sqlInsert);


            for (int i = 0; i < courses.size(); i++) {

                pstmt.setString(1, courses.get(i).getCourseName());
                pstmt.setInt(2, courses.get(i).getDay());
                pstmt.setInt(3, courses.get(i).getStartHour());
                pstmt.setInt(4, courses.get(i).getDuration());
                pstmt.setString(5, courses.get(i).getLecturer());
                pstmt.setString(6, courses.get(i).getClassroom());
                pstmt.executeUpdate();

            }

        } catch (SQLException e) {
            //   e.printStackTrace();
            System.err.println("Error with table insertion, table: " + "Courses");
        }


    }

    public void createScheduleTable(String name, Schedule schedule) {
        String[][] weeklyProgram = schedule.getWeeklyProgram();
        var sqlTable = "CREATE TABLE IF NOT EXISTS " + name + " ("
                + "day Text,"
                + "hour0 Text, hour1 Text, hour2 Text, hour3 Text, hour4 Text, hour5 Text, hour6 Text, hour7 Text, hour8 Text, hour9 Text, hour10 Text, hour11 Text, hour12 Text, hour13 Text, hour14 Text, hour15 Text"
                + ");";
        try {
            var stmt = databaseConnection.createStatement();
            stmt.execute(sqlTable);
        } catch (SQLException e) {
            //  e.printStackTrace();
            System.out.println("Error with table creation, table: " + name);
        }
        try {
            String sqlInsert = "INSERT INTO " + name + "(day,hour0,hour1,hour2,hour3,hour4,hour5,hour6,hour7,hour8,hour9,hour10,hour11,hour12,hour13,hour14,hour15)" +
                    " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            var pstmt = databaseConnection.prepareStatement(sqlInsert);
            for (int i = 0; i < 7; i++) {
                pstmt.setInt(1, i);
                for (int x = 0; x < 16; x++) {
                    pstmt.setString(x + 2, weeklyProgram[i][x]);
                }
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Error with table insertion, table: " + name);
        }


    }

    public void updateSchedule(String tableName, String course, int day, int startHour, int duration) {

        try {
            if(databaseConnection.isClosed()) {
                databaseConnection = DriverManager.getConnection("jdbc:sqlite:sisao.db");
                //  System.out.println("it was closed");
            }
        } catch (SQLException e) {
            System.err.println("we might need to check if the connection to db is closed in each method");
            System.err.println("in updateSchedule() method");
        }

        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET day =?");
        for (int i = startHour; i < duration + startHour; i++) {
            query.append(",hour" + i + "=?");
        }
        query.append(" WHERE day=" + day);

        try {
            PreparedStatement update = databaseConnection.
                    prepareStatement(query.toString());

            update.setInt(1, day);
            for (int i = 0; i < duration; i++) {
                update.setString(i + 2, course);
            }
            update.executeUpdate();
            System.out.println("Updated Schedule of:" + tableName);

        } catch (SQLException e) {
            System.err.println("Error with updating the schedule of: " + tableName);
            //    e.printStackTrace();
        }

    }


    public int getEnrollmentCount(String courseName) {
        int retVal = 0;

        var sql = "SELECT COUNT(*) enrollmentCount FROM " + courseName;
        try {
            var stmt = databaseConnection.createStatement();
            var rs = stmt.executeQuery(sql);
            retVal = rs.getInt("enrollmentCount");
        } catch (SQLException e) {
            //    e.printStackTrace();
            System.err.println("Error with getting the attendance of: " + courseName);
        }

        return retVal;
    }

    public ArrayList<String> getEnrollment(String courseName) {
        ArrayList<String> students = new ArrayList<>();
        var sql = "SELECT student FROM " + courseName;

        try {
            var stmt = databaseConnection.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                students.add(rs.getString(1));
            }

        } catch (SQLException e) {
            //    e.printStackTrace();
            System.err.println("Error with getting the attendance of: " + courseName);
        }


        return students;
    }

    public Schedule getCommonFreeHours(ArrayList<String> students) {
        Schedule freeHours = new Schedule();
        String studentCompare = students.getFirst();
        if (students.size() == 1) {
            for (int day = 0; day < 7; day++) {
                for (int hour = 0; hour < 16; hour++) {
                    if (isAvailable(studentCompare, day, hour, 1)) {
                        freeHours.updateSchedule(freeHours, day, hour, "Y");
                    } else {
                        freeHours.updateSchedule(freeHours, day, hour, "N");
                    }
                }
            }
            return freeHours;
        }

        for (int i = 1; i < students.size(); i++) {

            String student = students.get(i);
            for (int day = 0; day < 7; day++) {
                for (int hour = 0; hour < 16; hour++) {
                    if (isAvailable(studentCompare, day, hour, 1) && isAvailable(student, day, hour, 1)) {
                        freeHours.updateSchedule(freeHours, day, hour, "Y");
                    } else {
                        freeHours.updateSchedule(freeHours, day, hour, "N");
                    }
                }
            }


        }
        return freeHours;
    }

    public ArrayList<Classroom> getClassrooms() {
        ArrayList<Classroom> classrooms = new ArrayList<>();
        var sql = "SELECT classroomName, capacity FROM Classrooms";

        try {
            var stmt = databaseConnection.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                classrooms.add(new Classroom(rs.getString("classroomName"), rs.getString("capacity")));
            }

        } catch (SQLException e) {
            //    e.printStackTrace();
            System.err.println("Error with getting the classrooms");
        }
        return classrooms;

    }

    public ArrayList<Course> getCourses() {
        System.out.println("Inside the getCourses method");
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses";

        try {
            var stmt = databaseConnection.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Extract course data from ResultSet
                String courseName = rs.getString("courseName"); // Column 1
                String day = rs.getString("courseDay");        // Column 2
                String startHour = rs.getString("courseTime"); // Column 3
                String duration = rs.getString("duration");    // Column 4
                String lecturer = rs.getString("lecturer");    // Column 5
                String classroom = rs.getString("classroom"); // column 6

                // Create and add the Course object to the list
                Course temp = new Course(courseName, Integer.parseInt(day), Integer.parseInt(startHour), Integer.parseInt(duration), lecturer, classroom);
                courses.add(temp);
            }
        } catch (SQLException e) {

            System.err.println("Error occurred while fetching courses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Operation completed successfully");
        }

        return courses;
    }


    public void addCourse(Course course) {

        String sqlInsert = "INSERT INTO Courses" + "(courseName,courseDay,courseTime,duration,lecturer,classroom) " +
                "values (?,?,?,?,?,?)";
        try {
            var pstmt = databaseConnection.prepareStatement(sqlInsert);
            pstmt.setString(1, course.getCourseName());
            pstmt.setInt(2, course.getDay());
            pstmt.setInt(3, course.getStartHour());
            pstmt.setInt(4, course.getDuration());
            pstmt.setString(5, course.getLecturer());
            pstmt.setString(6, course.getClassroom());
            pstmt.executeUpdate();
            System.out.println("Added to Courses table: " + course.getCourseName());

        } catch (SQLException e) {
            //   e.printStackTrace();
            System.err.println("Error with table insertion, table: " + "Courses");
            e.printStackTrace();

        }

    }


    public Schedule getSchedule(String schedule) {
        Schedule retrievedSchedule = new Schedule();

        try {
            String sql = "SELECT * FROM " + schedule;
            Statement stmt = databaseConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int day = rs.getInt("day");
                for (int hour = 0; hour < 16; hour++) {
                    String courseAtTime = rs.getString("hour" + hour);
                    if (courseAtTime != null) {
                        retrievedSchedule.getWeeklyProgram()[day][hour] = courseAtTime;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while retrieving the schedule for: " + schedule);
        }

        return retrievedSchedule;
    }

    public List<String> getStudents() {
        List<String> students = new ArrayList<>();

        try {
            String sql = "SELECT studentName FROM Students";
            Statement stmt = databaseConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            // int i =0;
            while (rs.next()) {
                String studentName = rs.getString("studentName");
                if (studentName != null) {
                    students.add(studentName);
                    //  System.out.println(studentName+" "+i);i++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch students from the database.");
        }

        return students;
    }

    public boolean isCourseNameUnique(String courseName) {

        boolean unique = true;

        String st = "SELECT COUNT(*) AS count FROM courses WHERE courseName =" + '"' + courseName + '"';

        try {
            var pstmt = databaseConnection.prepareStatement(st);
            var rs = pstmt.executeQuery();
            int count = rs.getInt("count");
            if (count != 0) {
                unique = false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return unique;
    }

    public void swapClassrooms(String courseName1, String courseName2) {
        Course course1 = getCourse(courseName1);
        Course course2 = getCourse(courseName2);
        if (course1 == null || course2 == null) {
            System.out.println("Courses not found");
            return;
        }
        Classroom classroom1 = getClassroom(course1.getClassroom());
        Classroom classroom2 = getClassroom(course2.getClassroom());
        if (classroom1 == null || classroom2 == null) {
            System.out.println("Classrooms not found");
            return;
        }
        //check if the capacities of to be changed align
        if (classroom1.getCapacity() >= getEnrollmentCount(course2.getCourseName()) && classroom2.getCapacity() >= getEnrollmentCount(course1.getCourseName())) {
            //emptying current classrooms for courses
            emptyClassroom(course1, classroom1);
            emptyClassroom(course2, classroom2);
            //try to change both courses' classrooms
            try {
                changeClassroom(course1.getCourseName(), classroom2.getClassroomName());
            } catch (RuntimeException e) {
                //if change classroom fails, restore original classroom
                //revert the emptied classrooms
                changeClassroom(course1.getCourseName(), classroom1.getClassroomName());
                System.out.println("Classroom swap failed: " + e.getMessage());
                return;
            }
            try {
                changeClassroom(course2.getCourseName(), classroom1.getClassroomName());
                System.out.println("Classrooms swapped successfully");
            } catch (RuntimeException e) {
                //if change classroom fails, restore original classrooms
                //revert the emptied classrooms
                changeClassroom(course2.getCourseName(), classroom2.getClassroomName());
                System.out.println("Classroom swap failed: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("Classroom capacities do not match course enrollments");
        }

    }

    private void emptyClassroom(Course course, Classroom classroom) {
        if (classroom != null && course != null && course.getClassroom() != null) {
            //remove the course from the current classroom's schedule
            int day = course.getDay();
            int startHour = course.getStartHour();
            int duration = course.getDuration();
            updateSchedule(classroom.getClassroomName(), "", day, startHour, duration);
        }
    }

    public void changeClassroom(String courseName, String classroomName) {
        Course course = getCourse(courseName);
        Classroom classroom = getClassroom(classroomName);

        if (course == null || classroom == null) {
            System.out.println("Classroom or course not found");
            return;
        }
        int enrollment = getEnrollmentCount(course.getCourseName());
        if (classroom.getCapacity() >= enrollment) {
            int day = course.getDay();
            int startHour = course.getStartHour();
            int duration = course.getDuration();

            if (isAvailable(classroom.getClassroomName(), day, startHour, duration)) {
                if (course.getClassroom() != null) {
                    //removing the course from the old classroom's schedule
                    updateSchedule(course.getClassroom(), "", day, startHour, duration);
                }
                //update the classroom's schedule
                updateSchedule(classroom.getClassroomName(), course.getCourseName(), day, startHour, duration);
                //update the course's classroom
                course.setClassroom(classroom.getClassroomName());
                //UPDATE THE DB RECORD
                String SQLString = "UPDATE Courses SET classroom = ? WHERE courseName = ?";
                try {
                    PreparedStatement pstmt = databaseConnection.prepareStatement(SQLString);
                    pstmt.setString(1, classroom.getClassroomName());
                    pstmt.setString(2, course.getCourseName());
                    pstmt.executeUpdate();
                    System.out.println("Classroom updated successfully");
                } catch (SQLException e) {
                    System.err.printf("Error while updating the classroom: %s%n", e.getMessage());
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Classroom is not available at this time");
            }
        } else {
            System.out.println("Classroom does not have enough capacity");
        }
    }

    public Course getCourse(String courseName) {
        try {
            String sql = "SELECT * FROM Courses WHERE courseName = ?";
            PreparedStatement coursepstmt = databaseConnection.prepareStatement(sql);
            coursepstmt.setString(1, courseName);
            ResultSet coursers = coursepstmt.executeQuery();
            if (coursers.next()) {

                String day = coursers.getString("courseDay");
                String startHour = coursers.getString("courseTime");
                String duration = coursers.getString("duration");
                String lecturer = coursers.getString("lecturer");
                String classroom = coursers.getString("classroom");

                return new Course(courseName, Integer.parseInt(day),
                        Integer.parseInt(startHour),
                        Integer.parseInt(duration),
                        lecturer,
                        classroom);
            }
        } catch (SQLException e) {
            System.err.printf("Error while retrieving the course from the database: %s%n", e.getMessage());
            return null;
        }
        return null;
    }

    public Classroom getClassroom(String classroomName) {
        try {
            String sql = "SELECT * FROM Classrooms WHERE classroomName = ?";
            PreparedStatement classroompstmt = databaseConnection.prepareStatement(sql);
            classroompstmt.setString(1, classroomName);
            ResultSet classroomrs = classroompstmt.executeQuery();
            if (classroomrs.next()) {
                String capacity = classroomrs.getString("capacity");
                return new Classroom(classroomName, capacity);
            }
        } catch (SQLException e) {
            System.err.printf("Error while retrieving the classroom: %s%n", e.getMessage());
            return null;
        }
        return null;
    }
    public void addStudentToCourse(String student, Course course) {
        String sqlInsert = "INSERT INTO " + course.getCourseName() + "(student) " +
                "values (?)";
        try {
            var pstmt = databaseConnection.prepareStatement(sqlInsert);
            pstmt.setString(1, student);
            pstmt.executeUpdate();
            System.out.println("Added to table: " + course.getCourseName());

        } catch (SQLException e) {
            //   e.printStackTrace();
            System.err.println("Error with table insertion, table: " + course);
            e.printStackTrace();

        }
        updateSchedule(student, course.getCourseName(), course.getDay(), course.getStartHour(), course.getDuration());

    }

    public void withdrawStudentFromCourse(String student, Course course) {
        String sqlDelete = "DELETE FROM " + course.getCourseName() + " WHERE student=? ";

        try (var conn = databaseConnection;


             var pstmt = conn.prepareStatement(sqlDelete)){

            pstmt.setString(1, student);

            // execute the delete statement
            pstmt.executeUpdate();

        } catch(SQLException e){
            System.err.println("Error with deleting studing from table: " + course);
        }
        updateSchedule(student, "", course.getDay(), course.getStartHour(), course.getDuration());

    }
}