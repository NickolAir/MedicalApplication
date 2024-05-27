import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewContractWindow extends JFrame {
    private JComboBox<String> clinicComboBox;
    private JComboBox<String> labComboBox;

    public NewContractWindow() {
        super("Add New Contract");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Clinic ComboBox
        JLabel clinicLabel = new JLabel("Clinic:");
        clinicComboBox = new JComboBox<>();
        fillClinics(); // Fill clinic combo box with data from database
        panel.add(clinicLabel);
        panel.add(clinicComboBox);

        // Lab ComboBox
        JLabel labLabel = new JLabel("Lab:");
        labComboBox = new JComboBox<>();
        fillLabs(); // Fill lab combo box with data from database
        panel.add(labLabel);
        panel.add(labComboBox);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::saveContract);
        panel.add(saveButton);

        add(panel);
    }

    private void fillClinics() {
        clinicComboBox.addItem("Select Clinic");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT clinic_id, number FROM \"Clinic\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int clinicId = resultSet.getInt("clinic_id");
                int number = resultSet.getInt("number");
                clinicComboBox.addItem("Clinic #" + number + " (ID: " + clinicId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve clinics.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillLabs() {
        labComboBox.addItem("Select Lab");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT lab_id, number FROM Lab";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int labId = resultSet.getInt("lab_id");
                int number = resultSet.getInt("number");
                labComboBox.addItem("Lab #" + number + " (ID: " + labId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve labs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveContract(ActionEvent e) {
        String selectedClinic = (String) clinicComboBox.getSelectedItem();
        String selectedLab = (String) labComboBox.getSelectedItem();

        if (selectedClinic.equals("Select Clinic") || selectedLab.equals("Select Lab")) {
            JOptionPane.showMessageDialog(this, "Please select a clinic and a lab.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extract clinic ID and lab ID from selected items
        int clinicId = Integer.parseInt(selectedClinic.split("\\(ID: ")[1].replace(")", ""));
        int labId = Integer.parseInt(selectedLab.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "INSERT INTO contracts (clinic_id, lab_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, clinicId);
            statement.setInt(2, labId);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Contract saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after saving
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save contract.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the contract.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}