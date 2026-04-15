import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.util.List;
import java.text.SimpleDateFormat;

@SuppressWarnings("serial")
public class ChatInsert extends HttpServlet {

    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String idAlumno = req.getParameter("idAlumno");
        String idEmployee = req.getParameter("idEmployee");

        if (idAlumno == null) {
            out.println("<h1>Error: Falta ID Alumno</h1>");
            return;
        }

        boolean isAdmin = (idEmployee != null && !idEmployee.isEmpty());

        int idAlumnoInt = Integer.parseInt(idAlumno);

        List<MessageDataFull> mensajes = ChatData.getMessagesByAlumno(connection, idAlumnoInt);
        String nombreAlumno = ChatData.getNombreAlumno(connection, idAlumnoInt);

        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");

        // HTML
        out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
        out.println("<title>TecnunGo - Chat</title>");

        // CSS (tuyo + hora)
        out.println("<style>");
        out.println("body { font-family: sans-serif; background-color: #f0f2f5; margin: 0; padding: 40px 20px; display: flex; justify-content: center; }");
        out.println(".chat-container { background: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); width: 100%; max-width: 800px; padding: 30px; display: flex; flex-direction: column; }");
        out.println(".chat-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #f0f2f5; padding-bottom: 20px; margin-bottom: 20px; }");
        out.println(".header-text h2 { margin: 0 0 5px 0; color: #2c3e50; font-size: 1.6em; }");
        out.println(".header-text p { margin: 0; color: #666; font-size: 1em; }");
        out.println(".btn-volver { background-color: #2c3e50; color: white; border: none; padding: 10px 18px; border-radius: 8px; }");
        out.println(".chat-messages { flex-grow: 1; background-color: #f8f9fa; border: 1px solid #eaeaea; padding: 25px; border-radius: 12px; overflow-y: auto; height: 450px; margin-bottom: 20px; display: flex; flex-direction: column; gap: 20px; }");
        out.println(".message-group { display: flex; flex-direction: column; max-width: 75%; }");
        out.println(".message-group.user { align-self: flex-end; align-items: flex-end; }");
        out.println(".message-group.admin { align-self: flex-start; align-items: flex-start; }");
        out.println(".sender-name { font-size: 0.95em; color: #777; margin-bottom: 6px; font-weight: bold; }");
        out.println(".message { padding: 16px 22px; border-radius: 12px; font-size: 1.1em; }");
        out.println(".message-group.user .message { background-color: #e6f7ff; border-bottom-right-radius: 0; }");
        out.println(".message-group.admin .message { background-color: #ffffff; border-bottom-left-radius: 0; border: 1px solid #eaeaea; }");
        out.println(".hora { font-size: 0.8em; color: #999; margin-top: 5px; }");
        out.println(".chat-input-area { display: flex; gap: 12px; }");
        out.println(".chat-input-area textarea { flex-grow: 1; padding: 15px; border-radius: 12px; border: 1px solid #ddd; }");
        out.println(".chat-input-area button { background-color: #588e73; color: white; border: none; padding: 12px 30px; border-radius: 12px; }");
        out.println("</style></head><body>");

        out.println("<div class='chat-container'>");

        // HEADER CON NOMBRE
        out.println("<div class='chat-header'>");
        out.println("<div class='header-text'>");
        out.println("<h2>Chat con " + nombreAlumno + "</h2>");
        out.println("<p>TecnunGo - University of Navarra</p>");
        out.println("</div>");
        out.println("<button class='btn-volver' onclick='history.back()'>Volver</button>");
        out.println("</div>");

        // MENSAJES
        out.println("<div class='chat-messages'>");

        for (MessageDataFull m : mensajes) {

            boolean esAlumno = (m.tipoRemitente == 0);
            String clase = esAlumno ? "user" : "admin";
            String nombre = esAlumno ? nombreAlumno : "Coordinador Erasmus";

            String hora = formatoHora.format(m.fecha);

            out.println("<div class='message-group " + clase + "'>");
            out.println("<span class='sender-name'>" + nombre + "</span>");
            out.println("<div class='message'>");
            out.println("<p>" + m.textoMensaje + "</p>");
            out.println("<div class='hora'>" + hora + "</div>");
            out.println("</div>");
            out.println("</div>");
        }

        out.println("</div>");

        // FORM
        out.println("<form class='chat-input-area' action='ChatInsert' method='POST'>");
        out.println("<input type='hidden' name='idAlumno' value='" + idAlumno + "'>");

        if (isAdmin) {
            out.println("<input type='hidden' name='idEmployee' value='" + idEmployee + "'>");
        }

        out.println("<textarea name='textoMensaje' rows='2' placeholder='Escribe tu mensaje...' required></textarea>");
        out.println("<button type='submit'>Enviar</button>");
        out.println("</form>");

        out.println("</div></body></html>");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String idAlumnoStr = req.getParameter("idAlumno");
        String idEmployeeStr = req.getParameter("idEmployee");
        String texto = req.getParameter("textoMensaje");

        int idAlumno = Integer.parseInt(idAlumnoStr);
        int tipo = (idEmployeeStr != null && !idEmployeeStr.isEmpty()) ? 1 : 0;

        if (texto != null && !texto.trim().isEmpty()) {
            MessageData msg = new MessageData(idAlumno, tipo, texto);
            ChatData.insertMessage(connection, msg);
        }

        String redirect = "ChatInsert?idAlumno=" + idAlumnoStr;
        if (idEmployeeStr != null) redirect += "&idEmployee=" + idEmployeeStr;

        res.sendRedirect(redirect);
    }
}