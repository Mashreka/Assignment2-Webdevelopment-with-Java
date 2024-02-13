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

//servlet mapped to the /addStudent
@WebServlet("/addStudent")
public class AddStudentServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Connect to the database using JDBC
        try {
            Connection connection = DatabaseConnector.connectForRead();

            // Display all students
            displayAllStudents(connection, response);

            // Close connection
            connection.close();
        } catch (SQLException e) {
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
            // Back to allStudents
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/allStudents';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");
        }

        // Display form to add a new student
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Grit Academy</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
        // Add New Student form
        out.println("<h2 class='form-heading'>Add New Student</h2>");

        out.println("<form class='form-container' action='/addStudent' method='post'>");//the form data will be sent via an HTTP POST request.

        out.println("<div class='form-group'><label for='firstName'>First Name:</label>");
        out.println("<input type='text' id='firstName' name='Fname'></div>");

        out.println("<div class='form-group'><label for='lastName'>Last Name:</label>");
        out.println("<input type='text' id='lastName' name='Lname'></div>");

        out.println("<div class='form-group'><label for='city'>City:</label>");
        out.println("<input type='text' id='city' name='ort'></div>");

        out.println("<div class='form-group'><label for='hobby'>Hobby:</label>");
        out.println("<input type='text' id='hobby' name='intressen'></div>");

        out.println("<button type='submit' class='btn btn-primary'>Add Student</button></form>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Connection connection = DatabaseConnector.connectForInsert();

            String fName = request.getParameter("Fname"); //the form data is sent to the server as parameters.
            String lName = request.getParameter("Lname");
            String city = request.getParameter("ort");
            String hobby = request.getParameter("intressen");

            // Validate input data
            if (fName == null || lName == null || city == null || hobby == null ||
                    fName.isEmpty() || lName.isEmpty() || city.isEmpty() || hobby.isEmpty()) {
                // If any required field is empty, display fail message
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
                out.println("<h2 class='error-header'>Failed to add student. Please enter all required fields.</h2>");
                // Back to add student
                out.println("<div style='text-align:center;'>");
                out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/addStudent';\">Back</button>"); // Back button
                out.println("</div>");
                out.println("</body></html>");
                return; // Exit method
            }


            String query = "INSERT INTO students (Fname,Lname,ort,intressen) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, fName);
                preparedStatement.setString(2, lName);
                preparedStatement.setString(3, city);
                preparedStatement.setString(4, hobby);
                preparedStatement.executeUpdate();
            }
            connection.close();
        } catch (SQLException e) {
            // Handle SQL exceptions
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2 class='error-header'>Failed to add student. Please try again later.</h2>");
            // Back to add student
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/addStudent';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");

        }
    }
    // Display all students
    private void displayAllStudents(Connection connection, HttpServletResponse response) throws SQLException, IOException {
        // Execute SQL query to get all students
        String query = "SELECT * FROM students";
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

        out.println("<a href='/addStudent' class='active'>Add Student</a>");
        out.println("<a href='/addCourse'>Add Course</a>");
        out.println("<a href='/associateStudentCourse'>Associate Student With Course </a>");
        out.println("<a href='/statistics'>Statistics</a>");
        out.println("</div>");

        out.println("<h2>All Students</h2><table>");
        out.println("<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>City</th><th>Interests</th></tr>");

        // Loop through the result set and print data in the table
        while (resultSet.next()) {
            out.println("<tr><td>" + resultSet.getInt("id") + "</td><td>" + resultSet.getString("Fname") + "</td><td>"
                    + resultSet.getString("Lname") + "</td><td>" + resultSet.getString("ort") + "</td><td>"
                    + resultSet.getString("intressen") + "</td></tr>");
        }

        out.println("</table>");
        // Close resources
        resultSet.close();
        statement.close();
    }


}
