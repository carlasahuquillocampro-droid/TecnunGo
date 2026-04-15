import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatData {

    // INSERTAR MENSAJE
    public static int insertMessage(Connection connection, MessageData msg) {

        String sql = "INSERT INTO Questions (ID_Alumno, Tipo_Remitente, Texto_Mensaje, Fecha_Envio, Leido) "
                   + "VALUES (?, ?, ?, ?, ?)";

        int n = 0;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, msg.idAlumno);
            stmt.setInt(2, msg.tipoRemitente);
            stmt.setString(3, msg.textoMensaje);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setBoolean(5, false);

            n = stmt.executeUpdate();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return n;
    }

    // OBTENER MENSAJES
    public static List<MessageDataFull> getMessagesByAlumno(Connection connection, int idAlumno) {

        List<MessageDataFull> lista = new ArrayList<>();

        String sql = "SELECT ID_Alumno, Tipo_Remitente, Texto_Mensaje, Fecha_Envio "
                   + "FROM Questions WHERE ID_Alumno = ? ORDER BY Fecha_Envio ASC";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idAlumno);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new MessageDataFull(
                    rs.getInt("ID_Alumno"),
                    rs.getInt("Tipo_Remitente"),
                    rs.getString("Texto_Mensaje"),
                    rs.getTimestamp("Fecha_Envio")
                ));
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 🔥 OBTENER NOMBRE REAL DEL STUDENT
    public static String getNombreAlumno(Connection connection, int idAlumno) {

        String nombre = "Alumno";

        String sql = "SELECT Name, Surname FROM Students WHERE ID_Student = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idAlumno);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombre = rs.getString("Name") + " " + rs.getString("Surname");
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nombre;
    }
}

// -------- CLASES --------

class MessageData {
    int idAlumno;
    int tipoRemitente;
    String textoMensaje;

    MessageData(int idAlumno, int tipoRemitente, String textoMensaje) {
        this.idAlumno = idAlumno;
        this.tipoRemitente = tipoRemitente;
        this.textoMensaje = textoMensaje;
    }
}

class MessageDataFull {
    int idAlumno;
    int tipoRemitente;
    String textoMensaje;
    Timestamp fecha;

    MessageDataFull(int idAlumno, int tipoRemitente, String textoMensaje, Timestamp fecha) {
        this.idAlumno = idAlumno;
        this.tipoRemitente = tipoRemitente;
        this.textoMensaje = textoMensaje;
        this.fecha = fecha;
    }
}