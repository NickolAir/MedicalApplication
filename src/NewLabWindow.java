import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewLabWindow extends JFrame {
    private JTextField labNumberField;

    public NewLabWindow() {
        super("Add New Lab");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        // Lab Number Field
        JLabel labNumberLabel = new JLabel("Lab Number:");
        labNumberField = new JTextField();
        panel.add(labNumberLabel);
        panel.add(labNumberField);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::saveLab);
        panel.add(saveButton);

        add(panel);
    }

    private void saveLab(ActionEvent e) {
        String labNumber = labNumberField.getText().trim();

        if (labNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lab number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "INSERT INTO lab (number) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(labNumber));
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Lab saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after saving
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save lab.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the lab.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lab number must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}