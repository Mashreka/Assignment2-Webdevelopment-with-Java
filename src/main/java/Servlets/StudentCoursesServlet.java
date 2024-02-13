package Servlets;

import models.DatabaseConnector;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/studentCourses")
public class StudentCoursesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Connect to the database using JDBC
        try {
            Connection connection = DatabaseConnector.connectForRead();

            // Execute SQL query to get student-course data
            String query = "SELECT students.id AS student_id, students.Fname, students.Lname, courses.namn " +
                    "FROM students " +
                    "JOIN attendance ON students.id = attendance.student_id " +
                    "JOIN courses ON attendance.kurs_id = courses.id";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Generate HTML response dynamically
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Grit Academy</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");

            out.println("<div class='navbar'>");
            out.println("<a href='/allStudents'>All Students</a>");
            out.println("<a href='/allCourses'>All Courses</a>");
            out.println("<a href='/studentCourses' class='active'>All Students with Courses</a>");
            out.println("<a href='/statistics'>Statistics</a>");
            out.println("<a href='/addStudent'>Add Student</a>");
            out.println("<a href='/addCourse'>Add Course</a>");
            out.println("<a href='/associateStudentCourse'>Associate Student With Course </a></div>");

            out.println("<h2>Students with Courses</h2><table>");
            out.println("<tr><th>ID</th><th>Name</th><th>Course</th></tr>");



            // Loop through the result set and print data in the table
            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                String firstName = resultSet.getString("Fname");
                String lastName = resultSet.getString("Lname");
                String courseName = resultSet.getString("namn");


                // Concatenate first name and last name into a single "Name" column
                String fullName = firstName + " " + lastName;

                //print data in the table
                out.println("<tr><td>" + studentId + "</td><td>" + fullName + "</td><td>" + courseName + "</td></tr>");
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

