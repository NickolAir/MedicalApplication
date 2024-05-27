import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteLabWindow extends JFrame {
    private JComboBox<String> labComboBox;

    public DeleteLabWindow() {
        super("Delete Lab");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel labLabel = new JLabel("Select Lab:");
        labComboBox = new JComboBox<>();
        fillLabs();
        panel.add(labLabel);
        panel.add(labComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteLab);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillLabs() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT lab_id, number FROM lab";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int labId = resultSet.getInt("lab_id");
                int number = resultSet.getInt("number");
                labComboBox.addItem("Lab #" + number + " (ID: " + labId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve labs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLab(ActionEvent e) {
        String selectedLab = (String) labComboBox.getSelectedItem();
        if (selectedLab == null || selectedLab.equals("Select Lab")) {
            JOptionPane.showMessageDialog(this, "Please select a lab to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int labId = Integer.parseInt(selectedLab.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM lab WHERE lab_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, labId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Lab deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete lab.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the lab.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteLabWindow::new);
    }
}
