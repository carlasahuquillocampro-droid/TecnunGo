import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ChatData {

    public static int insertMessage(Connection connection, MessageData msg) {
        // Usamos el nombre de tu tabla: Questions y los campos de tu foto
        String sql = "INSERT INTO Questions (ID_Alumno, Tipo_Remitente, Texto_Mensaje, Fecha_Envio, Leido) "
                   + "VALUES (?, ?, ?, ?, ?)";
        System.out.println("insertMessage: " + sql);
        int n = 0;
        try {
            PreparedStatement stmtUpdate = connection.prepareStatement(sql);
            stmtUpdate.setInt(1, msg.idAlumno);
            stmtUpdate.setInt(2, msg.tipoRemitente);
            stmtUpdate.setString(3, msg.textoMensaje);
            stmtUpdate.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmtUpdate.setBoolean(5, false); 
            
            n = stmtUpdate.executeUpdate();
            stmtUpdate.close();
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error in insertMessage: " + sql + " Exception: " + e);
        }
        return n;
    }
}

class MessageData {
    int idAlumno;
    int tipoRemitente;
    String textoMensaje;
    
    MessageData (int idAlumno, int tipoRemitente, String textoMensaje) {
        this.idAlumno = idAlumno;
        this.tipoRemitente = tipoRemitente;
        this.textoMensaje = textoMensaje;
    }
}