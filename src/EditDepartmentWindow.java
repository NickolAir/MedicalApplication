import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditDepartmentWindow extends JFrame {
    private JComboBox<Integer> departmentComboBox;
    private JTextField buildingIdField;
    private JTextField diseaseGroupField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditDepartmentWindow() {
        super("Edit Department");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Department ComboBox
        panel.add(new JLabel("Select Department:"));
        departmentComboBox = new JComboBox<>();
        loadDepartments();
        panel.add(departmentComboBox);

        // Building ID Field
        panel.add(new JLabel("Building ID:"));
        buildingIdField = new JTextField();
        panel.add(buildingIdField);

        // Disease Group Field
        panel.add(new JLabel("Disease Group:"));
        diseaseGroupField = new JTextField();
        panel.add(diseaseGroupField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDepartment();
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

    private void loadDepartments() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT department_id FROM \"Department\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                departmentComboBox.addItem(resultSet.getInt("department_id"));
            }

            departmentComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadDepartmentDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadDepartmentDetails() {
        int selectedDepartmentId = (int) departmentComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT building_id, disease_group FROM \"Department\" WHERE department_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedDepartmentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                buildingIdField.setText(resultSet.getString("building_id"));
                diseaseGroupField.setText(resultSet.getString("disease_group"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveDepartment() {
        int selectedDepartmentId = (int) departmentComboBox.getSelectedItem();
        int buildingId = Integer.parseInt(buildingIdField.getText());
        String diseaseGroup = diseaseGroupField.getText();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"Department\" SET building_id = ?, disease_group = ? WHERE department_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, buildingId);
            statement.setString(2, diseaseGroup);
            statement.setInt(3, selectedDepartmentId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Department updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Department.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Department.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditDepartmentWindow::new);
    }
}
