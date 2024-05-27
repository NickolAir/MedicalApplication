import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewLabTypeWindow extends JFrame {
    private JTextField labIdField;
    private JTextField labTypeField;

    public NewLabTypeWindow() {
        super("Add New Lab Type");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Lab ID Field
        JLabel labIdLabel = new JLabel("Lab ID:");
        labIdField = new JTextField();
        panel.add(labIdLabel);
        panel.add(labIdField);

        // Lab Type Field
        JLabel labTypeLabel = new JLabel("Lab Type:");
        labTypeField = new JTextField();
        panel.add(labTypeLabel);
        panel.add(labTypeField);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::saveLabType);
        panel.add(saveButton);

        add(panel);
    }

    private void saveLabType(ActionEvent e) {
        String labId = labIdField.getText().trim();
        String labType = labTypeField.getText().trim();

        if (labId.isEmpty() || labType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lab ID and Lab Type cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "INSERT INTO lab_type (lab_id, type) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(labId));
            statement.setString(2, labType);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Lab type saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after saving
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save lab type.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the lab type.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lab ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}