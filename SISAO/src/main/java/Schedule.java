

public class Schedule {

    private String[][] weeklyProgram ;

    public Schedule(String[][] weeklyProgram){
        this.weeklyProgram =weeklyProgram;
    }
    public Schedule(){
        this.weeklyProgram = new String[7][16];

        for(int i =0;i<7;i++){
            for(int x =0;x<16;x++){
                weeklyProgram[i][x]="";
            }
        }

    }


    public String[][] getWeeklyProgram() {
        return weeklyProgram;
    }
}
