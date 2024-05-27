import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewMedicalStaffWindow extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField surnameField;
    private JTextField specializationField;
    private JTextField salaryCoefficientField;
    private JTextField vacationCoefficientField;
    private JTextField experienceField;
    private JComboBox<String> hospitalComboBox;
    private JComboBox<String> clinicComboBox;

    public NewMedicalStaffWindow() {
        super("Add New Medical Staff");
        initComponents();
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(10, 2));

        // Fields for medical staff information
        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("Surname:"));
        surnameField = new JTextField();
        panel.add(surnameField);

        panel.add(new JLabel("Specialization:"));
        specializationField = new JTextField();
        panel.add(specializationField);

        panel.add(new JLabel("Salary Coefficient:"));
        salaryCoefficientField = new JTextField();
        panel.add(salaryCoefficientField);

        panel.add(new JLabel("Vacation Coefficient:"));
        vacationCoefficientField = new JTextField();
        panel.add(vacationCoefficientField);

        panel.add(new JLabel("Experience:"));
        experienceField = new JTextField();
        panel.add(experienceField);

        // Combo boxes for hospital and clinic selection
        panel.add(new JLabel("Hospital:"));
        hospitalComboBox = new JComboBox<>();
        hospitalComboBox.addItem("Select Hospital");
        populateComboBox(hospitalComboBox, "Hospital", "hospital_id", "number");
        panel.add(hospitalComboBox);

        panel.add(new JLabel("Clinic:"));
        clinicComboBox = new JComboBox<>();
        clinicComboBox.addItem("Select Clinic");
        populateComboBox(clinicComboBox, "Clinic", "clinic_id", "number");
        panel.add(clinicComboBox);

        // Add button
        JButton addButton = new JButton("Add Medical Staff");
        addButton.addActionListener(new AddMedicalStaffActionListener());
        panel.add(addButton);

        add(panel);
    }

    private void populateComboBox(JComboBox<String> comboBox, String tableName, String idColumn, String nameColumn) {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT " + idColumn + ", " + nameColumn + " FROM \"" + tableName + "\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(idColumn);
                int number = resultSet.getInt(nameColumn);
                comboBox.addItem(tableName + " #" + number + " (ID: " + id + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private class AddMedicalStaffActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String surname = surnameField.getText();
            String specialization = specializationField.getText();
            float salaryCoefficient = Float.parseFloat(salaryCoefficientField.getText());
            float vacationCoefficient = Float.parseFloat(vacationCoefficientField.getText());
            int experience = Integer.parseInt(experienceField.getText());
            int hospitalId = extractIdFromComboBox(hospitalComboBox);
            int clinicId = extractIdFromComboBox(clinicComboBox);

            try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
                String query = "INSERT INTO medical_staff (first_name, last_name, surname, specialization, salary_coefficient, vacation_coefficient, experience, hospital_id, clinic_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, surname);
                statement.setString(4, specialization);
                statement.setFloat(5, salaryCoefficient);
                statement.setFloat(6, vacationCoefficient);
                statement.setInt(7, experience);
                statement.setInt(8, hospitalId);
                statement.setInt(9, clinicId);
                statement.executeUpdate();

                JOptionPane.showMessageDialog(NewMedicalStaffWindow.this, "Medical Staff added successfully!");
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(NewMedicalStaffWindow.this, "Error adding medical staff. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private int extractIdFromComboBox(JComboBox<String> comboBox) {
            String selectedItem = (String) comboBox.getSelectedItem();
            return Integer.parseInt(selectedItem.split("\\(ID: ")[1].replace(")", ""));
        }

        private void clearFields() {
            firstNameField.setText("");
            lastNameField.setText("");
            surnameField.setText("");
            specializationField.setText("");
            salaryCoefficientField.setText("");
            vacationCoefficientField.setText("");
            experienceField.setText("");
            hospitalComboBox.setSelectedIndex(0);
            clinicComboBox.setSelectedIndex(0);
        }
    }
}