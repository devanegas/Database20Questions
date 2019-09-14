import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    public static int id = 3;
    public static String dbName = "dataBase.db";
    public static String path = "jdbc:sqlite:C:/Users/diego.vanegaszuniga/Desktop/Database20Questions/";
    public static String directory = "C:/Users/diego.vanegaszuniga/Desktop/Database20Questions/";

    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);

        //Setup of the databases
        setup();

        //Driver
        String choice = "yes";
        Main demo = new Main();
        System.out.println("\n [Your database currently looks like this]\n");
        System.out.println("Id\tQ/A\tY\tN\tQuestion");
        demo.selectAll();


        while(choice.equals("yes")) {
            System.out.println("\n\t\t\t[Welcome to 20+ Questions]\n>> Think about a person/thing and I will try to guess it <<");
            System.out.println("Answer [Yes/No]:");
            recursiveTest(0, 0, true, demo);

            System.out.printf("\n\n> Do you want to play again? [Yes/No]: ");
            choice = cin.nextLine();
            choice=choice.toLowerCase();
        }
        //demo.deleteAll();
        //System.out.println("Id\tQ/A\tY\tN\tQuestion");
        //demo.selectAll();
    }

    public static void setup(){
        Scanner cin = new Scanner(System.in);

        //Start of program
        System.out.print("Do you want to create a new data base? [Yes/No]: ");

        String choice = cin.nextLine();
        choice=choice.toLowerCase();


        if(choice.equals("no")){
            List<String> files;
            files = getDbNames();

            if(files.size() == 0){
                System.out.println("There are no databases! Create a name for your first database: ");
                dbName = cin.nextLine() + ".db";
                files.add(dbName);
                createNewTable();
            }

            System.out.println(" [Databases] ");
            for(String file : files){
                System.out.println("> " + file.substring(0, file.lastIndexOf('.')));
            }

            System.out.print("Choose the name of a database: ");

            dbName = cin.nextLine() + ".db";
            File f = new File(dbName);
            while(!f.isFile()){
                System.out.print("Invalid Database. Choose again: ");
                dbName = cin.nextLine() + ".db";
                f = new File(dbName);
            }

            System.out.println("DONE: USING ALREADY EXISTING DATABASE");
        }
        else if(choice.equals("yes")){

            System.out.print("Insert the name of the database: ");
            dbName = cin.nextLine() + ".db";

            File file = new File(dbName);
            while(file.exists() == true){
                System.out.print("[ Name already exists! ] Choose another one:  ");
                dbName = cin.nextLine() + ".db";
                file = new File(dbName);
            }

            createNewTable();

        }
    }

    public static List<String> getDbNames(){
        File[] files = new File(directory).listFiles();
        List<String> listOfFiles = new ArrayList<String>();

        for(File file : files){
            if(file.isFile() && file.getName().substring(file.getName().lastIndexOf('.')).equals(".db")){
                listOfFiles.add(file.getName());
            }
        }

        return listOfFiles;
    }


    public static void createNewDatabase(String fileName) {

        String url = path + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(int id, int hasAnswer, int yes, int no, String question) {
        String sql = "INSERT INTO Tree(id,hasAnswer, yes, no, question) VALUES(?,?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, hasAnswer);
            pstmt.setInt(3, yes);
            pstmt.setInt(4, no);
            pstmt.setString(5, question);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
        // SQLite connection string
        String url = path + dbName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void selectAll(){
        String sql = "SELECT id, hasAnswer, yes, no, question FROM Tree";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getInt("hasAnswer") + "\t" +
                        rs.getInt("yes") + "\t" +
                        rs.getInt("no") + "\t" +
                        rs.getString("question"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void deleteAll(){
        String sql = "DELETE FROM Tree";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(int id, int hasAnswer, int yes, int no, String question) {
        String sql = "UPDATE Tree SET hasAnswer = ? , "
                + "yes = ? , "
                + "no = ? , "
                + "question = ? "
                + "WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, hasAnswer);
            pstmt.setInt(2, yes);
            pstmt.setInt(3, no);
            pstmt.setString(4, question);
            pstmt.setInt(5, id);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable() {
        // SQLite connection string
        String url = path + dbName;

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Tree (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	hasAnswer integer NOT NULL,\n"
                + "	yes integer,\n"
                + "	no integer,\n"
                + "	question text\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getNextYesNode(int currNode){

        int yesNode = 0;
        String sql = "SELECT yes "
                + "FROM Tree WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setInt(1,currNode);
            //
            ResultSet rs  = pstmt.executeQuery();

            while(rs.next()) {
                yesNode = rs.getInt("yes");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        String sql2 = "SELECT id, hasAnswer, yes, no, question "
                + "FROM Tree WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql2)){

            // set the value
            pstmt.setInt(1,yesNode);
            //
            ResultSet rs  = pstmt.executeQuery();



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return yesNode;

    }

    public String getQuestion(int root){

        String noNode = null;
        String sql = "SELECT question "
                + "FROM Tree WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setInt(1,root);
            //
            ResultSet rs  = pstmt.executeQuery();

            while(rs.next()) {
                noNode = rs.getString("question");
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return noNode;
    }

    public boolean isItLeaf(int nodeId){

        int hasAnswer = -1;
        String sql = "SELECT hasAnswer "
                + "FROM Tree WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setInt(1,nodeId);
            //
            ResultSet rs  = pstmt.executeQuery();

            while(rs.next()) {
                hasAnswer = rs.getInt("hasAnswer");
            }

            //System.out.println(hasAnswer);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if(hasAnswer == 0){
            return true;
        }else if(hasAnswer == 1){
            return false;
        }
        else{
            System.out.println("ERROR!!!!!");
            return true;
        }
    }

    public int getNumberOfEntries(){
        int numberOfIds =0;
        String sql = "SELECT count(*) as total "
                + "FROM Tree";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            ResultSet rs  = pstmt.executeQuery();


            while(rs.next()) {
                numberOfIds = rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return numberOfIds;
    }

    public int getNextNoNode(int currNode){

        int noNode = 0;
        String sql = "SELECT no "
                + "FROM Tree WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setInt(1,currNode);
            //
            ResultSet rs  = pstmt.executeQuery();

            while(rs.next()) {
                noNode = rs.getInt("no");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        String sql2 = "SELECT id, hasAnswer, yes, no, question "
                + "FROM Tree WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql2)){

            // set the value
            pstmt.setInt(1,noNode);
            //
            ResultSet rs  = pstmt.executeQuery();



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        return noNode;
    }

    public static void addEntry(int no, int parentId, boolean parentYes,Main demo){
        Scanner cin = new Scanner(System.in);
        System.out.printf("Please insert the answer you were looking for: ");
        String answer = cin.nextLine();
        System.out.printf("Now a question in the form \"Is it.../Does it...\" [Example: Is it wet?]:");
        String question = cin.nextLine();

        int qId = demo.getNumberOfEntries()+1;
        int aId = demo.getNumberOfEntries()+2;
        demo.insert(qId, 1, aId, no, question);
        demo.insert(aId, 0, 0, 0, answer);

        if(parentYes){
            demo.update(parentId, 1, qId, demo.getNextNoNode(parentId), demo.getQuestion(parentId));
        }
        else if(!parentYes){
            demo.update(parentId, 1, demo.getNextYesNode(parentId), qId, demo.getQuestion(parentId));
        }


    }



    public static void recursiveTest(int nodeId, int parentId, boolean parentYes,Main demo){
        Scanner cin = new Scanner(System.in);

        if(demo.getNumberOfEntries() == 0){
            firstMenu(demo);
            System.out.println("\n");
        }

        //If it is a leaf
        if(demo.isItLeaf(nodeId)){
            System.out.printf("Is it "+ demo.getQuestion(nodeId) + "?: ");
            String answer = cin.nextLine();
            answer=answer.toLowerCase();
            //If leaf and yes, computer wins
            if(answer.equals("yes")){
                System.out.println("\n>> I win! <<");
                return;
            }
            else if(answer.equals("no")){
                System.out.println("\nI lost :(\n[Add entry]");
                addEntry(nodeId, parentId, parentYes, demo);
                return;
            }
        }

        System.out.printf("%s %s",demo.getQuestion(nodeId), ": ");
        String answer = cin.nextLine();
        answer=answer.toLowerCase();

        if(answer.equals("yes")){
            int nextId = demo.getNextYesNode(nodeId);
            recursiveTest(nextId, nodeId, true,demo);
        }
        else if( answer.equals("no")) {
            int nextId = demo.getNextNoNode(nodeId);
            recursiveTest(nextId, nodeId, false,demo);
        }
    }



    public static void firstMenu(Main demo){
        Scanner cin = new Scanner(System.in);
        System.out.println("Please enter the first answer: ");
        String answer = cin.nextLine();
        System.out.println("Please enter the opposite of: "+ answer +": ");
        String noAnswer = cin.nextLine();
        System.out.println("Please enter the question to \" "+ answer +" \" in the form [Is it.../Does it...] ");
        String question = cin.nextLine();

        demo.insert(0,1,1,2,question);
        demo.insert(1,0,0,0,answer);
        demo.insert(2,0,0,0,noAnswer);


        return;

    }
}