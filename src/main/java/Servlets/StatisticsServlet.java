package Servlets;

import models.DatabaseConnector;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        try {
            // Connect to the database
            Connection connection = DatabaseConnector.connectForRead();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            // Display the statistics in HTML response with a linear gradient color
            out.println("<html><head><title>Grit Academy</title><link rel='stylesheet' type='text/css' href='/css/styles.css'>");
            out.println("<style>");
            out.println("h2 { text-align: center; }");  // Center the Statistics header
            out.println("</style>");
            out.println("</head><body>");

            out.println("<div class='navbar'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='/allStudents'>All Students</a>");
            //out.println("<a href='/allCourses'>All Courses</a>");
            //out.println("<a href='/studentCourses'>All Students with Courses</a>");

            out.println("<a href='/addStudent'>Add Student</a>");
            out.println("<a href='/addCourse'>Add Course</a>");
            out.println("<a href='/associateStudentCourse'>Associate Student With Course </a>");
            out.println("<a href='/statistics' class='active'>Statistics</a>");
            out.println("</div>");
            // Calculate statistics using SQL queries
            int totalStudents = getCount(connection, "students");
            int totalCourses = getCount(connection, "courses");
            double averageCoursesPerStudent = getAverageCoursesPerStudent(connection);

            out.println("<h2>Statistics</h2><table>");
            out.println("<tr><th>Total Students</th><th>Total Courses</th><th>Average Courses Per Student</th></tr>");
            out.println("<tr><td>" + totalStudents + "</td><td>" + totalCourses + "</td><td>" + averageCoursesPerStudent + "</td></tr>");
            out.println("</table></body></html>");


            // Close the database connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // to count total students and total courses
    private int getCount(Connection connection, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                //if the result set contains at least one row, it retrieves the value of the first column
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    // first counts how many courses each student has taken using the inner query.
    // Then calculates the average of these counts to find the average number of courses per student.
    private double getAverageCoursesPerStudent(Connection connection) throws SQLException {
        String query = "SELECT AVG(courses_count) AS average_courses_per_student FROM \n" +
                "(SELECT COUNT(*) AS courses_count FROM attendance GROUP BY student_id) AS courses_per_student";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                //if the result set contains at least one row, it retrieves the value of the first column
                return resultSet.getDouble(1);
            }
        }
        return 0.0;
    }
}