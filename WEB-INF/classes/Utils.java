public class Utils {
   public static String header(String title) {
    StringBuilder str = new StringBuilder();
    str.append("<!DOCTYPE HTML><html lang='es'>");
    str.append("<head><meta charset='UTF-8'>");
    str.append("<title>" + title + "</title>");
    // CSS global con fuente moderna y colores limpios
    str.append("<style>");
    str.append("body { font-family: 'Segoe UI', sans-serif; background-color: #f9f9f9; padding: 20px; }");
    str.append(".container { max-width: 600px; margin: auto; }");
    str.append("h2 { color: #1a365d; font-weight: 700; margin-bottom: 20px; }");
    str.append("</style>");
    str.append("</head>");
    str.append("<body><div class='container'>");
    str.append("<h2>" + title + "</h2>");
    return str.toString();
}

    public static String footer(String title) {
        StringBuilder str = new StringBuilder();
        str.append("</body>");
        str.append("</html>");
        return str.toString();
    }
}