package team5.sisao;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Course {
    private String courseName;
    private int startHour;
    private int duration;

    private String lecturer;
    private int day;
    private String classroom;
    private ArrayList<String> attandees;

    public Course(String courseName, String dayAndStartHour, String duration, String lecturer, ArrayList<String> attandees) {
        String[] dayAndHour = dayAndStartHour.split(" ");
        this.day = getDayInteger(dayAndHour[0]);
        this.startHour = getStartHourInteger(dayAndHour[1]);
        this.courseName = courseName;
        this.lecturer = lecturer;
        this.duration = Integer.parseInt(duration);
        this.attandees = attandees;
    }
	
	public Course(String courseName, int courseDay, int courseTime, int duration,String lecturer,String classroom) {
        this.courseName = courseName;
        this.day = courseDay;
        this.startHour = courseTime;
        this.duration = duration;
        this.lecturer = lecturer;
        this.classroom = classroom;

    }
    // constructor for addCourse()
    public Course(String courseName, String courseDay,String courseTime, int duration, String lecturer,String classroom, ArrayList<String> attandees) {
        this.day = getDayInteger(courseDay);
        this.startHour = getStartHourInteger(courseTime);
        this.duration = duration;
        this.courseName = courseName;
        this.lecturer = lecturer;

        this.classroom = classroom;
        this.attandees = attandees;
    }


    public void addClassroom() {
    }

    public int getDayInteger(String day) {
        int retval = -1;
        if (day.toLowerCase().contentEquals("monday")) {
            retval = 0;
        } else if (day.toLowerCase().contentEquals("tuesday")) {
            retval = 1;
        } else if (day.toLowerCase().contentEquals("wednesday")) {
            retval = 2;
        } else if (day.toLowerCase().contentEquals("thursday")) {
            retval = 3;
        } else if (day.toLowerCase().contentEquals("friday")) {
            retval = 4;
        } else if (day.toLowerCase().contentEquals("saturday")) {
            retval = 5;
        } else {
            retval = 6;
        }
        return retval;

    }

    public int getStartHourInteger(String startHour) {
        int retval = -1;
        LocalTime hour = LocalTime.of(8, 30);

        DateTimeFormatter parser = DateTimeFormatter.ofPattern("H:mm");
        LocalTime startHourLocalTime = LocalTime.parse(startHour, parser);


        for (int i = 0; i < 16; i++) {

            if (startHourLocalTime.equals(hour)) {
                retval = i;
                return retval;
            }
            hour = hour.plusMinutes(55);
        }

        return retval;
    }


    private void test() {

        System.out.println(courseName + " " + lecturer + " duration: " + duration + " start: " + startHour + " day: " + day);


    }


    public String getCourseName() {
        return courseName;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getDuration() {
        return duration;
    }

    public String getLecturer() {
        return lecturer;
    }

    public int getDay() {
        return day;
    }
    public String getDayString() {
        String retval = "";
        if (day == 0) {
            retval = "Monday";
        } else if (day == 1) {
            retval = "Tuesday";
        } else if (day == 2) {
            retval = "Wednesday";
        } else if (day == 3) {
            retval = "Thursday";
        } else if (day == 4) {
            retval = "Friday";
        } else if (day == 5) {
            retval = "Saturday";
        } else {
            retval = "Sunday";
        }
        return retval;
    }

    public String getClassroom() {
        return classroom;
    }

    public ArrayList<String> getAttandees() {
        return attandees;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}


