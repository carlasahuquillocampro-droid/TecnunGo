import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class UniList extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Corrección de codificación para evitar caracteres raros
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/html; charset=UTF-8");
        
        PrintWriter toClient = res.getWriter();

        // Imprimir Header (asegúrate de que Utils.header tenga el meta charset UTF-8)
        toClient.println(Utils.header("Listado de Universidades"));

        // Estilos CSS modernos para coincidir con tu Login
        toClient.println("<style>");
			toClient.println("body { font-family: 'Segoe UI', sans-serif; background-color: #f9f9f9; padding: 20px; }");
			toClient.println(".container { max-width: 700px; margin: auto; }");
			toClient.println(".card { display: flex; align-items: center; background: white; border-radius: 12px; padding: 15px 20px; margin-bottom: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; transition: transform 0.2s; }");
			toClient.println(".card:hover { transform: scale(1.02); }");
			
			toClient.println(".card-img { max-width: 150px; max-height: 80px; object-fit: contain; margin-right: 25px; }");
			toClient.println(".card-info h3 { margin: 0; color: #1a365d; font-size: 19px; font-weight: 600; }");
			toClient.println(".card-info p { margin: 4px 0 0 0; color: #718096; font-size: 15px; }");
			toClient.println(".card-menu { margin-left: auto; color: #cbd5e0; font-size: 28px; cursor: pointer; }");
			toClient.println("</style>");

        toClient.println("<div class='container'>");
		
					

        // Obtener datos
        Vector<UniData> universityList = UniData.getUniversityList(connection);

        // Generar las tarjetas
        for(UniData uni : universityList) {
            toClient.println("<a href='UniDetail?id=" + uni.Id_University + "' style='text-decoration:none; color:inherit;'>");
            toClient.println("<div class='card'>");
            toClient.println("<img class='card-img' src='" + req.getContextPath() + uni.Image + "' alt='Logo'>");
            toClient.println("<div class='card-info'>");
            toClient.println("<h3>" + uni.Name + "</h3>");
            toClient.println("<p>" + uni.CityName + ", " + uni.CountryName + "</p>");
            toClient.println("</div>");
           toClient.println("<div class='card-menu'>&#8801;</div>"); 
			toClient.println("</div></a>");
        }

        toClient.println("</div>"); // Cierre del container
        toClient.println(Utils.footer("Universidades"));
        toClient.close();
    }
}