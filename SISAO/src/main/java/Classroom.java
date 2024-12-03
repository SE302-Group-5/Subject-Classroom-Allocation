public class Classroom {

    private String classroomName;
    private int capacity;

    public Classroom(String classroomName, String capacity) {
        this.classroomName = classroomName;
        this.capacity = Integer.parseInt(capacity);
    }

    public String getClassroomName() {
        return classroomName;
    }

    public int getCapacity() {
        return capacity;
    }
}
