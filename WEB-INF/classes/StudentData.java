import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentData {
    
    int idStudent;
    String name;
    String surname;
    String email;
    String password;
    int idDegree;
    int boeScore;
    String degreeName;
    Vector<String> languages; 
    
    // Constructor completo
    public StudentData(int idStudent, String name, String surname, String email, String password, int idDegree, int boeScore, String degreeName, Vector<String> languages) {
        this.idStudent = idStudent;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.idDegree = idDegree;
        this.boeScore = boeScore;
        this.degreeName = degreeName;
        this.languages = languages;
    }

    // ==========================================
    // 1. OBTENER UN SOLO ALUMNO (Con Grado e Idiomas)
    // ==========================================
    public static StudentData getStudent(Connection connection, String id) {
        String sqlStudent = "SELECT S.ID_Student, S.Name, S.Surname, S.Email, S.Password, S.ID_Degree, S.BOEScore, D.Name AS DegreeName " +
                            "FROM Students S " +
                            "LEFT JOIN Degrees D ON S.ID_Degree = D.ID_Degree " +
                            "WHERE S.ID_Student = ?";
                             
        StudentData student = null;
        
        try {
            // --- PASO 1: Buscar los datos principales del alumno ---
            PreparedStatement pstmtStudent = connection.prepareStatement(sqlStudent);
            pstmtStudent.setInt(1, Integer.parseInt(id)); 
            ResultSet resultStudent = pstmtStudent.executeQuery();
            
            if(resultStudent.next()) {
                
                // --- PASO 2: Buscar los idiomas (SÚPER SIMPLE, SIN MIN_LEVEL) ---
                Vector<String> studentLangs = new Vector<String>();
                
                // Hacemos JOIN pero SOLO pedimos la columna Language (ej: "English B2")
                String sqlLangs = "SELECT L.Language " +
                                  "FROM Student_Language SL " +
                                  "INNER JOIN Language L ON SL.ID_Language = L.ID_Language " +
                                  "WHERE SL.ID_Student = ?";
                                  
                PreparedStatement pstmtLangs = connection.prepareStatement(sqlLangs);
                pstmtLangs.setInt(1, resultStudent.getInt("ID_Student"));
                ResultSet resultLangs = pstmtLangs.executeQuery();
                
                // Añadimos cada idioma directamente a la lista
                while(resultLangs.next()) {
                    studentLangs.add(resultLangs.getString("Language"));
                }
                
                resultLangs.close();
                pstmtLangs.close();

                // --- PASO 3: Construir el objeto final ---
                student = new StudentData(
                    resultStudent.getInt("ID_Student"),
                    resultStudent.getString("Name"),
                    resultStudent.getString("Surname"),
                    resultStudent.getString("Email"),
                    resultStudent.getString("Password"),
                    resultStudent.getInt("ID_Degree"),
                    resultStudent.getInt("BOEScore"),
                    resultStudent.getString("DegreeName"),
                    studentLangs 
                );
            }
            resultStudent.close();
            pstmtStudent.close();
            
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error en getStudent: " + e.getMessage());
        }
        return student;
    }

    // ==========================================
    // 2. ACTUALIZAR DATOS DEL ALUMNO (Seguro)
    // ==========================================
    public static int updateStudentProfile(Connection connection, StudentData student) {
        String sql = "UPDATE Students SET Name = ?, Surname = ?, Email = ?, Password = ? WHERE ID_Student = ?";
        int n = 0;
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, student.name);
            pstmt.setString(2, student.surname);
            pstmt.setString(3, student.email);
            pstmt.setString(4, student.password);
            pstmt.setInt(5, student.idStudent);
            
            n = pstmt.executeUpdate();
            pstmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error en updateStudentProfile: " + sql + " Exception: " + e);
        }
        return n;
    }

    // ==========================================
    // 3. AÑADIR UN NUEVO IDIOMA AL ALUMNO (Muy fácil de explicar)
    // ==========================================
    public static int addStudentLanguage(Connection connection, int idStudent, int idLanguage) {
        String sql = "INSERT INTO Student_Language (ID_Student, ID_Language) VALUES (?, ?)";
        int n = 0;
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idStudent);
            pstmt.setInt(2, idLanguage);
            
            n = pstmt.executeUpdate();
            pstmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error en addStudentLanguage: " + sql + " Exception: " + e);
        }
        return n;
    }
}