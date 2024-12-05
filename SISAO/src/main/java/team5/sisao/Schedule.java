package team5.sisao;

public class Schedule {

    private String[][] weeklyProgram;

    public Schedule(String[][] weeklyProgram) {
        this.weeklyProgram = weeklyProgram;
    }

    public Schedule() {
        this.weeklyProgram = new String[7][16];

        for (int i = 0; i < 7; i++) {
            for (int x = 0; x < 16; x++) {
                weeklyProgram[i][x] = "";
            }
        }

    }


    public String[][] getWeeklyProgram() {
        return weeklyProgram;
    }

    public Schedule updateSchedule(Schedule schedule, int day, int hour, String word) {
        String[][] current = schedule.getWeeklyProgram();
        current[day][hour] = word;
        schedule.setWeeklyProgram(current);
        return schedule;
    }


    public void setWeeklyProgram(String[][] weeklyProgram) {
        this.weeklyProgram = weeklyProgram;
    }

    public void schdulePrint(Schedule schedule) {

        String[][] weeklyProgram = schedule.getWeeklyProgram();
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 16; hour++) {
                System.out.print(weeklyProgram[day][hour] + " ");
            }
            System.out.println();
        }
    }
}
