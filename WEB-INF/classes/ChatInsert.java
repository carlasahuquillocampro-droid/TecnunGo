import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class ChatInsert extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter toClient = res.getWriter();
        
        // 1. Obtener parámetros de la URL
        String idAlumno = req.getParameter("idAlumno");
        String idEmployee = req.getParameter("idEmployee");

        // Validar que al menos tengamos al alumno
        if (idAlumno == null) {
            toClient.println("<h1>Error: Falta ID de Alumno</h1>");
            return;
        }

        // Determinar si es Admin o Alumno para el título y el formulario
        boolean isAdmin = (idEmployee != null && !idEmployee.isEmpty());

        // --- EMPEZAR HTML ---
        toClient.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
        toClient.println("<title>TecnunGo - Chat de Dudas</title>");
        
        // --- TODO TU CSS ---
        toClient.println("<style>");
        toClient.println("body { font-family: sans-serif; background-color: #f0f2f5; margin: 0; padding: 40px 20px; display: flex; justify-content: center; }");
        toClient.println(".chat-container { background: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); width: 100%; max-width: 800px; padding: 30px; display: flex; flex-direction: column; }");
        toClient.println(".chat-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #f0f2f5; padding-bottom: 20px; margin-bottom: 20px; }");
        toClient.println(".header-text h2 { margin: 0 0 5px 0; color: #2c3e50; font-size: 1.6em; }");
        toClient.println(".header-text p { margin: 0; color: #666; font-size: 1em; }");
        toClient.println(".btn-volver { background-color: #2c3e50; color: white; border: none; padding: 10px 18px; border-radius: 8px; cursor: pointer; font-weight: bold; font-size: 1em; transition: 0.2s; }");
        toClient.println(".chat-messages { flex-grow: 1; background-color: #f8f9fa; border: 1px solid #eaeaea; padding: 25px; border-radius: 12px; overflow-y: auto; height: 450px; margin-bottom: 20px; display: flex; flex-direction: column; gap: 20px; }");
        toClient.println(".message-group { display: flex; flex-direction: column; max-width: 75%; }");
        toClient.println(".message-group.user { align-self: flex-end; align-items: flex-end; }");
        toClient.println(".message-group.admin { align-self: flex-start; align-items: flex-start; }");
        toClient.println(".sender-name { font-size: 0.95em; color: #777; margin-bottom: 6px; font-weight: bold; padding: 0 5px; }");
        toClient.println(".message { padding: 16px 22px; border-radius: 12px; line-height: 1.5; font-size: 1.1em; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }");
        toClient.println(".message-group.user .message { background-color: #e6f7ff; border-bottom-right-radius: 0; color: #004085; }");
        toClient.println(".message-group.admin .message { background-color: #ffffff; border-bottom-left-radius: 0; color: #333; border: 1px solid #eaeaea; }");
        toClient.println(".chat-input-area { display: flex; gap: 12px; }");
        toClient.println(".chat-input-area textarea { flex-grow: 1; padding: 15px; border-radius: 12px; border: 1px solid #ddd; resize: none; font-family: inherit; font-size: 1.1em; }");
        toClient.println(".chat-input-area button { background-color: #588e73; color: white; border: none; padding: 12px 30px; border-radius: 12px; cursor: pointer; font-weight: bold; font-size: 1.1em; }");
        toClient.println("</style></head><body>");

        // --- CUERPO DEL CHAT ---
        toClient.println("<div class='chat-container'>");
        toClient.println("    <div class='chat-header'>");
        toClient.println("        <div class='header-text'>");
        toClient.println("            <h2>Chat de Dudas Erasmus</h2>");
        toClient.println("            <p>TecnunGo - University of Navarra</p>");
        toClient.println("        </div>");
        toClient.println("        <button class='btn-volver' onclick='history.back()'> Volver</button>");
        toClient.println("    </div>");

        toClient.println("    <div class='chat-messages'>");
        // Aquí iría el bucle de la base de datos (Servlet 9.1). Por ahora el ejemplo:
        toClient.println("        <div class='message-group admin'>");
        toClient.println("            <span class='sender-name'>Coordinador Erasmus</span>");
        toClient.println("            <div class='message'><p>Bienvenido. Estás en el chat del alumno " + idAlumno + "</p></div>");
        toClient.println("        </div>");
        toClient.println("    </div>");

        // --- FORMULARIO DINAMICO ---
        toClient.println("    <form class='chat-input-area' action='ChatInsert' method='POST'>");
        
        // Campos ocultos para pasar los datos al POST
        toClient.println("        <input type='hidden' name='idAlumno' value='" + idAlumno + "'>");
        if (isAdmin) {
            toClient.println("    <input type='hidden' name='idEmployee' value='" + idEmployee + "'>");
        }

        toClient.println("        <textarea name='textoMensaje' rows='2' placeholder='Escribe tu mensaje aquí...' required></textarea>");
        toClient.println("        <button type='submit'>Enviar</button>");
        toClient.println("    </form>");
        
        toClient.println("</div></body></html>");
        toClient.close();
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        // 1. Recoger datos del formulario
        String idAlumnoStr = req.getParameter("idAlumno");
        String idEmployeeStr = req.getParameter("idEmployee");
        String texto = req.getParameter("textoMensaje");

        int idAlumno = Integer.parseInt(idAlumnoStr);
        // Si hay idEmployee, el tipo es 1 (Admin), si no es 0 (Alumno)
        int tipo = (idEmployeeStr != null && !idEmployeeStr.isEmpty()) ? 1 : 0;

        // 2. Insertar en la tabla Questions
        if (texto != null && !texto.trim().isEmpty()) {
            MessageData msg = new MessageData(idAlumno, tipo, texto);
            ChatData.insertMessage(connection, msg);
        }

        // 3. Redirigir al mismo chat para limpiar el textarea
        String redirect = "ChatInsert?idAlumno=" + idAlumnoStr;
        if (idEmployeeStr != null) redirect += "&idEmployee=" + idEmployeeStr;
        res.sendRedirect(redirect);
    }
}