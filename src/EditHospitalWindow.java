import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditHospitalWindow extends JFrame {
    private JComboBox<Integer> hospitalComboBox;
    private JTextField clinicIdField;
    private JTextField numberField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditHospitalWindow() {
        super("Edit Hospital");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Hospital ComboBox
        panel.add(new JLabel("Select Hospital:"));
        hospitalComboBox = new JComboBox<>();
        loadHospitals();
        panel.add(hospitalComboBox);

        // Clinic ID Field
        panel.add(new JLabel("Clinic ID:"));
        clinicIdField = new JTextField();
        panel.add(clinicIdField);

        // Number Field
        panel.add(new JLabel("Number:"));
        numberField = new JTextField();
        panel.add(numberField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveHospital();
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

    private void loadHospitals() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT hospital_id FROM \"Hospital\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                hospitalComboBox.addItem(resultSet.getInt("hospital_id"));
            }

            hospitalComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadHospitalDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadHospitalDetails() {
        int selectedHospitalId = (int) hospitalComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT clinic_id, number FROM \"Hospital\" WHERE hospital_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedHospitalId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                clinicIdField.setText(String.valueOf(resultSet.getInt("clinic_id")));
                numberField.setText(String.valueOf(resultSet.getInt("number")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveHospital() {
        int selectedHospitalId = (int) hospitalComboBox.getSelectedItem();
        int clinicId = Integer.parseInt(clinicIdField.getText());
        int number = Integer.parseInt(numberField.getText());

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"Hospital\" SET clinic_id = ?, number = ? WHERE hospital_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, clinicId);
            statement.setInt(2, number);
            statement.setInt(3, selectedHospitalId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Hospital updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update hospital.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the hospital.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
