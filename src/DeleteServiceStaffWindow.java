import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteServiceStaffWindow extends JFrame {
    private JComboBox<String> serviceStaffComboBox;

    public DeleteServiceStaffWindow() {
        super("Delete Service Staff");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel serviceStaffLabel = new JLabel("Select Service Staff:");
        serviceStaffComboBox = new JComboBox<>();
        fillServiceStaff(); // Fill service staff combo box with data from database
        panel.add(serviceStaffLabel);
        panel.add(serviceStaffComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteServiceStaff);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillServiceStaff() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id, first_name, last_name FROM service_staff";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                serviceStaffComboBox.addItem(firstName + " " + lastName + " (ID: " + staffId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve service staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteServiceStaff(ActionEvent e) {
        String selectedServiceStaff = (String) serviceStaffComboBox.getSelectedItem();

        if (selectedServiceStaff == null || selectedServiceStaff.equals("Select Service Staff")) {
            JOptionPane.showMessageDialog(this, "Please select a service staff to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int staffId = Integer.parseInt(selectedServiceStaff.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM service_staff WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, staffId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Service staff deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete service staff.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the service staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteServiceStaffWindow::new);
    }
}