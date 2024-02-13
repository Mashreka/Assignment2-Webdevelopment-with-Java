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

@WebServlet("/associateStudentCourse")
public class AssociateStudentCourseServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Connection connection = DatabaseConnector.connectForRead();

            // Retrieve students and courses from the database
            String studentsQuery = "SELECT * FROM students";
            String coursesQuery = "SELECT * FROM courses";

            // Create prepared statements
            PreparedStatement studentsStatement = connection.prepareStatement(studentsQuery);
            PreparedStatement coursesStatement = connection.prepareStatement(coursesQuery);

            ResultSet studentsResultSet = studentsStatement.executeQuery();
            ResultSet coursesResultSet = coursesStatement.executeQuery();


            // Prepare HTML form to associate student with course
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            // Prepare HTML response
            out.println("<html><head><title>Grit Academy</title>");
            out.println("<link rel='stylesheet' type='text/css' href='/css/styles.css'>");
            out.println("</head><body>");

            // Display navigation links
            out.println("<div class='navbar'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='/allStudents'>All Students</a>");
            //out.println("<a href='/allCourses'>All Courses</a>");
            //out.println("<a href='/studentCourses'>All Students with Courses</a>");

            out.println("<a href='/addStudent'>Add Student</a>");
            out.println("<a href='/addCourse'>Add Course</a>");
            out.println("<a href='/associateStudentCourse' class='active'>Associate Student With Course </a>");
            out.println("<a href='/statistics'>Statistics</a>");
            out.println("</div>");

            // Display all students in a table
            out.println("<h2>All Students</h2><table>");
            out.println("<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>City</th><th>Interests</th></tr>");

            // Loop through the result set and print data in the table
            while (studentsResultSet.next()) {
                out.println("<tr><td>" + studentsResultSet.getInt("id") + "</td><td>" + studentsResultSet.getString("Fname") + "</td><td>"
                        + studentsResultSet.getString("Lname") + "</td><td>" + studentsResultSet.getString("ort") + "</td><td>"
                        + studentsResultSet.getString("intressen") + "</td></tr>");
            }

            out.println("</table>");

            // Display all courses in a table
            out.println("<h2>All Courses</h2><table>");
            out.println("<tr><th>ID</th><th>Name</th><th>YHP</th><th>Description</th></tr>");

            // Loop through the result set and print data in the table
            while (coursesResultSet.next()) {
                out.println("<tr><td>" + coursesResultSet.getInt("id") + "</td><td>" + coursesResultSet.getString("namn")
                        + "</td><td>" + coursesResultSet.getInt("YHP") + "</td><td>"
                        + coursesResultSet.getString("beskrivning") + "</td></tr>");
            }

            out.println("</table>");

            // Associate Student with Course form
            studentsResultSet = studentsStatement.executeQuery();
            coursesResultSet = coursesStatement.executeQuery();
            out.println("<h2 class='form-heading'>Associate Student with Course</h2>");
            out.println("<form class='form-container' action='/associateStudentCourse' method='post'>");

            out.println("<div class='form-group'><label for='selectStudent'>Select Student:</label>");
            out.println("<select id='selectStudent' name='student_id'>");
            while (studentsResultSet.next()) {
                int studentId = studentsResultSet.getInt("id");
                String fullName = studentsResultSet.getString("Fname") + " " + studentsResultSet.getString("Lname");
                out.println("<option value='" + studentId + "'>" + studentId + ". " + fullName + "</option>");
            }
            out.println("</select></div>");
            out.println("<div class='form-group'><label for='selectCourse'>Select Course:</label>");
            out.println("<select id='selectCourse' name='kurs_id'>");
            while (coursesResultSet.next()) {
                out.println("<option value='" + coursesResultSet.getInt("id") + "'>"
                        + coursesResultSet.getString("namn") + "</option>");
            }
            out.println("</select></div>");
            out.println("<button type='submit' class='btn btn-primary'>Associate</button></form>");



            // Check if the "Check Associations" button has been clicked
            boolean showTable = false;
            String checkAssociationsButton = request.getParameter("checkAssociationsButton");
            if (checkAssociationsButton != null && checkAssociationsButton.equals("true")) {
                showTable = true; // Set the variable to true to show the table
            }

            // Check if the "Reset" button has been clicked
            boolean resetButtonClicked = false;
            String resetButton = request.getParameter("resetButton");
            if (resetButton != null && resetButton.equals("true")) {
                showTable = false; // Hide the table
                resetButtonClicked = true;
            }

            // create "Check Associations" button
            out.println("<div style='text-align:center;'>");
            out.println("<form action='/associateStudentCourse' method='Get'>");
            out.println("<input type='hidden' name='checkAssociationsButton' value='true'>");
            out.println("<button type='submit' class='btn btn-secondary'>Check Associations</button>");
            out.println("</form>");
            out.println("</div>");

            // create reset button if the table is visible
            if (showTable) {
                out.println("<div style='text-align:center;'>");
                out.println("<form action='/associateStudentCourse' method='Get'>");
                out.println("<input type='hidden' name='resetButton' value='true'>");
                out.println("<button type='submit' class='btn btn-secondary'>Reset</button>");
                out.println("</form>");
                out.println("</div>");
            }

            // create table only if showTable is true
            if (showTable && !resetButtonClicked) {
                out.println("<h2>Students with Courses</h2>");
                out.println("<table>");
                out.println("<tr><th>ID</th><th>Name</th><th>Course</th></tr>");

                // Execute SQL query to get student-course data
                String query = "SELECT students.id AS student_id, students.Fname, students.Lname, courses.namn " +
                        "FROM students " +
                        "JOIN attendance ON students.id = attendance.student_id " +
                        "JOIN courses ON attendance.kurs_id = courses.id";

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Loop through the result set and print data in the table
                while (resultSet.next()) {
                    int studentId = resultSet.getInt("student_id");
                    String firstName = resultSet.getString("Fname");
                    String lastName = resultSet.getString("Lname");
                    String courseName = resultSet.getString("namn");

                    // Concatenate first name and last name
                    String fullName = firstName + " " + lastName;

                    // Print data in the table
                    out.println("<tr><td>" + studentId + "</td><td>" + fullName + "</td><td>" + courseName + "</td></tr>");
                }

                out.println("</table>");
            }

            // Close resources
            studentsResultSet.close();
            coursesResultSet.close();
            studentsStatement.close();
            coursesStatement.close();
            connection.close();
        } catch (SQLException e) {
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2 class='error-header'>Failed to retrieve data</h2>");
            out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
            // Back to associateStudentCourse
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/associateStudentCourse';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Connection connection = DatabaseConnector.connectForInsert();
            Connection connection2 = DatabaseConnector.connectForRead();

            int studentId = Integer.parseInt(request.getParameter("student_id"));
            int courseId = Integer.parseInt(request.getParameter("kurs_id"));

            // Verify that student and course IDs are retrieved correctly
            System.out.println("Student ID: " + studentId);
            System.out.println("Course ID: " + courseId);

            // Check if the association already exists
            if (associationExists(connection2, studentId, courseId)) {
                // Set content type to HTML
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
                out.println("<h2 class='error-header'>Association Failed</h2>");
                out.println("<p class='error-header'>Student is already associated with this course.</p>");
                // Back to associateStudentCourse
                out.println("<div style='text-align:center;'>");
                out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/associateStudentCourse';\">Back</button>"); // Back button
                out.println("</div>");
                out.println("</body></html>");
                return;
            }

            // Associate student with course in the database
            String query = "INSERT INTO attendance (student_id, kurs_id) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, studentId);
                preparedStatement.setInt(2, courseId);
                int rowsAffected = preparedStatement.executeUpdate();

                // Check if the association was successfully inserted
                if (rowsAffected > 0) {
                    response.sendRedirect("/associateStudentCourse");
                } else {
                    // Send an error message if no rows were affected
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
                    out.println("<h2 class='error-header'>Failed to associate student with course. No rows affected.</h2>");
                    // Back to associateStudentCourse
                    out.println("<div style='text-align:center;'>");
                    out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/associateStudentCourse';\">Back</button>"); // Back button
                    out.println("</div>");
                }
            }

            // Close resources
            connection.close();
        } catch (NumberFormatException e) {
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2 class='error-header'>Invalid student or course ID</h2>");
            out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
            // Back to associateStudentCourse
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/associateStudentCourse';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");

        } catch (SQLException e) {
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Error</title><link rel='stylesheet' type='text/css' href='/css/styles.css'></head><body>");
            out.println("<h2 class='error-header'>Failed to associate student with course</h2>");
            out.println("<p class='error-header'>Error: " + e.getMessage() + "</p>");
            // Back to associateStudentCourse
            out.println("<div style='text-align:center;'>");
            out.println("<button class='btn btn-secondary' onclick=\"window.location.href='/associateStudentCourse';\">Back</button>"); // Back button
            out.println("</div>");
            out.println("</body></html>");
        }

    }

    private boolean associationExists(Connection connection2, int studentId, int courseId) throws SQLException {
        String query = "SELECT * FROM attendance WHERE student_id = ? AND kurs_id = ?";
        try (PreparedStatement preparedStatement = connection2.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

}
