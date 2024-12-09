package team5.sisao;

import java.util.ArrayList;

public class ClassroomManager {
    private static ArrayList<Classroom> classrooms;
    private static DatabaseManager databaseManager; // I will change it to scheduleManager when it is added

    public ClassroomManager(DatabaseManager db) {
        databaseManager = db;// I will change it to scheduleManager when it is added
        classrooms = db.getClassrooms();
    }


    public ArrayList<Classroom> getAvailableClassrooms(int enrollment, int day, int hour, int duration) {
        ArrayList<Classroom> available = new ArrayList<>();
        for (Classroom classroom : classrooms) {
            if (databaseManager.isAvailable(classroom.getClassroomName(), day, hour, duration) && classroom.getCapacity() >= enrollment) {
                available.add(classroom);
            }
        }
        return available;
    }


}