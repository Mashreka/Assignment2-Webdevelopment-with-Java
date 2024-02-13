package Servlets;

import models.DatabaseConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/addCourse")
public class AddCourseServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Display form to add a new course
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Grit Academy</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");

        out.println("<div class='navbar'>");
        out.println("<a href='index.html'>Home</a>");
        out.println("<a href='/allStudents'>All Students</a>");
        out.println("<a href='/allCourses'>All Courses</a>");
        out.println("<a href='/studentCourses'>All Students with Courses</a>");
        out.println("<a href='/statistics'>Statistics</a>");
        out.println("<a href='/addStudent'>Add Student</a>");
        out.println("<a href='/addCourse' class='active'>Add Course</a>");
        out.println("<a href='/associateStudentCourse'>Associate Student With Course </a></div>");


        // Add New Course form
        out.println("<h2 class='form-heading'>Add New Course</h2>");

        out.println("<form class='form-container' action='/addCourse' method='post'>");

        out.println("<div class='form-group'><label for='courseName'>Course Name:</label>");
        out.println("<input type='text' id='courseName' name='namn'></div>");

        out.println("<div class='form-group'><label for='yhp'>YHP:</label>");
        out.println("<input type='text' id='yhp' name='YHP'></div>");

        out.println("<div class='form-group'><label for='description'>Description:</label>");
        out.println("<input type='text' id='description' name='beskrivning'></div>");

        out.println("<button type='submit' class='btn btn-primary'>Add Course</button></form>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Connection connection = DatabaseConnector.connectForInsert();

            String name = request.getParameter("namn");
            String yhpStr = request.getParameter("YHP");
            String description = request.getParameter("beskrivning");

            // Validate input data
            if (name == null || yhpStr == null || description == null ||
                    name.isEmpty() || yhpStr.isEmpty() || description.isEmpty()) {
                // If any required field is empty, display fail message
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
                out.println("<h2>Failed to add course. Please enter all required fields.</h2>");
                // Back to add course
                out.println("<div style='text-align:center;'>");
                out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/addCourse';\">Back</button>"); // Back button
                out.println("</div>");
                out.println("</body></html>");
                return; // Exit method
            }
            int yhp = Integer.parseInt(yhpStr);


            String query = "INSERT INTO courses (namn, YHP, beskrivning) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, yhp);
                preparedStatement.setString(3, description);
                preparedStatement.executeUpdate();
            }

            response.sendRedirect("/allCourses");

            connection.close();
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            // Display error message
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2>Failed to add course. Please try again later.</h2>");
            // Back to add course
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/addCourse';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");

        } catch (NumberFormatException e) {
            // Handle NumberFormatException if YHP is not a valid integer
            e.printStackTrace();
            // Display error message
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2>Failed to add course. YHP must be a valid integer.</h2>");
            // Back to add course
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/addCourse';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");
        }
    }
}
