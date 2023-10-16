
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import org.json.JSONObject;
import org.json.JSONArray;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.function.Function;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import java.util.Random;
import com.github.javafaker.Faker;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.json.JSONObject;
public class AuthenticationServlets {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "owasp10";
 
    @WebServlet("/change-password")
    public static class Servlet1 extends HttpServlet {
        public static String getTokenPayload(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        return JWT.decode(token).getClaim("username").asString();
    }
      @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String password = req.getParameter("password");
        String username = this.getTokenPayload(req); 

        if (password == null || password.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password is required");
            return;
        }

        String hashedPassword = hashPassword(password);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ? RETURNING id, username")) {

            statement.setString(1, hashedPassword);
            statement.setString(2, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
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

    @WebServlet("/all-data")
    public static class Servlet2 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         JSONArray usersArray = new JSONArray();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username, password FROM users")) {

            while (resultSet.next()) {
                JSONObject userJson = new JSONObject();
                userJson.put("username", resultSet.getString("username"));
                userJson.put("password", resultSet.getString("password"));
                usersArray.put(userJson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            return;
        }

        resp.setContentType("application/json");
        resp.getWriter().write(usersArray.toString()); 
        }
    }

    @WebServlet("/register")
public class Servlet3 extends HttpServlet {

    private static final String SECRET_KEY = "secret";
    private static final Faker faker = new Faker();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing username or password");
            return;
        }

        int age = new Random().nextInt(74) + 12;
        String creditCardNumber = faker.finance().creditCard().replaceAll("-", "");;
        String hashedPassword = this.hashPassword(password);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
} }

/*
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import org.json.JSONObject;
import org.json.JSONArray;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.function.Function;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Random;
import com.github.javafaker.Faker;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.json.JSONObject;
public class AuthenticationServlets {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "owasp10";
 
    @WebServlet("/change-password")
    public static class Servlet1 extends HttpServlet {
        public static String getTokenPayload(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        return JWT.decode(token).getClaim("username").asString();
    }
      @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String password = req.getParameter("password");
        String username = this.getTokenPayload(req); 

        if (password == null || password.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password is required");
            return;
        }

        String hashedPassword = hashPassword(password);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ? RETURNING id, username")) {

            statement.setString(1, hashedPassword);
            statement.setString(2, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

    @WebServlet("/all-data")
    public static class Servlet2 extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
 JSONArray usersArray = new JSONArray();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username, password FROM users")) {

            while (resultSet.next()) {
                JSONObject userJson = new JSONObject();
                userJson.put("username", resultSet.getString("username"));
                userJson.put("password", resultSet.getString("password"));
                usersArray.put(userJson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            return;
        }

        resp.setContentType("application/json");
        resp.getWriter().write(usersArray.toString()); 
        }
    }

    @WebServlet("/register")
public class Servlet3 extends HttpServlet {

    private static final String SECRET_KEY = "secret";
    private static final Faker faker = new Faker();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing username or password");
            return;
        }

        int age = new Random().nextInt(74) + 12; // Random age between 12 and 85
        String creditCardNumber = faker.finance().creditCard().replaceAll("-", "");;
        String hashedPassword = this.hashPassword(password);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
}
*/