package team5.sisao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static team5.sisao.DatabaseManager.databaseConnection;


public class ScheduleManager {
    private DatabaseManager databaseManager;

    public ScheduleManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
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

    private boolean isAvailable(String name, int day, int startHour, int duration) {
        return false;
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


    public Schedule getSchedule(String schedule) {
        Schedule retrievedSchedule = new Schedule();

        try {
            var sql = "SELECT * FROM " + schedule;
            var stmt = databaseConnection.createStatement();
            var rs = stmt.executeQuery(sql);
            while (rs.next()) {
                for (int hour = 0; hour < 16; hour++) {
                    String courseAtTime = rs.getString("hour" + hour);
                    retrievedSchedule.getWeeklyProgram()[rs.getInt("day")][hour] = courseAtTime != null ? courseAtTime : "";
                }
            }
        } catch (Exception e) {
            System.err.println("Error while retrieving the schedule for: " + schedule);
        }
        return retrievedSchedule;
    }

}