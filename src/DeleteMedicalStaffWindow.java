import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteMedicalStaffWindow extends JFrame {
    private JComboBox<String> medicalStaffComboBox;

    public DeleteMedicalStaffWindow() {
        super("Delete Medical Staff");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel medicalStaffLabel = new JLabel("Select Medical Staff:");
        medicalStaffComboBox = new JComboBox<>();
        fillMedicalStaff(); // Fill medical staff combo box with data from database
        panel.add(medicalStaffLabel);
        panel.add(medicalStaffComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteMedicalStaff);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillMedicalStaff() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id, first_name, last_name FROM medical_staff";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                medicalStaffComboBox.addItem(firstName + " " + lastName + " (ID: " + staffId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve medical staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicalStaff(ActionEvent e) {
        String selectedMedicalStaff = (String) medicalStaffComboBox.getSelectedItem();

        if (selectedMedicalStaff == null || selectedMedicalStaff.equals("Select Medical Staff")) {
            JOptionPane.showMessageDialog(this, "Please select a medical staff to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int staffId = Integer.parseInt(selectedMedicalStaff.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM medical_staff WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, staffId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Medical staff deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete medical staff.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the medical staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteMedicalStaffWindow::new);
    }
}
