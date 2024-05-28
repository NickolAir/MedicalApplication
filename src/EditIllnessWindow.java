import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditIllnessWindow extends JFrame {
    private JComboBox<Integer> illnessComboBox;
    private JTextField diseaseGroupField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditIllnessWindow() {
        super("Edit Illness");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Illness ComboBox
        panel.add(new JLabel("Select Illness:"));
        illnessComboBox = new JComboBox<>();
        loadIllnesses();
        panel.add(illnessComboBox);

        // Disease Group Field
        panel.add(new JLabel("Disease Group:"));
        diseaseGroupField = new JTextField();
        panel.add(diseaseGroupField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveIllness();
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

    private void loadIllnesses() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT department_id FROM \"Department\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                illnessComboBox.addItem(resultSet.getInt("department_id"));
            }

            illnessComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadIllnessDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadIllnessDetails() {
        int selectedIllnessId = (int) illnessComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT disease_group FROM \"Department\" WHERE department_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedIllnessId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                diseaseGroupField.setText(resultSet.getString("disease_group"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveIllness() {
        int selectedIllnessId = (int) illnessComboBox.getSelectedItem();
        String diseaseGroup = diseaseGroupField.getText();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"Department\" SET disease_group = ? WHERE department_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, diseaseGroup);
            statement.setInt(2, selectedIllnessId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Illness updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Illness.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Illness.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditIllnessWindow::new);
    }
}
