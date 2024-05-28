import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditPatientWindow extends JFrame {
    private JComboBox<Integer> patientComboBox;
    private JTextField hospitalIdField;
    private JTextField clinicIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField surnameField;
    private JTextField temperatureField;
    private JTextField conditionField;
    private JTextField admissionDateField;
    private JTextField dischargeDateField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditPatientWindow() {
        super("Edit Patient");
        initComponents();
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));

        // Patient ComboBox
        panel.add(new JLabel("Select Patient:"));
        patientComboBox = new JComboBox<>();
        loadPatients();
        panel.add(patientComboBox);

        // Hospital ID Field
        panel.add(new JLabel("Hospital ID:"));
        hospitalIdField = new JTextField();
        panel.add(hospitalIdField);

        // Clinic ID Field
        panel.add(new JLabel("Clinic ID:"));
        clinicIdField = new JTextField();
        panel.add(clinicIdField);

        // First Name Field
        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        // Last Name Field
        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        // Surname Field
        panel.add(new JLabel("Surname:"));
        surnameField = new JTextField();
        panel.add(surnameField);

        // Temperature Field
        panel.add(new JLabel("Temperature:"));
        temperatureField = new JTextField();
        panel.add(temperatureField);

        // Condition Field
        panel.add(new JLabel("Condition:"));
        conditionField = new JTextField();
        panel.add(conditionField);

        // Admission Date Field
        panel.add(new JLabel("Admission Date:"));
        admissionDateField = new JTextField();
        panel.add(admissionDateField);

        // Discharge Date Field
        panel.add(new JLabel("Discharge Date:"));
        dischargeDateField = new JTextField();
        panel.add(dischargeDateField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePatient();
            }
        });
        panel.add(saveButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(cancelButton);

        add(panel);
    }

    private void loadPatients() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT patient_id FROM \"patient\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                patientComboBox.addItem(resultSet.getInt("patient_id"));
            }

            patientComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadPatientDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadPatientDetails() {
        int selectedPatientId = (int) patientComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT hospital_id, clinic_id, first_name, last_name, surname, temperature, condition, admission_date, discharge_date FROM \"patient\" WHERE patient_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedPatientId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                hospitalIdField.setText(resultSet.getString("hospital_id"));
                clinicIdField.setText(resultSet.getString("clinic_id"));
                firstNameField.setText(resultSet.getString("first_name"));
                lastNameField.setText(resultSet.getString("last_name"));
                surnameField.setText(resultSet.getString("surname"));
                temperatureField.setText(resultSet.getString("temperature"));
                conditionField.setText(resultSet.getString("condition"));
                admissionDateField.setText(resultSet.getString("admission_date"));
                dischargeDateField.setText(resultSet.getString("discharge_date"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void savePatient() {
        int selectedPatientId = (int) patientComboBox.getSelectedItem();
        int hospitalId = Integer.parseInt(hospitalIdField.getText());
        int clinicId = Integer.parseInt(clinicIdField.getText());
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String surname = surnameField.getText();
        float temperature = Float.parseFloat(temperatureField.getText());
        String condition = conditionField.getText();
        Date admissionDate = Date.valueOf(admissionDateField.getText());
        Date dischargeDate = Date.valueOf(dischargeDateField.getText());

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"patient\" SET hospital_id = ?, clinic_id = ?, first_name = ?, last_name = ?, surname = ?, temperature = ?, condition = ?, admission_date = ?, discharge_date = ? WHERE patient_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, hospitalId);
            statement.setInt(2, clinicId);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, surname);
            statement.setFloat(6, temperature);
            statement.setString(7, condition);
            statement.setDate(8, admissionDate);
            statement.setDate(9, dischargeDate);
            statement.setInt(10, selectedPatientId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Patient.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditPatientWindow::new);
    }
}