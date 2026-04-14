import java.util.Vector;
import java.sql.*;

public class UniData {
    // Atributos idénticos a las columnas de tu DB
    String Id_University;
    String Name;
    int ID_City;
    int ID_Country;
    int Spots;
    String Image;
    float Minimum_Grade; // Corregido a 'minimum'
    String Description;
    
    // Nombres para mostrar en la lista (del JOIN)
    String CityName;
    String CountryName;

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
	
   
	
}