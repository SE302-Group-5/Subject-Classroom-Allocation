package team5.sisao;

public class CourseManager {
    

    public void addCourse(String courseName, String dayAndStartHour, String duration,String lecturer,String clasroom, ArrayList<String> attandees){
        Course course = createCourse(courseName,dayAndStartHour,duration,lecturer,clasroom,attandees);
        boolean isUnique = isUnique(course.getCourseName());
        if (isUnique) {


            databaseManager.addCourse(course);
            databaseManager.createEnrollmentTable(course);
            ArrayList<String> list = course.getAttandees();
            for(String student : list){
                databaseManager.updateSchedule(student, course.getCourseName(), course.getDay(), course.getStartHour(), course.getDuration());
            }
        } else {
            System.out.println("there are a course with the same name you want to add");
        }


    }
    public Course createCourse(String courseName, String dayAndStartHour, String duration,String lecturer,String clasroom, ArrayList<String> attandees) {
        return new Course(courseName, dayAndStartHour,duration,clasroom,lecturer,attandees);
    }

    public boolean isUnique(String name){
        boolean unique = true;
        ArrayList<String> courseList;
        StringBuilder st = new StringBuilder("SELECT COUNT(*) AS count FROM courses WHERE courseName = " + name);

        try {
            var pstmt = databaseConnection.prepareStatement(String.valueOf(st));
            var rs = pstmt.executeQuery();
            int count = rs.getInt("count");
            if(count != 0){
                unique = false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



        return unique;
    }
}
