import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class ListStudentData extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/html; charset=UTF-8");
        
        PrintWriter toClient = res.getWriter();
        
        // 1. Recogemos el ID del alumno (ej: ListStudentData?id=1)
        String id = req.getParameter("id");
        StudentData student = StudentData.getStudent(connection, id);

        // 2. Empezamos a pintar el HTML con el estilo de tu compañera
        toClient.println("<!DOCTYPE html>");
        toClient.println("<html lang='es'>");
        toClient.println("<head>");
        toClient.println("    <meta charset='UTF-8'>");
        toClient.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        toClient.println("    <title>TecnunGo - Mi Perfil</title>");
        toClient.println("    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        
        // --- CSS EXACTO DE TU COMPAÑERA ---
        toClient.println("    <style>");
        toClient.println("        :root {");
        toClient.println("            --tecnun-blue-dark: #102a43;");
        toClient.println("            --tecnun-blue-light: #243b55;");
        toClient.println("            --tecnun-green: #4b8b70; ");
        toClient.println("            --tecnun-green-hover: #3a6b56;");
        toClient.println("            --text-light: #ffffff;");
        toClient.println("            --text-dark: #1f2937;");
        toClient.println("            --text-muted: #6b7280;");
        toClient.println("            --bg-body: #f3f4f6;");
        toClient.println("            --error-color: #ef4444;");
        toClient.println("        }");
        toClient.println("        body {");
        toClient.println("            font-family: 'Poppins', sans-serif;");
        toClient.println("            margin: 0;");
        toClient.println("            min-height: 100vh;");
        toClient.println("            display: flex;");
        toClient.println("            justify-content: center;");
        toClient.println("            align-items: center;");
        toClient.println("            background-color: var(--bg-body); ");
        toClient.println("            color: var(--text-dark);");
        toClient.println("        }");
        toClient.println("        .split-container {");
        toClient.println("            display: grid;");
        toClient.println("            grid-template-columns: 1fr 1.2fr;");
        toClient.println("            background-color: #ffffff;");
        toClient.println("            width: 90%;");
        toClient.println("            max-width: 950px;");
        toClient.println("            min-height: 550px;");
        toClient.println("            border-radius: 20px;");
        toClient.println("            overflow: hidden;");
        toClient.println("            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);");
        toClient.println("        }");
        toClient.println("        .panel-left {");
        toClient.println("            background: linear-gradient(135deg, var(--tecnun-blue-dark) 0%, var(--tecnun-blue-light) 100%);");
        toClient.println("            padding: 50px;");
        toClient.println("            display: flex;");
        toClient.println("            flex-direction: column;");
        toClient.println("            justify-content: center;");
        toClient.println("            align-items: center;");
        toClient.println("            color: var(--text-light);");
        toClient.println("            text-align: center;");
        toClient.println("        }");
        toClient.println("        .panel-left h2 { font-size: 2.5rem; font-weight: 700; margin: 0 0 10px 0; letter-spacing: 1px; }");
        toClient.println("        .panel-left h3 { font-size: 1.1rem; font-weight: 400; margin: 0 0 30px 0; color: #93c5fd; }");
        toClient.println("        .panel-left p { font-size: 0.95rem; font-weight: 300; margin: 0 0 40px 0; line-height: 1.6; opacity: 0.9; }");
        
        toClient.println("        .panel-right {");
        toClient.println("            padding: 50px 70px;");
        toClient.println("            display: flex;");
        toClient.println("            flex-direction: column;");
        toClient.println("            justify-content: center;");
        toClient.println("        }");
        toClient.println("        .panel-right h1 { font-size: 2rem; font-weight: 600; color: var(--tecnun-blue-dark); margin: 0 0 5px 0; }");
        toClient.println("        .panel-right .subtitle { color: var(--text-muted); font-size: 0.95rem; margin: 0 0 35px 0; }");
        
        toClient.println("        .form-group { margin-bottom: 20px; }");
        toClient.println("        .form-group label { display: block; font-size: 0.85rem; font-weight: 600; color: var(--tecnun-blue-dark); margin-bottom: 8px; }");
        
        // Estilo adaptado para mostrar datos (parecido al input pero sin bordes)
        toClient.println("        .data-display {");
        toClient.println("            width: 100%;");
        toClient.println("            padding: 12px 15px;");
        toClient.println("            background-color: #f9fafb;");
        toClient.println("            border-radius: 10px;");
        toClient.println("            font-size: 0.95rem;");
        toClient.println("            color: var(--text-dark);");
        toClient.println("            box-sizing: border-box;");
        toClient.println("            border: 1px solid #e5e7eb;");
        toClient.println("        }");
        
        // Estilo para la lista de idiomas
        toClient.println("        .lang-list { padding-left: 20px; margin: 0; }");
        toClient.println("        .lang-list li { color: var(--tecnun-green); font-weight: 500; margin-bottom: 5px; }");

        toClient.println("        .btn-main {");
        toClient.println("            display: block;");
        toClient.println("            text-align: center;");
        toClient.println("            width: 100%;");
        toClient.println("            padding: 15px;");
        toClient.println("            background-color: var(--tecnun-green);");
        toClient.println("            color: var(--text-light);");
        toClient.println("            border: none;");
        toClient.println("            border-radius: 10px;");
        toClient.println("            font-size: 1rem;");
        toClient.println("            font-weight: 600;");
        toClient.println("            text-decoration: none;");
        toClient.println("            transition: background-color 0.3s ease, transform 0.1s ease;");
        toClient.println("            margin-top: 15px;");
        toClient.println("            box-sizing: border-box;");
        toClient.println("        }");
        toClient.println("        .btn-main:hover { background-color: var(--tecnun-green-hover); transform: translateY(-1px); }");
        
        toClient.println("        @media (max-width: 900px) {");
        toClient.println("            .split-container { grid-template-columns: 1fr; }");
        toClient.println("            .panel-left { padding: 40px 20px; min-height: 200px; }");
        toClient.println("            .panel-right { padding: 40px 30px; }");
        toClient.println("        }");
        toClient.println("    </style>");
        toClient.println("</head>");
        toClient.println("<body>");

        toClient.println("    <div class='split-container'>");
        
        // --- PANEL IZQUIERDO (Branding exacto de tu amiga) ---
        toClient.println("        <div class='panel-left'>");
        toClient.println("            <h2>TecnunGo</h2>");
        toClient.println("            <h3>University of Navarra</h3>");
        toClient.println("            <p>Explore exchange destinations, consult academic requirements, and manage your preference list through our geolocated interactive map.</p>");
        toClient.println("        </div>");

        // --- PANEL DERECHO (Datos del Perfil) ---
        toClient.println("        <div class='panel-right'>");
        
        if (student != null) {
            toClient.println("            <h1>Mi Perfil</h1>");
            toClient.println("            <p class='subtitle'>Consulta tu información académica y personal.</p>");

            // Fila 1: Nombre
            toClient.println("            <div class='form-group'>");
            toClient.println("                <label>Nombre y Apellidos</label>");
            toClient.println("                <div class='data-display'>" + student.name + " " + student.surname + "</div>");
            toClient.println("            </div>");

            // Fila 2: Email
            toClient.println("            <div class='form-group'>");
            toClient.println("                <label>Email Universitario</label>");
            toClient.println("                <div class='data-display'>" + student.email + "</div>");
            toClient.println("            </div>");

            // Fila 3: Grado y BOE
            toClient.println("            <div class='form-group'>");
            toClient.println("                <label>Datos Académicos (Oficial)</label>");
            toClient.println("                <div class='data-display'>");
            toClient.println("                    <strong>Grado:</strong> " + student.degreeName + "<br>");
            toClient.println("                    <strong>Nota BOE:</strong> " + student.boeScore);
            toClient.println("                </div>");
            toClient.println("            </div>");

            // Fila 4: Idiomas
            toClient.println("            <div class='form-group'>");
            toClient.println("                <label>Idiomas Acreditados</label>");
            toClient.println("                <div class='data-display'>");
            
            if(student.languages.size() > 0) {
                toClient.println("                    <ul class='lang-list'>");
                for(int i = 0; i < student.languages.size(); i++) {
                    toClient.println("                        <li>" + student.languages.elementAt(i) + "</li>");
                }
                toClient.println("                    </ul>");
            } else {
                toClient.println("                    <span style='color: var(--text-muted);'>No tienes idiomas registrados.</span>");
            }
            toClient.println("                </div>");
            toClient.println("            </div>");

            // BOTÓN DE EDITAR: Lleva al servlet UpdateAlumniData (que crearemos luego como EditStudentData)
            toClient.println("            <a href='EditStudentData?id=" + student.idStudent + "' class='btn-main'>Editar Información</a>");

        } else {
            // Si hay un error y no encuentra al alumno
            toClient.println("            <h1 style='color: var(--error-color);'>Error</h1>");
            toClient.println("            <p class='subtitle'>No se ha encontrado el perfil del alumno.</p>");
        }

        toClient.println("        </div>");
        toClient.println("    </div>");

        toClient.println("</body>");
        toClient.println("</html>");

        toClient.close();
    }
}