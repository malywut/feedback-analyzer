package engineering.epic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;

public class DbUtil {
    public static final String DB_URL = "jdbc:sqlite:src/main/resources/stored_feedback.db";

    public static void executeSqlScript(String resourcePath) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Read SQL from the resource file
            String sql = readResourceFileAsString(resourcePath);

            // Simple split by ";" - might need refinement for more complex scripts
            String[] sqlStatements = sql.split(";");
            for (String statement : sqlStatements) {
                if(statement.trim().isEmpty()) {
                    continue;
                }
                stmt.execute(statement.trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executePost(UserFeedback feedback) {
        // SQL statement for inserting UserFeedback
        String insertUserFeedbackSQL = "INSERT INTO UserFeedback(birthYear, nationality, gender, feedback) VALUES(?,?,?,?)";

        // SQL statement for inserting AtomicFeedback
        String insertAtomicFeedbackSQL = "INSERT INTO AtomicFeedback(userFeedbackId, category, urgency, severity, impact, feedback) VALUES(?,?,?,?,?,?)";

        String retrieveTagID = "SELECT ID FROM Tag WHERE NAME = ?";
        String insertAtomicFeedbackTagSQL = "INSERT INTO AtomicFeedback_Tag (atomicFeedbackId, tagId) VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Start transaction
            conn.setAutoCommit(false);

            // Insert UserFeedback and retrieve generated ID
            long userFeedbackId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertUserFeedbackSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, feedback.getBirthYear());
                pstmt.setString(2, feedback.getNationality());
                pstmt.setString(3, feedback.getGender());
                pstmt.setString(4, feedback.getFeedback());
                int affectedRows = pstmt.executeUpdate();

                System.out.println("Inserted UserFeedback Rows: " + affectedRows);

                if (affectedRows == 0) {
                    throw new SQLException("Creating user feedback failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userFeedbackId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Creating user feedback failed, no ID obtained.");
                    }
                }
            }

            // Insert AtomicFeedback
            for (AtomicFeedback atomicFeedback : feedback.getAtomicFeedbacks()) {
                long atomicFeedbackId;
                try (PreparedStatement pstmt = conn.prepareStatement(insertAtomicFeedbackSQL)) {
                    pstmt.setLong(1, userFeedbackId);
                    pstmt.setString(2, atomicFeedback.category.toString());
                    pstmt.setInt(3, atomicFeedback.urgency);
                    pstmt.setInt(4, atomicFeedback.severity);
                    pstmt.setInt(5, atomicFeedback.impact);
                    pstmt.setString(6, atomicFeedback.feedback);
                    int affectedRows = pstmt.executeUpdate();

                    System.out.println("Inserted AtomicFeedback Rows: " + affectedRows);

                    if (affectedRows == 0) {
                        throw new SQLException("Creating atomic feedback failed, no rows affected.");
                    }

                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            atomicFeedbackId = generatedKeys.getLong(1);
                        } else {
                            throw new SQLException("Creating user feedback failed, no ID obtained.");
                        }
                    }

                    for (Tag tagName : atomicFeedback.tags) {
                        long tagId = -1;

                        // Insert the tag or get its ID if it already exists
                        try (PreparedStatement pstmt2 = conn.prepareStatement(retrieveTagID)) {
                            pstmt2.setString(1, tagName.toString());
                            try (ResultSet rs = pstmt2.executeQuery()) { // Use executeQuery() for SELECT statements
                                if (rs.next()) { // If the tag exists, retrieve its ID
                                    tagId = rs.getLong("ID"); // It's often clearer to use column names
                                }
                            } catch (SQLException ex) {
                                System.out.println(ex.getMessage());
                                // TODO better error handling
                            }
                        }

                        // Insert the association between AtomicFeedback and Tag
                        try (PreparedStatement pstmt3 = conn.prepareStatement(insertAtomicFeedbackTagSQL)) {
                            pstmt3.setLong(1, atomicFeedbackId);
                            pstmt3.setLong(2, tagId);
                            pstmt3.executeUpdate();
                        }

                        System.out.println("Inserted AtomicFeedback link to Tag: " + affectedRows + " for tag " + tagName.toString() + " with ID " + tagId);
                    }
                } catch (SQLException ex){
                    System.out.println(ex.getMessage());
                    // TODO better error handling
                }
            }

            // Commit transaction
            conn.commit();

            // TODO log and report errors better
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            // If there is an exception, attempt to roll back changes
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                System.err.print("Transaction is being rolled back");
                conn.rollback();
            } catch (SQLException excep) {
                // Handle any rollback errors
            }
        }
    }

    public static String readResourceFileAsString(String resourcePath) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        return Files.readString(Paths.get(resource.toURI()));
    }

}
