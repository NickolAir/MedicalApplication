import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteDepartmentWindow extends JFrame {
    private JComboBox<String> departmentComboBox;

    public DeleteDepartmentWindow() {
        super("Delete Department");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel departmentLabel = new JLabel("Select Department:");
        departmentComboBox = new JComboBox<>();
        fillDepartments();
        panel.add(departmentLabel);
        panel.add(departmentComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteDepartment);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillDepartments() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT department_id, disease_group FROM \"Department\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int departmentId = resultSet.getInt("department_id");
                String diseaseGroup = resultSet.getString("disease_group");
                departmentComboBox.addItem(diseaseGroup + " (ID: " + departmentId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve departments.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDepartment(ActionEvent e) {
        String selectedDepartment = (String) departmentComboBox.getSelectedItem();
        if (selectedDepartment == null || selectedDepartment.equals("Select Department")) {
            JOptionPane.showMessageDialog(this, "Please select a department to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int departmentId = Integer.parseInt(selectedDepartment.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM \"Department\" WHERE department_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, departmentId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Department deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete department.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the department.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteDepartmentWindow::new);
    }
}