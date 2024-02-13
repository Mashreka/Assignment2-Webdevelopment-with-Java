package Servlets;

import models.DatabaseConnector;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/allStudents")
public class AllStudentsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            out.println("<h2 class='error-header'>Failed to retrieve students data</h2>");
            out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
            // Back to allStudents
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/allStudents';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form data for searching a specific student's courses
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String studentIDString = request.getParameter("studentID");

        // Convert studentIDString to an integer
        int studentID = 0;
        if(studentIDString != null && !studentIDString.isEmpty()) {
            try {
                studentID = Integer.parseInt(studentIDString);
            } catch (NumberFormatException e) {
                PrintWriter out = response.getWriter();
                out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
                out.println("<h2 class='error-header'>Failed to parse student ID</h2>");
                out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
                // Back to allStudents
                out.println("<div style='text-align:center;'>");
                out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/allStudents';\">Back</button>"); // Back button
                out.println("</div>");
                out.println("</body></html>");
                return;
            }
        }


        // Connect to the database using JDBC
        try {
            Connection connection = DatabaseConnector.connectForRead();

            // Display courses for the specific student
            if (firstName != null && lastName != null) {
                displayStudentCourses(connection, firstName, lastName, studentID,response);
            }

            // Close connection
            connection.close();
        } catch (SQLException e) {
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2 class='error-header'>Failed to retrieve student courses data</h2>");
            out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
            // Back to allStudents
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/allStudents';\">Back</button>"); // Back button
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
        out.println("<a href='/allStudents' class='active'>All Students</a>");
        //out.println("<a href='/allCourses'>All Courses</a>");
        //out.println("<a href='/studentCourses'>All Students with Courses</a>");
        out.println("<a href='/addStudent'>Add Student</a>");
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

        // Reset button
        out.println("<div style='text-align:center;'>");
        out.println("<form method='get' action='/allStudents'>");
        out.println("<button type='submit' class='btn btn-secondary'>RESET</button></form>");
        out.println("</div>");

        // Search for Student's Courses form
        out.println("<h2 class='form-heading'>Search for Student's Courses</h2>");

        out.println("<form class='form-container' method='post' action='/allStudents'>");

        out.println("<div class='form-group'><label for='searchFirstName'>First Name:</label>");
        out.println("<input type='text' id='searchFirstName' name='firstName' required></div>");

        out.println("<div class='form-group'><label for='searchLastName'>Last Name:</label>");
        out.println("<input type='text' id='searchLastName' name='lastName' required></div>");

        out.println("<div class='form-group'><label for='searchStudentID'>Student ID:</label>");
        out.println("<input type='text' id='searchStudentID' name='studentID'></div>");

        out.println("<button type='submit' class='btn btn-primary'>Submit</button>");

        out.println("</form>");

        out.println("</body></html>");

        // Close resources
        resultSet.close();
        statement.close();
    }

    // Display courses for a specific student
    private void displayStudentCourses(Connection connection, String firstName, String lastName,int studentID, HttpServletResponse response) throws SQLException, IOException {
        // Execute SQL query to get courses for the specific student
        String query = "SELECT students.id AS student_id, students.Fname AS student_fname, students.Lname AS student_lname, courses.namn AS course_name FROM students " +
                "INNER JOIN attendance ON students.id = attendance.student_id " +
                "INNER JOIN courses ON attendance.kurs_id = courses.id " +
                "WHERE students.Fname = ? AND students.Lname = ? AND students.id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, firstName);
        preparedStatement.setString(2, lastName);
        preparedStatement.setInt(3, studentID);

        ResultSet resultSet = preparedStatement.executeQuery();

        // Generate HTML response dynamically
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Grit Academy</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");

        out.println("<div class='navbar'>");
        out.println("<a href='index.html'>Home</a>");
        out.println("<a href='/studentCourses'>All Students with Courses</a>");
        out.println("<a href='/allStudents' class='active'>All Students</a>");
        out.println("<a href='/allCourses' >All Courses</a>");
        out.println("<a href='/statistics'>Statistics</a>");
        out.println("<a href='/addStudent'>Add Student</a>");
        out.println("<a href='/addCourse'>Add Course</a>");
        out.println("<a href='/associateStudentCourse'>Associate Student With Course </a></div>");

        // Check if any results are returned
        if (!resultSet.isBeforeFirst()) {
            // No results found for the given student name
            out.println("<h2 class='error-header'>Failed to find student or courses</h2>");
            out.println("<p class='error-header'>No student with the name " + firstName + " " + lastName + " found, or no courses are associated with this student.</p>");
            // Back to all students
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/allStudents';\">Back</button>"); // Back button
            out.println("</div>");

        } else {
            // Results found, display courses
            out.println("<h2>Courses for " + firstName + " " + lastName + "</h2><table>");
            out.println("<tr><th>Student ID</th><th>Student Name</th><th>Course Name</th></tr>");

            // Loop through the result set and print courses
            while (resultSet.next()) {
                out.println("<tr><td>" + resultSet.getInt("student_id")
                        + "</td><td>" + resultSet.getString("student_fname")
                        + " " + resultSet.getString("student_lname") + "</td><td>"
                        + resultSet.getString("course_name") + "</td></tr>");
            }

            out.println("</table>");
            // Back to all students button
            out.println("<div style='text-align:center;'>");
            out.println("<form method='get' action='/allStudents'>");
            out.println("<button type='submit' class='btn btn-secondary'>RESET</button></form>");
            out.println("</div>");


        }
        out.println("</body></html>");


        // Close resources
        resultSet.close();
        preparedStatement.close();
    }
}
