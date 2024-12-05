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

public class DatabaseManager {


    public static Connection databaseConnection;
    private boolean dbExists;

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
            }


            for (Course course : courses) {
                createEnrollmentTable(course);   // creating the enrollment tables for every course
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
            }

            createClassroomsTable(classrooms);
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
                        //    System.out.println(classroom.getClassroomName() +" "+classroom.getCapacity() +"    " + course.getCourseName()+" "+getEnrollmentCount(course.getCourseName()));
                        break;
                    }
                }
            }
            createCoursesTable(courses); // creates the Courses table that stores every courses info
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

        String createEnrollmentTable = "CREATE TABLE IF NOT EXISTS " + courseName + " (" +
                "student text);";
        try {
            var stmt = databaseConnection.prepareStatement(createEnrollmentTable);
            stmt.execute();

        } catch (SQLException e) {
            //   e.printStackTrace();
            System.err.println("Error with table creation: " + courseName);
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
            //  e.printStackTrace();
            System.err.println("Error with table insertion, table: " + courseName);
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
                        studentID INTEGER,
                        studentName text
                        );""";
        try {
            var stmt = databaseConnection.prepareStatement(createStudentsTable);
            stmt.execute();
        } catch (SQLException e) {
            //   e.printStackTrace();
            System.out.println("Error with table creation, table: " + "Students");

        }


        String sqlInsert = "INSERT INTO " + "Students" + "(studentID,studentName)" +
                " values (?,?)";


        try {
            var pstmt = databaseConnection.prepareStatement(sqlInsert);


            for (int i = 0; i < students.length; i++) {

                pstmt.setInt(1, i);
                pstmt.setString(2, students[i].toString());
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

    public void updateSchedule(String name, String course, int day, int startHour, int duration) {

        StringBuilder query = new StringBuilder("UPDATE " + name + " SET day =?");
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

        } catch (SQLException e) {
            //  e.printStackTrace();
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


}


