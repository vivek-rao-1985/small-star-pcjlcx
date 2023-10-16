import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/buy-product")
public class BuyProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("{\"success\": true}");
    }
}


/*
import io.github.bucket4j.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;

@WebServlet("/buy-product")
public class MyServlet extends HttpServlet {

    private final Bucket bucket;

    public Servlet() {
        // Create a bucket with a limit of 10 tokens, and a refill rate of 10 tokens per minute.
        Bandwidth limit = Bandwidth.simple(10, Duration.ofMinutes(1));
        this.bucket = Bucket4j.builder().addLimit(limit).build();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Try to consume a single token from the bucket.
        if (bucket.tryConsume(1)) {
            // Allowed to proceed
            out.println("{\"success\": true}");
        } else {
            // Too many requests
            response.setStatus(429);
            out.println("{\"success\": false, \"message\": \"Too many requests\"}");
        }
    }
}
*/