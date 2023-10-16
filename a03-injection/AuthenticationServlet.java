/*
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

@WebServlet("/customer")
public class AuthenticationServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "owasp10";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name is required");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE name='" + name + "'")) {

            ArrayList<Map<String, Object>> customers = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> customer = new HashMap<>();
                customer.put("id", rs.getInt("id"));
                customer.put("name", rs.getString("name"));
                customer.put("surname", rs.getString("surname"));
                customer.put("credit-card", rs.getString("credit_card_number"));
                customers.add(customer);
            }

            if (customers.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customers not found");
                return;
            }

            JSONArray jsonArray = new JSONArray(customers);
            resp.setContentType("application/json");
            resp.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
*/

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

@WebServlet("/customer")
public class AuthenticationServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "owasp10";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name is required");
            return;
        }

        String query = "SELECT * FROM customers WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {

                ArrayList<Map<String, Object>> customers = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> customer = new HashMap<>();
                    customer.put("id", rs.getInt("id"));
                    customer.put("name", rs.getString("name"));
                    customer.put("surname", rs.getString("surname"));
                    customer.put("credit-card", rs.getString("credit_card_number"));
                    customers.add(customer);
                }

                if (customers.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customers not found");
                    return;
                }

                JSONArray jsonArray = new JSONArray(customers);
                resp.setContentType("application/json");
                resp.getWriter().write(jsonArray.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
