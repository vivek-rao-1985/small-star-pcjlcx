
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.function.Function;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;


@WebServlet("/a08/profile")
public class DataProcessServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "owasp10";

    public static DecodedJWT getTokenPayload(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        return JWT.decode(token);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DecodedJWT decodedToken = this.getTokenPayload(request);
        String tokenUsername = decodedToken.getClaim("username").asString();
        String username = request.getParameter("username");
        if (username == null || username.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required");
            return;
        }
        if(!username.equals(tokenUsername)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username doesn't match token");
        }

        JSONObject userJson = getUserByUsername(username);
        if (userJson == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        response.setContentType("application/json");
        response.getWriter().write(userJson.toString());
    }

    private JSONObject getUserByUsername(String username) {
        JSONObject userJson = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT id, username, age FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userJson = new JSONObject();
                    userJson.put("id", resultSet.getInt("id"));
                    userJson.put("username", resultSet.getString("username"));
                    userJson.put("age", resultSet.getInt("age"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userJson;
    }

    
}



