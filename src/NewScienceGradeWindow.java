import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class NewScienceGradeWindow extends JFrame {
    private JComboBox<String> doctorComboBox;
    private JTextField gradeField;
    private JTextField rankField;

    public NewScienceGradeWindow() {
        super("Add New Science Grade and Rank");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2));

        // Doctor ComboBox
        JLabel doctorLabel = new JLabel("Doctor:");
        doctorComboBox = new JComboBox<>();
        fillDoctors(); // Fill doctor combo box with data from database
        panel.add(doctorLabel);
        panel.add(doctorComboBox);

        // Grade Field
        JLabel gradeLabel = new JLabel("Grade:");
        gradeField = new JTextField();
        panel.add(gradeLabel);
        panel.add(gradeField);

        // Rank Field
        JLabel rankLabel = new JLabel("Rank:");
        rankField = new JTextField();
        panel.add(rankLabel);
        panel.add(rankField);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::saveScienceGrade);
        panel.add(saveButton);

        add(panel);
    }

    private void fillDoctors() {
        doctorComboBox.addItem("Select Doctor");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id, first_name, last_name FROM medical_staff";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                doctorComboBox.addItem(firstName + " " + lastName + " (ID: " + staffId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve doctors.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveScienceGrade(ActionEvent e) {
        String selectedDoctor = (String) doctorComboBox.getSelectedItem();
        String grade = gradeField.getText();
        String rank = rankField.getText();

        if (selectedDoctor.equals("Select Doctor") || grade.isEmpty() || rank.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a doctor and fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extract doctor ID from selected item
        int doctorId = Integer.parseInt(selectedDoctor.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "INSERT INTO science_grade (doctor_id, grade, rank) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, doctorId);
            statement.setString(2, grade);
            statement.setString(3, rank);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Science grade and rank saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after saving
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save science grade and rank.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the science grade and rank.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}