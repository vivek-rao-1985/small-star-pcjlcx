
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import com.auth0.jwt.interfaces.DecodedJWT;
// import com.auth0.jwt.JWT;
// import com.auth0.jwt.interfaces.Claim;

// @WebServlet("/a09/profile")
// public class ProfileServlet extends HttpServlet {

//     public static DecodedJWT getTokenPayload(HttpServletRequest request) {
//         String token = request.getHeader("Authorization").split(" ")[1];
//         return JWT.decode(token);
//     }

//     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//         String contentType = request.getHeader("Content-Type");
//         DecodedJWT decodedToken = this.getTokenPayload(request);
//         String tokenUsername = decodedToken.getClaim("username").asString();
//         if (contentType == null || !contentType.contains("application/json")) {
//             response.getWriter().println("Something suspicious is happening");
//         } else {
//             response.getWriter().println("Nothing suspicious here..");
//         }
//     }
// }



/*
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;

@WebServlet("/a09/profile")
public class ProfileServlet extends HttpServlet {

    public static DecodedJWT getTokenPayload(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        return JWT.decode(token);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contentType = request.getHeader("Content-Type");
        Pattern headerCharRegex = Pattern.compile("\\\\[rn]");
        Matcher matcher = headerCharRegex.matcher(contentType == null ? "" : contentType);
        DecodedJWT decodedToken = this.getTokenPayload(request);
        String tokenUsername = decodedToken.getClaim("username").asString();
        if (matcher.find()) {
            System.out.println(tokenUsername + " " + request.getHeader("Content-Type"));
            response.getWriter().println("Something suspicious is happening");
        } else {
            response.getWriter().println("Nothing suspicious here..");
        }
    }
}
*/

// /////// A09 updated 

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/a09/login")
public class ProfileServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "owasp10";
    String CREDIT_CARD = "1234-5678-9012-3456";

    private static final String SECRET_KEY = "secret";

    public void logInformation(String data) {
    LocalDateTime currentTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");
    String timestamp = currentTime.format(formatter);
    System.out.println(timestamp + " " + data);
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing username or password");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT id, username, password FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    
                    if(BCrypt.checkpw(password, storedHashedPassword)) {
                        int userId = rs.getInt("id");
                        String returnedUsername = rs.getString("username");

                        String token = JWT.create()
                                .withClaim("id", userId)
                                .withClaim("username", returnedUsername)
                                .sign(Algorithm.HMAC256(SECRET_KEY));

                        JSONObject jsonResponse = new JSONObject();
                        logInformation("User " + username + " logged in with token " + token + " credit card " + CREDIT_CARD);
                      
                        jsonResponse.put("token", token);

                        response.setContentType("application/json");
                        response.getWriter().println(jsonResponse.toString());
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

}