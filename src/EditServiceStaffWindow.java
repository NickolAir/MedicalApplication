import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditServiceStaffWindow extends JFrame {
    private JComboBox<Integer> staffComboBox;
    private JTextField hospitalIdField;
    private JTextField clinicIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField surnameField;
    private JTextField specializationField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditServiceStaffWindow() {
        super("Edit Service Staff");
        initComponents();
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        // Staff ComboBox
        panel.add(new JLabel("Select Staff:"));
        staffComboBox = new JComboBox<>();
        loadServiceStaff();
        panel.add(staffComboBox);

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

        // Specialization Field
        panel.add(new JLabel("Specialization:"));
        specializationField = new JTextField();
        panel.add(specializationField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveServiceStaff();
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

    private void loadServiceStaff() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id FROM \"service_staff\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                staffComboBox.addItem(resultSet.getInt("staff_id"));
            }

            staffComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadServiceStaffDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadServiceStaffDetails() {
        int selectedStaffId = (int) staffComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT hospital_id, clinic_id, first_name, last_name, surname, specialization FROM \"service_staff\" WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedStaffId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                hospitalIdField.setText(resultSet.getString("hospital_id"));
                clinicIdField.setText(resultSet.getString("clinic_id"));
                firstNameField.setText(resultSet.getString("first_name"));
                lastNameField.setText(resultSet.getString("last_name"));
                surnameField.setText(resultSet.getString("surname"));
                specializationField.setText(resultSet.getString("specialization"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveServiceStaff() {
        int selectedStaffId = (int) staffComboBox.getSelectedItem();
        int hospitalId = Integer.parseInt(hospitalIdField.getText());
        int clinicId = Integer.parseInt(clinicIdField.getText());
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String surname = surnameField.getText();
        String specialization = specializationField.getText();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"service_staff\" SET hospital_id = ?, clinic_id = ?, first_name = ?, last_name = ?, surname = ?, specialization = ? WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, hospitalId);
            statement.setInt(2, clinicId);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, surname);
            statement.setString(6, specialization);
            statement.setInt(7, selectedStaffId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Service Staff updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Service Staff.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Service Staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditServiceStaffWindow::new);
    }
}