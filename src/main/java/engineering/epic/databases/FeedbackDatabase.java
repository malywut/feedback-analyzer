package engineering.epic.databases;

import engineering.epic.datastorageobjects.Category;
import engineering.epic.datastorageobjects.Tag;
import engineering.epic.datastorageobjects.UserFeedback;
import engineering.epic.datastorageobjects.AtomicFeedback;
import engineering.epic.endpoints.DashboardController;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class FeedbackDatabase {
    public static final String DB_URL_NOPE = "jdbc:sqlite:src/main/resources/stored_feedback.db";
    public static final String INTLOW_QUERY = "SELECT COUNT(*) FROM AtomicFeedback WHERE ? < 25";
    public static final String INTHIGH_QUERY = "SELECT COUNT(*) FROM AtomicFeedback WHERE ? > 75";
    public static final String INTMID_QUERY = "SELECT COUNT(*) FROM AtomicFeedback WHERE ? >= 25 AND ? <= 75";

    @ConfigProperty(name = "app.database.jdbc.url")
    String dbUrl;

    public String loadString(String query, String variable) throws SQLException {
        String result = "";
        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, variable);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString(1);
                } else {
                    return "";
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred with query [" + query + "]: " + e.getMessage());
            throw new SQLException("SQL error occurred with query [" + query + "]", e);
        }
        return result;
    }

    public void executeSqlScript(String resourcePath) {
        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             Statement stmt = conn.createStatement()) {

            // Read SQL from the resource file
            String sql = readResourceFileAsString(resourcePath);

            // Simple split by ";" - might need refinement for more complex scripts
            String[] sqlStatements = sql.split(";");
            for (String statement : sqlStatements) {
                if (statement.trim().isEmpty()) {
                    continue;
                }
                try {
                    stmt.execute(statement.trim());
                } catch (SQLException e) {
                    throw new SQLException("Error executing query '" + statement + "' from script " + resourcePath + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error executing SQL script" + resourcePath + ": " + e.getMessage());
        }
    }

    public void executePost(UserFeedback feedback) {
        // SQL statement for inserting UserFeedback
        String insertUserFeedbackSQL = "INSERT INTO UserFeedback(birthYear, nationality, gender, feedback) VALUES(?,?,?,?)";

        // SQL statement for inserting AtomicFeedback
        String insertAtomicFeedbackSQL = "INSERT INTO AtomicFeedback(userFeedbackId, category, urgency, severity, impact, feedback) VALUES(?,?,?,?,?,?)";

        String retrieveTagID = "SELECT ID FROM Tag WHERE NAME = ?";
        String insertAtomicFeedbackTagSQL = "INSERT INTO AtomicFeedback_Tag (atomicFeedbackId, tagId) VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(this.dbUrl)) {
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
                } catch (SQLException ex) {
                    ex.printStackTrace();
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
            try (Connection conn = DriverManager.getConnection(this.dbUrl)) {
                System.err.print("Transaction is being rolled back");
                conn.rollback();
            } catch (SQLException excep) {
                e.printStackTrace();
                // Handle any rollback errors
            }
        }
    }

    public String readResourceFileAsString(String resourcePath) throws Exception {

        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return new String(resource.readAllBytes());
        }
    }

    public Integer loadIntHigh(String attribute) {
        if (!attribute.equals("severity") && !attribute.equals("urgency") && !attribute.equals("impact")) {
            throw new IllegalArgumentException("Invalid attribute specified. Choose 'severity', 'urgency', or 'impact'.");
        }

        String query = String.format("SELECT COUNT(*) FROM AtomicFeedback WHERE %s > 75", attribute);

        int count = 0;
        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL error occurred with query [" + query + "]: " + e.getMessage());
            throw new RuntimeException("SQL error occurred with query [" + query + "]", e);
        }

        return count;
    }

    public Integer loadIntMid(String attribute) {
        if (!attribute.equals("severity") && !attribute.equals("urgency") && !attribute.equals("impact")) {
            throw new IllegalArgumentException("Invalid attribute specified. Choose 'severity', 'urgency', or 'impact'.");
        }

        String query = String.format("SELECT COUNT(*) FROM AtomicFeedback WHERE %s >= 25 AND %s <= 75", attribute, attribute);

        int count = 0;
        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred with query [" + query + "]: " + e.getMessage());
            throw new RuntimeException("SQL error occurred with query [" + query + "]", e);
        }

        return count;
    }

    public Integer loadIntLow(String attribute) {
        if (!attribute.equals("severity") && !attribute.equals("urgency") && !attribute.equals("impact")) {
            throw new IllegalArgumentException("Invalid attribute specified. Choose 'severity', 'urgency', or 'impact'.");
        }

        String query = String.format("SELECT COUNT(*) FROM AtomicFeedback WHERE %s < 25", attribute);

        int count = 0;
        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred with query [" + query + "]: " + e.getMessage());
            throw new RuntimeException("SQL error occurred with query [" + query + "]", e);
        }

        return count;
    }

    public Map<String, Integer> getTagCounts() {
        String query = """
                SELECT Tag.name, COUNT(AtomicFeedback_Tag.tagId) AS times_attributed
                FROM Tag
                INNER JOIN AtomicFeedback_Tag ON Tag.id = AtomicFeedback_Tag.tagId
                GROUP BY Tag.name
                ORDER BY times_attributed DESC;
                """;

        Map<String, Integer> tagCounts = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String tagName = rs.getString("name");
                int count = rs.getInt("times_attributed");
                tagCounts.put(tagName, count);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred with query [" + query + "]: " + e.getMessage());
            throw new RuntimeException("SQL error occurred with query [" + query + "]", e);
        }

        return tagCounts;
    }

    public List<AtomicFeedback> fetchFeedbacks() {
        List<AtomicFeedback> feedbacks = new ArrayList<>();
        String query = """
                SELECT 
                    af.category,
                    af.severity,
                    af.urgency,
                    af.impact,
                    af.feedback,
                    GROUP_CONCAT(t.name, ', ') AS tags
                FROM 
                    AtomicFeedback af
                LEFT JOIN 
                    AtomicFeedback_Tag aft ON af.id = aft.atomicFeedbackId
                LEFT JOIN 
                    Tag t ON aft.tagId = t.id
                GROUP BY 
                    af.id
                ORDER BY 
                    af.id
                LIMIT 300;
                """;

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Category category = Category.valueOf(rs.getString("category"));
                int urgency = rs.getInt("urgency");
                int severity = rs.getInt("severity");
                int impact = rs.getInt("impact");
                String feedbackText = rs.getString("feedback");
                String tagNames = rs.getString("tags");
                List<Tag> tags = new ArrayList<>();

                if (tagNames != null) {
                    Arrays.stream(tagNames.split(",\\s*"))
                            .forEach(tagName -> tags.add(Tag.valueOf(tagName.trim())));
                }

                AtomicFeedback atomicFeedback = DashboardController.generateAtomicFeedback(tags, category, urgency, severity, impact, feedbackText);
                feedbacks.add(atomicFeedback);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred when fetching feedbacks: " + e.getMessage());
            throw new RuntimeException("SQL error occurred when fetching feedbacks", e);
        }

        return feedbacks;
    }


    public Map<String, Integer> getCategoryCounts() {
        String query = "SELECT category, COUNT(*) AS count FROM AtomicFeedback GROUP BY category";
        Map<String, Integer> categoryCounts = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                Integer count = rs.getInt("count");
                categoryCounts.put(category, count);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred with query [" + query + "]: " + e.getMessage());
            throw new RuntimeException("SQL error occurred with query [" + query + "]", e);
        }
        return categoryCounts;
    }

    public List<ExtendedAtomicFeedback> fetchExtendedFeedbacks() {
        List<ExtendedAtomicFeedback> feedbacks = new ArrayList<>();
        String query = """
            SELECT 
                af.id AS af_id,
                af.category,
                af.severity,
                af.urgency,
                af.impact,
                af.feedback,
                uf.id AS uf_id,
                uf.birthYear,
                uf.nationality,
                uf.gender,
                GROUP_CONCAT(t.name, ', ') AS tags
            FROM 
                AtomicFeedback af
            LEFT JOIN 
                UserFeedback uf ON af.userFeedbackId = uf.id
            LEFT JOIN 
                AtomicFeedback_Tag aft ON af.id = aft.atomicFeedbackId
            LEFT JOIN 
                Tag t ON aft.tagId = t.id
            GROUP BY 
                af.id
            ORDER BY 
                af.id
            LIMIT 300;
            """;

        try (Connection conn = DriverManager.getConnection(this.dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int atomicFeedbackId = rs.getInt("af_id");
                Category category = Category.valueOf(rs.getString("category"));
                int urgency = rs.getInt("urgency");
                int severity = rs.getInt("severity");
                int impact = rs.getInt("impact");
                String feedbackText = rs.getString("feedback");
                int userFeedbackId = rs.getInt("uf_id");
                int birthYear = rs.getInt("birthYear");
                String nationality = rs.getString("nationality");
                String gender = rs.getString("gender");
                String tagNames = rs.getString("tags");
                List<Tag> tags = new ArrayList<>();

                if (tagNames != null) {
                    Arrays.stream(tagNames.split(",\\s*"))
                            .forEach(tagName -> tags.add(Tag.valueOf(tagName.trim())));
                }

                ExtendedAtomicFeedback atomicFeedback = new ExtendedAtomicFeedback(
                        atomicFeedbackId, userFeedbackId, birthYear, nationality, gender, tags, category, urgency, severity, impact, feedbackText
                );
                feedbacks.add(atomicFeedback);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred when fetching extended feedbacks: " + e.getMessage());
            throw new RuntimeException("SQL error occurred when fetching extended feedbacks", e);
        }

        return feedbacks;
    }

    public static class ExtendedAtomicFeedback {
        private final int atomicFeedbackId;
        private final int userFeedbackId;
        private final int birthYear;
        private final String nationality;
        private final String gender;
        private final List<Tag> tags;
        private final Category category;
        private final int urgency;
        private final int severity;
        private final int impact;
        private final String feedback;

        public ExtendedAtomicFeedback(int atomicFeedbackId, int userFeedbackId, int birthYear, String nationality, String gender,
                                      List<Tag> tags, Category category, int urgency, int severity, int impact, String feedback) {
            this.atomicFeedbackId = atomicFeedbackId;
            this.userFeedbackId = userFeedbackId;
            this.birthYear = birthYear;
            this.nationality = nationality;
            this.gender = gender;
            this.tags = tags;
            this.category = category;
            this.urgency = urgency;
            this.severity = severity;
            this.impact = impact;
            this.feedback = feedback;
        }

        public int getAtomicFeedbackId() {
            return atomicFeedbackId;
        }

        public int getUserFeedbackId() {
            return userFeedbackId;
        }

        public int getBirthYear() {
            return birthYear;
        }

        public String getNationality() {
            return nationality;
        }

        public String getGender() {
            return gender;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public Category getCategory() {
            return category;
        }

        public int getUrgency() {
            return urgency;
        }

        public int getSeverity() {
            return severity;
        }

        public int getImpact() {
            return impact;
        }

        public String getFeedback() {
            return feedback;
        }

        public Map<String,String> getMetaInfo() {
            Map<String,String> result = new HashMap<>();
            result.put("percentage_of_people_affected", String.valueOf(impact));
            result.put("severity_in_percent", String.valueOf(severity));
            result.put("urgency_in_percent", String.valueOf(urgency));
            result.put("feedback_category", category.toString());
            result.put("feedback_tags_commaseparated", tags.stream().map(Tag::name).collect(Collectors.joining(", ")));
            result.put("birthyear", String.valueOf(birthYear));
            result.put("gender", gender);
            result.put("nationality", nationality);
            return result;
        }

    }

}
