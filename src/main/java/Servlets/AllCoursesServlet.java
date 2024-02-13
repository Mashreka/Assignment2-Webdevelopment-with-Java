package Servlets;

import models.DatabaseConnector;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/allCourses")
public class AllCoursesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            out.println("<a href='/allStudents'>All Students</a>");
            out.println("<a href='/allCourses' class='active'>All Courses</a>");
            out.println("<a href='/studentCourses'>All Students with Courses</a>");
            out.println("<a href='/statistics'>Statistics</a>");
            out.println("<a href='/addStudents'>Add Student</a>");
            out.println("<a href='/addCourses'>Add Course</a>");
            out.println("<a href='/associateStudentCourse'>Associate Student With Course </a></div>");

            out.println("<h2>All Courses</h2><table>");
            out.println("<tr><th>ID</th><th>Name</th><th>YHP</th><th>Description</th></tr>");

            // Loop through the result set and print data in the table
            while (resultSet.next()) {
                out.println("<tr><td>" + resultSet.getInt("id") + "</td><td>" + resultSet.getString("namn")
                        + "</td><td>" + resultSet.getInt("YHP") + "</td><td>"
                        + resultSet.getString("beskrivning") + "</td></tr>");
            }

            out.println("</table></body></html>");


            // Close resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

