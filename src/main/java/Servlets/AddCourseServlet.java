package Servlets;

import models.DatabaseConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/addCourse")
public class AddCourseServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Connect to the database using JDBC
        try {
            Connection connection = DatabaseConnector.connectForRead();

            // Execute SQL query to get all courses
            String query = "SELECT * FROM courses";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Generate HTML response dynamically
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<html><head><title>Grit Academy</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");

            out.println("<div class='navbar'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='/allStudents'>All Students</a>");
            //out.println("<a href='/allCourses'>All Courses</a>");
            //out.println("<a href='/studentCourses'>All Students with Courses</a>");
            out.println("<a href='/addStudent'>Add Student</a>");
            out.println("<a href='/addCourse' class='active'>Add Course</a>");
            out.println("<a href='/associateStudentCourse'>Associate Student With Course </a>");
            out.println("<a href='/statistics'>Statistics</a>");
            out.println("</div>");

            out.println("<div class='container'>");
            // Display the table of courses
            out.println("<div class='courses-table'>");
            out.println("<h2>All Courses</h2><table>");
            out.println("<tr><th>ID</th><th>Name</th><th>YHP</th><th>Description</th></tr>");

            // Loop through the result set and print data in the table
            while (resultSet.next()) {
                out.println("<tr><td>" + resultSet.getInt("id") + "</td><td>" + resultSet.getString("namn")
                        + "</td><td>" + resultSet.getInt("YHP") + "</td><td>"
                        + resultSet.getString("beskrivning") + "</td></tr>");
            }

            out.println("</table></div>");

            // Display the form to add a new course
            out.println("<div class='add-course-form'>");
            out.println("<h2 class='form-heading'>Add New Course</h2>");
            out.println("<form class='form-container' action='/addCourse' method='post'>");
            out.println("<div class='form-group'><label for='courseName'>Course Name:</label>");
            out.println("<input type='text' id='courseName' name='namn'></div>");
            out.println("<div class='form-group'><label for='yhp'>YHP:</label>");
            out.println("<input type='text' id='yhp' name='YHP'></div>");
            out.println("<div class='form-group'><label for='description'>Description:</label>");
            out.println("<input type='text' id='description' name='beskrivning'></div>");
            out.println("<button type='submit' class='btn btn-primary'>Add Course</button></form>");
            out.println("</div>");

            out.println("</div>"); // Close container

            out.println("</body></html>");


            // Close resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                out.println("<h2 class='error-header'>Failed to add course. Please enter all required fields.</h2>");
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
            connection.close();
        } catch (SQLException e) {
            // Display error message
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2 class='error-header'>Failed to add course. Please try again later.</h2>");
            out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
            // Back to add course
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/addCourse';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");

        } catch (NumberFormatException e) {
            // Handle NumberFormatException if YHP is not a valid integer
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<p class='error-header'>Failed to add course. YHP must be a valid integer." + e.getMessage() + "</p>");
            // Back to add course
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/addCourse';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");
        }
    }
}
