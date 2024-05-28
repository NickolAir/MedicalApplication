import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditMedicalStaffWindow extends JFrame {
    private JComboBox<Integer> staffComboBox;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField surnameField;
    private JTextField specializationField;
    private JTextField salaryCoefficientField;
    private JTextField vacationCoefficientField;
    private JTextField experienceField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditMedicalStaffWindow() {
        super("Edit Medical Staff");
        initComponents();
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));

        // Staff ComboBox
        panel.add(new JLabel("Select Medical Staff:"));
        staffComboBox = new JComboBox<>();
        loadMedicalStaff();
        panel.add(staffComboBox);

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

        // Salary Coefficient Field
        panel.add(new JLabel("Salary Coefficient:"));
        salaryCoefficientField = new JTextField();
        panel.add(salaryCoefficientField);

        // Vacation Coefficient Field
        panel.add(new JLabel("Vacation Coefficient:"));
        vacationCoefficientField = new JTextField();
        panel.add(vacationCoefficientField);

        // Experience Field
        panel.add(new JLabel("Experience:"));
        experienceField = new JTextField();
        panel.add(experienceField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMedicalStaff();
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

    private void loadMedicalStaff() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id FROM \"medical_staff\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                staffComboBox.addItem(resultSet.getInt("staff_id"));
            }

            staffComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadMedicalStaffDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadMedicalStaffDetails() {
        int selectedStaffId = (int) staffComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT first_name, last_name, surname, specialization, salary_coefficient, vacation_coefficient, experience FROM \"medical_staff\" WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedStaffId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                firstNameField.setText(resultSet.getString("first_name"));
                lastNameField.setText(resultSet.getString("last_name"));
                surnameField.setText(resultSet.getString("surname"));
                specializationField.setText(resultSet.getString("specialization"));
                salaryCoefficientField.setText(resultSet.getString("salary_coefficient"));
                vacationCoefficientField.setText(resultSet.getString("vacation_coefficient"));
                experienceField.setText(resultSet.getString("experience"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveMedicalStaff() {
        int selectedStaffId = (int) staffComboBox.getSelectedItem();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String surname = surnameField.getText();
        String specialization = specializationField.getText();
        String salaryCoefficient = salaryCoefficientField.getText();
        String vacationCoefficient = vacationCoefficientField.getText();
        String experience = experienceField.getText();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"medical_staff\" SET first_name = ?, last_name = ?, surname = ?, specialization = ?, salary_coefficient = ?, vacation_coefficient = ?, experience = ? WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, surname);
            statement.setString(4, specialization);
            statement.setFloat(5, Float.parseFloat(salaryCoefficient));
            statement.setFloat(6, Float.parseFloat(vacationCoefficient));
            statement.setInt(7, Integer.parseInt(experience));
            statement.setInt(8, selectedStaffId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Medical Staff updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Medical Staff.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Medical Staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}