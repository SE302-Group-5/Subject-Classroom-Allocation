package team5.sisao;

import java.util.ArrayList;

public class CourseManager {


    public ScheduleManager sc;

    public Course addCourse(String courseName, String day, String hour, int duration, String lecturer, String classroom, ArrayList<String> attendees) {


        Course course = new Course(courseName, day, hour, duration, lecturer, classroom, attendees);
        sc.getDatabaseManager().createEnrollmentTable(course);
        sc.getDatabaseManager().addCourse(course);

        ArrayList<String> list = course.getAttandees();
        for (String student : list) {
            sc.getDatabaseManager().updateSchedule(student, course.getCourseName(), course.getDay(), course.getStartHour(), course.getDuration());
        }
        return course;

    }

    public CourseManager(DatabaseManager db) {
        sc = new ScheduleManager(db);
    }

}
