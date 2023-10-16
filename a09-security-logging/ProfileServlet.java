/*
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contentType = request.getHeader("Content-Type");

        if (contentType == null || !contentType.contains("application/json")) {
            response.getWriter().println("Something suspicious is happening");
        } else {
            response.getWriter().println("Nothing suspicious here..");
        }
    }
}
*/



import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contentType = request.getHeader("Content-Type");
        Pattern headerCharRegex = Pattern.compile("\\\\[rn]");
        Matcher matcher = headerCharRegex.matcher(contentType == null ? "" : contentType);

        if (matcher.find()) {
            System.out.println(request.getHeader("Content-Type"));
            response.getWriter().println("Something suspicious is happening");
        } else {
            response.getWriter().println("Nothing suspicious here..");
        }
    }
}
