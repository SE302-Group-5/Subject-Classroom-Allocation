package team5.sisao;

public class Main {

    public static void main(String[] args) {
    DatabaseManager db =new DatabaseManager();
    db.boot();
    System.out.println("DB connection established");
    }
}
