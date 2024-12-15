package team5.sisao;

import java.util.ArrayList;

public class CourseManager {

    private static DatabaseManager db;

    public Course addCourse(String courseName, String day, String hour, int duration, String lecturer, String classroom, ArrayList<String> attendees) {


        Course course = new Course(courseName, day, hour, duration, lecturer, classroom, attendees);

        db.createEnrollmentTable(course);
        db.addCourse(course);

        ArrayList<String> list = course.getAttandees();
        for (String student : list) {
            db.updateSchedule(student, course.getCourseName(), course.getDay(), course.getStartHour(), course.getDuration());
        }
        return course;

    }

    public CourseManager(DatabaseManager db) {
        this.db = db;
    }

}
