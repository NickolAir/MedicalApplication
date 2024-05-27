import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class NewPatientWindow extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField surnameField;
    private JTextField temperatureField;
    private JTextField conditionField;
    private JTextField admissionDateField;
    private JTextField dischargeDateField;
    private JComboBox<String> hospitalComboBox;
    private JComboBox<String> clinicComboBox;

    public NewPatientWindow() {
        super("Add New Patient");
        initComponents();
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(10, 2));

        // Fields for patient information
        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("Surname:"));
        surnameField = new JTextField();
        panel.add(surnameField);

        panel.add(new JLabel("Temperature:"));
        temperatureField = new JTextField();
        panel.add(temperatureField);

        panel.add(new JLabel("Condition:"));
        conditionField = new JTextField();
        panel.add(conditionField);

        panel.add(new JLabel("Admission Date (YYYY-MM-DD):"));
        admissionDateField = new JTextField();
        panel.add(admissionDateField);

        panel.add(new JLabel("Discharge Date (YYYY-MM-DD):"));
        dischargeDateField = new JTextField();
        panel.add(dischargeDateField);

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
        JButton addButton = new JButton("Add Patient");
        addButton.addActionListener(new AddPatientActionListener());
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

    private class AddPatientActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String surname = surnameField.getText();
            float temperature = Float.parseFloat(temperatureField.getText());
            String condition = conditionField.getText();
            String admissionDate = admissionDateField.getText();
            String dischargeDate = dischargeDateField.getText();
            int hospitalId = extractIdFromComboBox(hospitalComboBox);
            int clinicId = extractIdFromComboBox(clinicComboBox);

            try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
                String query = "INSERT INTO patient (first_name, last_name, surname, temperature, condition, admission_date, discharge_date, hospital_id, clinic_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, surname);
                statement.setFloat(4, temperature);
                statement.setString(5, condition);
                statement.setDate(6, Date.valueOf(admissionDate));
                statement.setDate(7, Date.valueOf(dischargeDate));
                statement.setInt(8, hospitalId);
                statement.setInt(9, clinicId);
                statement.executeUpdate();

                JOptionPane.showMessageDialog(NewPatientWindow.this, "Patient added successfully!");
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(NewPatientWindow.this, "Error adding patient. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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
            temperatureField.setText("");
            conditionField.setText("");
            admissionDateField.setText("");
            dischargeDateField.setText("");
            hospitalComboBox.setSelectedIndex(0);
            clinicComboBox.setSelectedIndex(0);
        }
    }
}
