import java.sql.*;

public class Main {

    /*public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/Users/diego.vanegaszuniga/IdeaProjects/Database20Questions" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }*/

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
        String url = "jdbc:sqlite:C:/Users/diego.vanegaszuniga/IdeaProjects/Database20Questions/tests.db";
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
        String url = "jdbc:sqlite:C:/Users/diego.vanegaszuniga/IdeaProjects/Database20Questions/tests.db";

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //createNewDatabase("test.db");
        //createNewTable();
        Main demo = new Main();

        //demo.insert(1,1,2,3, "Dog");
        demo.selectAll();
        demo.update(1,0,3,5,"Cat");
        demo.selectAll();
    }
}