import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteIllnessWindow extends JFrame {
    private JComboBox<String> illnessComboBox;

    public DeleteIllnessWindow() {
        super("Delete Illness");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel illnessLabel = new JLabel("Select Illness:");
        illnessComboBox = new JComboBox<>();
        fillIllnesses();
        panel.add(illnessLabel);
        panel.add(illnessComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteIllness);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillIllnesses() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT illness_id, illness_name FROM illness";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int illnessId = resultSet.getInt("illness_id");
                String illnessName = resultSet.getString("illness_name");
                illnessComboBox.addItem(illnessName + " (ID: " + illnessId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve illnesses.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteIllness(ActionEvent e) {
        String selectedIllness = (String) illnessComboBox.getSelectedItem();
        if (selectedIllness == null || selectedIllness.equals("Select Illness")) {
            JOptionPane.showMessageDialog(this, "Please select an illness to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int illnessId = Integer.parseInt(selectedIllness.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM illness WHERE illness_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, illnessId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Illness deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete illness.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the illness.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteIllnessWindow::new);
    }
}
