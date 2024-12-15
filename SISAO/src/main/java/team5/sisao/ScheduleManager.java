package team5.sisao;


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
                    if (databaseManager.isAvailable(studentCompare, day, hour, 1) && databaseManager.isAvailable(student, day, hour, 1)) {
                        freeHours.updateSchedule(freeHours, day, hour, "Y");
                    } else {
                        freeHours.updateSchedule(freeHours, day, hour, "N");
                    }
                }
            }


        }


        return freeHours;
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

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }
}