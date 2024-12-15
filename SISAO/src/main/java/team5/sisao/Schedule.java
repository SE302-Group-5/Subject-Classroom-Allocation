package team5.sisao;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    public ArrayList<String> getAvailableDaysCommonSchedule() {
        //ONLY USE FOR COMMON FREE HOUR SCHEDULES
        ArrayList<String> freeDays = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 16; hour++) {
                if (this.weeklyProgram[day][hour].equals("Y")) {
                    freeDays.add(getDayString(day));
                    break;
                }
            }
        }

        return freeDays;
    }

    public ArrayList<String> getAvailableHoursCommonSchedule(String day) {
        //ONLY USE FOR COMMON FREE HOUR SCHEDULES
        ArrayList<String> freeHours = new ArrayList<>();
        int iday = getDayInteger(day);
        for (int hour = 0; hour < 16; hour++) {
            if (this.weeklyProgram[iday][hour].equals("Y")) {
                freeHours.add(getHourString(hour));

            }
        }


        return freeHours;
    }

    public ArrayList<Integer> getAvailableDurationCommonSchedule(String day, String hour) {
        //ONLY USE FOR COMMON FREE HOUR SCHEDULES
        ArrayList<Integer> retval = new ArrayList<>();
        // System.out.println("DAY: " + day + " HOUR: " + hour);
        int duration = 0;

        int startHour = getStartHourInteger(hour);
        int iday = getDayInteger(day);
        for (int i = startHour; i < 16; i++) {
            if (this.weeklyProgram[iday][i].equals("Y")) {
                duration++;
                retval.add(duration);
            } else {
                break;
            }
        }

        return retval;
    }

    public String getHourString(int startHour) {

        LocalTime hour = LocalTime.of(8, 30);

        for (int i = 0; i < startHour; i++) {
            hour = hour.plusMinutes(55);
        }
        //      System.out.println(hour);
        return hour.toString();


    }


    public String getDayString(int day) {
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

    public String[][] getWeeklyProgram() {
        return weeklyProgram;
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

    public Schedule updateSchedule(Schedule schedule, int day, int hour, String courseToChange) {
        String[][] current = schedule.getWeeklyProgram();
        current[day][hour] = courseToChange;
        schedule.setWeeklyProgram(current);
        return schedule;
    }


    public void setWeeklyProgram(String[][] weeklyProgram) {
        this.weeklyProgram = weeklyProgram;
    }

    public void schedulePrint(Schedule schedule) {

        String[][] weeklyProgram = schedule.getWeeklyProgram();
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 16; hour++) {
                System.out.print(weeklyProgram[day][hour] + " ");
            }
            System.out.println();
        }
    }
}
