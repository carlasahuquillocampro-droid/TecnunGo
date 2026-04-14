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
        // Character encoding correction 
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/html; charset=UTF-8");
        
        PrintWriter toClient = res.getWriter();

        // 1. Capture the URL parameters sent by the form
        String gradeFilter = req.getParameter("grade");
        String countryFilter = req.getParameter("country");
        String degreeFilter = req.getParameter("degree");

        // Print Header 
        toClient.println(Utils.header("University List"));

        // CSS
        toClient.println("<style>");
        toClient.println("body { font-family: 'Segoe UI', sans-serif; background-color: #f9f9f9; padding: 20px; }");
        toClient.println(".container { max-width: 700px; margin: auto; }");
        
        // Styles for the filter form
        toClient.println(".filter-form { display: flex; gap: 10px; background: white; padding: 15px; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; align-items: center; }");
        toClient.println(".filter-form input, .filter-form select { flex: 1; padding: 8px 12px; border: 1px solid #cbd5e0; border-radius: 6px; background-color: white; font-family: inherit; font-size: 14px; }");
        toClient.println(".filter-form button { padding: 8px 15px; background-color: #1a365d; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: bold; }");
        toClient.println(".filter-form button:hover { background-color: #2a4365; }");
        toClient.println(".filter-form .btn-clear { padding: 8px 15px; background-color: #e2e8f0; color: #1a365d; text-decoration: none; border-radius: 6px; font-size: 14px; text-align: center; }");

        // Styles for the cards
        toClient.println(".card { display: flex; align-items: center; background: white; border-radius: 12px; padding: 15px 20px; margin-bottom: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; transition: transform 0.2s; }");
        toClient.println(".card:hover { transform: scale(1.02); }");
        toClient.println(".card-img { max-width: 150px; max-height: 80px; object-fit: contain; margin-right: 25px; }");
        toClient.println(".card-info h3 { margin: 0; color: #1a365d; font-size: 19px; font-weight: 600; }");
        toClient.println(".card-info p { margin: 4px 0 0 0; color: #718096; font-size: 15px; }");
        toClient.println(".card-menu { margin-left: auto; color: #cbd5e0; font-size: 28px; cursor: pointer; }");
        toClient.println("</style>");

        toClient.println("<div class='container'>");

        // ==========================================
        // 2. FILTER FORM
        // ==========================================
        toClient.println("<form class='filter-form' method='GET' action='UniList'>");
        
        // Null handling so the fields don't display "null" visually
        String gradeVal = (gradeFilter != null) ? gradeFilter : "";
        String countryVal = (countryFilter != null) ? countryFilter : "";
        String degreeVal = (degreeFilter != null) ? degreeFilter : "";

        // A) Numeric input for GRADE
        toClient.println("<input type='number' step='0.1' name='grade' placeholder='Max Grade' value='" + gradeVal + "'>");

        // B) Dynamic dropdown for COUNTRY
        List<String> allCountries = UniData.getAllCountries(connection);
        toClient.println("<select name='country'>");
        toClient.println("<option value=''>Any Country</option>"); 
        for (String country : allCountries) {
            String selected = country.equals(countryVal) ? "selected" : "";
            toClient.println("<option value='" + country + "' " + selected + ">" + country + "</option>");
        }
        toClient.println("</select>");

        // C) Dynamic dropdown for DEGREE
        List<String> allDegrees = UniData.getAllDegrees(connection);
        toClient.println("<select name='degree'>");
        toClient.println("<option value=''>Any Degree</option>"); 
        for (String degree : allDegrees) {
            String selected = degree.equals(degreeVal) ? "selected" : "";
            toClient.println("<option value='" + degree + "' " + selected + ">" + degree + "</option>");
        }
        toClient.println("</select>");
        
        // Action buttons
        toClient.println("<button type='submit'>Filter</button>");
        toClient.println("<a href='UniList' class='btn-clear'>Clear</a>");
        toClient.println("</form>");


        // ==========================================
        // 3. GET AND DISPLAY RESULTS
        // ==========================================
        Vector<UniData> universityList = UniData.getFilteredUniversityList(connection, gradeFilter, countryFilter, degreeFilter);

        // If there are no results, show a friendly message
        if (universityList.isEmpty()) {
            toClient.println("<div style='text-align:center; padding: 40px; background: white; border-radius: 12px; border: 1px solid #e2e8f0;'>");
            toClient.println("<h3 style='color: #1a365d; margin-top:0;'>No results found</h3>");
            toClient.println("<p style='color: #718096;'>We couldn't find any universities matching your filters. Try being less restrictive.</p>");
            toClient.println("</div>");
        } else {
            // Generate the cards for each university
            for(UniData uni : universityList) {
                toClient.println("<a href='UniDetail?id=" + uni.Id_University + "' style='text-decoration:none; color:inherit;'>");
                toClient.println("<div class='card'>");
                toClient.println("<img class='card-img' src='" + req.getContextPath() + uni.Image + "' alt='" + uni.Name + " Logo'>");
                toClient.println("<div class='card-info'>");
                toClient.println("<h3>" + uni.Name + "</h3>");
                // Shows the city, country and cut-off grade on the card
                toClient.println("<p>" + uni.CityName + ", " + uni.CountryName + "</p>");
                toClient.println("</div>");
                toClient.println("<div class='card-menu'>&#8801;</div>"); 
                toClient.println("</div></a>");
            }
        }

        toClient.println("</div>"); // Closing container div
        toClient.println(Utils.footer("Universities"));
        toClient.close();
    }
}