/*
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/a04/buy-product")
public class BuyProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("{\"success\": true}");
    }
}

*/

import io.github.bucket4j.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;

@WebServlet("/a04/buy-product")
public class BuyProductServlet extends HttpServlet {

    private final Bucket bucket;

    public BuyProductServlet() {
        Bandwidth limit = Bandwidth.simple(10, Duration.ofMinutes(1));
        this.bucket = Bucket4j.builder().addLimit(limit).build();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (bucket.tryConsume(1)) {
            out.println("{\"success\": true}");
        } else {
            response.setStatus(429);
            out.println("{\"message\": \"Too many requests\"}");
        }
    }
}
