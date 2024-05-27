import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class NewDoctorPatientWindow extends JFrame {
    private JComboBox<String> doctorComboBox;
    private JComboBox<String> patientComboBox;

    public NewDoctorPatientWindow() {
        super("Assign Patient to Doctor");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Doctor ComboBox
        JLabel doctorLabel = new JLabel("Doctor:");
        doctorComboBox = new JComboBox<>();
        fillDoctors(); // Fill doctor combo box with data from database
        panel.add(doctorLabel);
        panel.add(doctorComboBox);

        // Patient ComboBox
        JLabel patientLabel = new JLabel("Patient:");
        patientComboBox = new JComboBox<>();
        fillPatients(); // Fill patient combo box with data from database
        panel.add(patientLabel);
        panel.add(patientComboBox);

        // Save Button
        JButton saveButton = new JButton("Assign");
        saveButton.addActionListener(this::saveDoctorPatient);
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

    private void fillPatients() {
        patientComboBox.addItem("Select Patient");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT patient_id, first_name, last_name FROM patient";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                patientComboBox.addItem(firstName + " " + lastName + " (ID: " + patientId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve patients.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDoctorPatient(ActionEvent e) {
        String selectedDoctor = (String) doctorComboBox.getSelectedItem();
        String selectedPatient = (String) patientComboBox.getSelectedItem();

        if (selectedDoctor.equals("Select Doctor") || selectedPatient.equals("Select Patient")) {
            JOptionPane.showMessageDialog(this, "Please select both a doctor and a patient.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extract doctor ID and patient ID from selected items
        int doctorId = Integer.parseInt(selectedDoctor.split("\\(ID: ")[1].replace(")", ""));
        int patientId = Integer.parseInt(selectedPatient.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "INSERT INTO doctor_patient (doctor_id, patient_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, doctorId);
            statement.setInt(2, patientId);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Patient assigned to doctor successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after saving
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign patient to doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while assigning the patient to the doctor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
