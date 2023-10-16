import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet("/processData")
public class DataProcessServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonData = request.getReader().lines().collect(Collectors.joining());
        
        ObjectMapper mapper = new ObjectMapper();
        MyData data = mapper.readValue(jsonData, MyData.class);
    }
}

