import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteClinicWindow extends JFrame {
    private JComboBox<String> clinicComboBox;

    public DeleteClinicWindow() {
        super("Delete Clinic");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel clinicLabel = new JLabel("Select Clinic:");
        clinicComboBox = new JComboBox<>();
        fillClinics(); // Fill clinic combo box with data from database
        panel.add(clinicLabel);
        panel.add(clinicComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteClinic);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillClinics() {
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

    private void deleteClinic(ActionEvent e) {
        String selectedClinic = (String) clinicComboBox.getSelectedItem();

        if (selectedClinic == null || selectedClinic.equals("Select Clinic")) {
            JOptionPane.showMessageDialog(this, "Please select a clinic to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int clinicId = Integer.parseInt(selectedClinic.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM \"Clinic\" WHERE clinic_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, clinicId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Clinic deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete clinic.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the clinic.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteClinicWindow::new);
    }
}
