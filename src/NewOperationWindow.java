import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class NewOperationWindow extends JFrame {
    private JComboBox<String> patientComboBox;
    private JComboBox<String> doctorComboBox;
    private JTextField operationTypeField;
    private JTextField dateField;
    private JButton saveButton;

    public NewOperationWindow() {
        super("Add New Operation");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        // Patient ComboBox
        panel.add(new JLabel("Patient:"));
        patientComboBox = new JComboBox<>();
        patientComboBox.addItem("Select Patient");
        populateComboBox(patientComboBox, "SELECT patient_id, first_name, last_name FROM patient", "patient_id", "first_name", "last_name");
        panel.add(patientComboBox);

        // Doctor ComboBox
        panel.add(new JLabel("Doctor:"));
        doctorComboBox = new JComboBox<>();
        doctorComboBox.addItem("Select Doctor");
        populateComboBox(doctorComboBox, "SELECT staff_id, first_name, last_name FROM medical_staff WHERE specialization IS NOT NULL", "staff_id", "first_name", "last_name");
        panel.add(doctorComboBox);

        // Operation Type Field
        panel.add(new JLabel("Operation Type:"));
        operationTypeField = new JTextField();
        panel.add(operationTypeField);

        // Date Field
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        panel.add(dateField);

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveOperation();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void populateComboBox(JComboBox<String> comboBox, String query, String idColumn, String firstNameColumn, String lastNameColumn) {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt(idColumn);
                String firstName = resultSet.getString(firstNameColumn);
                String lastName = resultSet.getString(lastNameColumn);
                comboBox.addItem(firstName + " " + lastName + " (ID: " + id + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveOperation() {
        String selectedPatient = (String) patientComboBox.getSelectedItem();
        String selectedDoctor = (String) doctorComboBox.getSelectedItem();
        String operationType = operationTypeField.getText();
        String date = dateField.getText();

        if (selectedPatient.equals("Select Patient") || selectedDoctor.equals("Select Doctor")) {
            JOptionPane.showMessageDialog(this, "Please select both a patient and a doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int patientId = Integer.parseInt(selectedPatient.split("\\(ID: ")[1].replace(")", ""));
        int doctorId = Integer.parseInt(selectedDoctor.split("\\(ID: ")[1].replace(")", ""));

        String insertOperationQuery = "INSERT INTO operations (patient_id, doctor_id, operation_type, date) VALUES (?, ?, ?, ?)";
        String updateOperationsCountQuery = "UPDATE operating_doctors SET operations_count = operations_count + 1 WHERE doctor_id = ?";

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            connection.setAutoCommit(false);

            try (PreparedStatement insertOperationStatement = connection.prepareStatement(insertOperationQuery);
                 PreparedStatement updateOperationsCountStatement = connection.prepareStatement(updateOperationsCountQuery)) {

                insertOperationStatement.setInt(1, patientId);
                insertOperationStatement.setInt(2, doctorId);
                insertOperationStatement.setString(3, operationType);
                insertOperationStatement.setDate(4, Date.valueOf(date));
                insertOperationStatement.executeUpdate();

                updateOperationsCountStatement.setInt(1,doctorId);
                updateOperationsCountStatement.executeUpdate();
                        connection.commit();
                JOptionPane.showMessageDialog(this, "Operation saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                connection.rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while saving the operation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}