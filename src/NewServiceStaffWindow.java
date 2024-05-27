import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class NewServiceStaffWindow extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField surnameField;
    private JTextField specializationField;
    private JComboBox<String> institutionComboBox;
    private JButton saveButton;

    public NewServiceStaffWindow() {
        super("Add New Service Staff");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 2));

        // First Name
        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        // Last Name
        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        // Surname
        panel.add(new JLabel("Surname:"));
        surnameField = new JTextField();
        panel.add(surnameField);

        // Specialization
        panel.add(new JLabel("Specialization:"));
        specializationField = new JTextField();
        panel.add(specializationField);

        // Institution (Hospital/Clinic)
        panel.add(new JLabel("Institution:"));
        institutionComboBox = new JComboBox<>();
        institutionComboBox.addItem("Select Institution");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String hospitalQuery = "SELECT hospital_id, number FROM \"Hospital\"";
            PreparedStatement statement = connection.prepareStatement(hospitalQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int hospitalId = resultSet.getInt("hospital_id");
                int number = resultSet.getInt("number");
                institutionComboBox.addItem("Hospital #" + number + " (ID: " + hospitalId + ")");
            }

            String clinicQuery = "SELECT clinic_id, number FROM \"Clinic\"";
            statement = connection.prepareStatement(clinicQuery);
            resultSet = statement.executeQuery(clinicQuery);
            while (resultSet.next()) {
                int clinicId = resultSet.getInt("clinic_id");
                int number = resultSet.getInt("number");
                institutionComboBox.addItem("Clinic #" + number + " (ID: " + clinicId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        panel.add(institutionComboBox);

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveServiceStaff();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveServiceStaff() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String surname = surnameField.getText();
        String specialization = specializationField.getText();
        String selectedInstitution = (String) institutionComboBox.getSelectedItem();

        if (firstName.isEmpty() || lastName.isEmpty() || specialization.isEmpty() || selectedInstitution.equals("Select Institution")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select an institution.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer hospitalId = null;
        Integer clinicId = null;

        if (selectedInstitution.startsWith("Hospital")) {
            hospitalId = Integer.parseInt(selectedInstitution.split("\\(ID: ")[1].replace(")", ""));
        } else if (selectedInstitution.startsWith("Clinic")) {
            clinicId = Integer.parseInt(selectedInstitution.split("\\(ID: ")[1].replace(")", ""));
        }

        String query = "INSERT INTO service_staff (hospital_id, clinic_id, first_name, last_name, surname, specialization) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, hospitalId);
            statement.setObject(2, clinicId);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, surname);
            statement.setString(6, specialization);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Service staff added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding service staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        surnameField.setText("");
        specializationField.setText("");
        institutionComboBox.setSelectedIndex(0);
    }
}