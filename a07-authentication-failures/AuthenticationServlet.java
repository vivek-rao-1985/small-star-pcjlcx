
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import com.github.javafaker.Faker;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet("/a07/register")
public class AuthenticationServlet extends HttpServlet {

    private static final String SECRET_KEY = "secret";
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "owasp10";
    private static final Faker faker = new Faker();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing username or password");
            return;
        }

        int age = new Random().nextInt(74) + 12;
        String creditCardNumber = faker.finance().creditCard().replaceAll("-", "");
        creditCardNumber = creditCardNumber.substring(0, 16);
        String hashedPassword = this.hashPassword(password);
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, age, credit_card_number) VALUES (?, ?, ?, ?) RETURNING id, username")) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setInt(3, age);
            stmt.setString(4, creditCardNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String returnedUsername = rs.getString("username");

                    String token = JWT.create()
                            .withClaim("id", userId)
                            .withClaim("username", returnedUsername)
                            .sign(Algorithm.HMAC256(SECRET_KEY));

                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("token", token);

                    response.setContentType("application/json");
                    response.getWriter().println(jsonResponse.toString());
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
     private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
} 



