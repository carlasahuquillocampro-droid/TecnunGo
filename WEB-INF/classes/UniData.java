import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class UniData {
    
    String Id_University;
    String Name;
    int ID_City;
    int ID_Country;
    int Spots;
    String Image;
    float Minimum_Grade; 
    String Description;
    
    String CityName;
    String CountryName;

    // Constructor
    public UniData(String Id_University, String Name, int ID_City, int ID_Country, int Spots, String Image, float Minimum_Grade, String Description, String CityName, String CountryName) {
        this.Id_University = Id_University;
        this.Name = Name;
        this.ID_City = ID_City;
        this.ID_Country = ID_Country;
        this.Spots = Spots;
        this.Image = Image;
        this.Minimum_Grade = Minimum_Grade;
        this.Description = Description;
        this.CityName = CityName;
        this.CountryName = CountryName;
    }

    // ==========================================
    // 1. MeTODO ORIGINAL (Sin filtros)
    // ==========================================
    public static Vector<UniData> getUniversityList(Connection connection) {
        Vector<UniData> vec = new Vector<UniData>();
        
        String sql = "SELECT Universities.ID_University, Universities.Name, Universities.ID_City, " +
                     "Universities.ID_Country, Universities.Spots, Universities.Image, " +
                     "Universities.Minimum_Grade, Universities.Description, " +
                     "Cities.Name AS CityName, Countries.Name AS CountryName " + 
                     "FROM Universities " +
                     "INNER JOIN Cities ON Universities.ID_City = Cities.ID_City " +
                     "INNER JOIN Countries ON Universities.ID_Country = Countries.ID_Country";
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                UniData uni = new UniData(
                    result.getString("Id_University"),
                    result.getString("Name"),
                    result.getInt("ID_City"),
                    result.getInt("ID_Country"),
                    result.getInt("Spots"),
                    result.getString("Image"),
                    result.getFloat("Minimum_Grade"),
                    result.getString("Description"),
                    result.getString("CityName"),
                    result.getString("CountryName")
                );
                vec.addElement(uni);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return vec;
    }

// ==========================================
    // 2. MÉTODO CON FILTROS DINAMICOS (TABLAS CORREGIDAS Y NOTA ORIGINAL)
    // ==========================================
    public static Vector<UniData> getFilteredUniversityList(Connection connection, String gradeFilter, String countryFilter, String degreeFilter) {
        Vector<UniData> vec = new Vector<UniData>();
        
        // Usamos StringBuilder para construir la query dinámicamente
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT U.ID_University, U.Name, U.ID_City, " +
            "U.ID_Country, U.Spots, U.Image, " +
            "U.Minimum_Grade, U.Description, " +
            "C.Name AS CityName, Co.Name AS CountryName " + 
            "FROM Universities U " +
            "INNER JOIN Cities C ON U.ID_City = C.ID_City " +
            "INNER JOIN Countries Co ON U.ID_Country = Co.ID_Country "
        );

        // Nombres de tablas y columnas corregidos (Uni_Degree y ID_Uni)
        if (degreeFilter != null && !degreeFilter.trim().isEmpty()) {
            sql.append("INNER JOIN Uni_Degree UD ON U.ID_University = UD.ID_Uni ");
            sql.append("INNER JOIN Degrees D ON UD.ID_Degree = D.ID_Degree ");
        }

        sql.append("WHERE 1=1 ");

        List<Object> parameters = new ArrayList<>();

        // Filtro por Nota (Grade) - Se mantiene el <= para ver "dónde te aceptan"
        if (gradeFilter != null && !gradeFilter.trim().isEmpty()) {
            sql.append("AND U.Minimum_Grade <= ? ");
            parameters.add(Float.parseFloat(gradeFilter));
        }

        // Filtro por País (Country)
        if (countryFilter != null && !countryFilter.trim().isEmpty()) {
            sql.append("AND Co.Name LIKE ? ");
            parameters.add("%" + countryFilter + "%");
        }

        // Filtro por Carrera (Degree)
        if (degreeFilter != null && !degreeFilter.trim().isEmpty()) {
            sql.append("AND D.Name LIKE ? ");
            parameters.add("%" + degreeFilter + "%");
        }

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql.toString());
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet result = pstmt.executeQuery();
            while(result.next()) {
                UniData uni = new UniData(
                    result.getString("ID_University"),
                    result.getString("Name"),
                    result.getInt("ID_City"),
                    result.getInt("ID_Country"),
                    result.getInt("Spots"),
                    result.getString("Image"),
                    result.getFloat("Minimum_Grade"),
                    result.getString("Description"),
                    result.getString("CityName"),
                    result.getString("CountryName")
                );
                vec.addElement(uni);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return vec;
    }

    // ==========================================
    // 3. OBTENER PAiSES PARA EL DESPLEGABLE
    // ==========================================
    public static List<String> getAllCountries(Connection connection) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT Name FROM Countries ORDER BY Name";
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                list.add(result.getString("Name"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================
    // 4. OBTENER GRADOS PARA EL DESPLEGABLE
    // ==========================================
    public static List<String> getAllDegrees(Connection connection) {
        List<String> list = new ArrayList<>();
        // Ajusta el nombre de la tabla 'Degrees' si en tu BD se llama diferente
        String sql = "SELECT DISTINCT Name FROM Degrees ORDER BY Name"; 
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                list.add(result.getString("Name"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}