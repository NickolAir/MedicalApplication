import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditLabTypeWindow extends JFrame {
    private JComboBox<Integer> labTypeComboBox;
    private JTextField labIdField;
    private JTextField typeField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditLabTypeWindow() {
        super("Edit Lab Type");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Lab Type ComboBox
        panel.add(new JLabel("Select Lab Type:"));
        labTypeComboBox = new JComboBox<>();
        loadLabTypes();
        panel.add(labTypeComboBox);

        // Lab ID Field
        panel.add(new JLabel("Lab ID:"));
        labIdField = new JTextField();
        panel.add(labIdField);

        // Type Field
        panel.add(new JLabel("Type:"));
        typeField = new JTextField();
        panel.add(typeField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveLabType();
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

    private void loadLabTypes() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT type_id FROM \"lab_type\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                labTypeComboBox.addItem(resultSet.getInt("type_id"));
            }

            labTypeComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadLabTypeDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadLabTypeDetails() {
        int selectedLabTypeId = (int) labTypeComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT lab_id, type FROM \"lab_type\" WHERE type_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedLabTypeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                labIdField.setText(resultSet.getString("lab_id"));
                typeField.setText(resultSet.getString("type"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveLabType() {
        int selectedLabTypeId = (int) labTypeComboBox.getSelectedItem();
        int labId = Integer.parseInt(labIdField.getText());
        String type = typeField.getText();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"lab_type\" SET lab_id = ?, type = ? WHERE type_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, labId);
            statement.setString(2, type);
            statement.setInt(3, selectedLabTypeId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Lab Type updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Lab Type.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Lab Type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditLabTypeWindow::new);
    }
}
