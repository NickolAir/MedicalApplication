import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteLabTypeWindow extends JFrame {
    private JComboBox<String> labTypeComboBox;

    public DeleteLabTypeWindow() {
        super("Delete Lab Type");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel labTypeLabel = new JLabel("Select Lab Type:");
        labTypeComboBox = new JComboBox<>();
        fillLabTypes();
        panel.add(labTypeLabel);
        panel.add(labTypeComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteLabType);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillLabTypes() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT type_id, type FROM lab_type";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int typeId = resultSet.getInt("type_id");
                String type = resultSet.getString("type");
                labTypeComboBox.addItem(type + " (ID: " + typeId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve lab types.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLabType(ActionEvent e) {
        String selectedLabType = (String) labTypeComboBox.getSelectedItem();
        if (selectedLabType == null || selectedLabType.equals("Select Lab Type")) {
            JOptionPane.showMessageDialog(this, "Please select a lab type to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int typeId = Integer.parseInt(selectedLabType.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM lab_type WHERE type_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, typeId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Lab type deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete lab type.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the lab type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteLabTypeWindow::new);
    }
}
