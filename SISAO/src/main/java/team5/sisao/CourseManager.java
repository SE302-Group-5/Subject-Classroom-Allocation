package team5.sisao;

import java.util.ArrayList;

public class CourseManager {

    private static ScheduleManager scheduleManager;

    public void addCourse(String courseName, String day, String hour, int duration, String lecturer, String classroom, ArrayList<String> attendees) {
        DatabaseManager databaseManager = scheduleManager.getDatabaseManager();

        boolean isUnique = databaseManager.isCourseNameUnique(courseName);

        if (isUnique) {
            Course course = new Course(courseName, day, hour, duration, lecturer, classroom, attendees);
            databaseManager.addCourse(course);
            databaseManager.createEnrollmentTable(course);
            ArrayList<String> list = course.getAttandees();
            for (String student : list) {
                databaseManager.updateSchedule(student, course.getCourseName(), course.getDay(), course.getStartHour(), course.getDuration());
            }
        } else {
            System.out.println("there are a course with the same name you want to add");
        }


    }


}
