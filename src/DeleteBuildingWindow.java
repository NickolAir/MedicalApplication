import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteBuildingWindow extends JFrame {
    private JComboBox<String> buildingComboBox;

    public DeleteBuildingWindow() {
        super("Delete Building");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel buildingLabel = new JLabel("Select Building:");
        buildingComboBox = new JComboBox<>();
        fillBuildings(); // Fill building combo box with data from database
        panel.add(buildingLabel);
        panel.add(buildingComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteBuilding);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillBuildings() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT building_id, number FROM \"Buiding\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int buildingId = resultSet.getInt("building_id");
                int number = resultSet.getInt("number");
                buildingComboBox.addItem("Building #" + number + " (ID: " + buildingId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve buildings.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBuilding(ActionEvent e) {
        String selectedBuilding = (String) buildingComboBox.getSelectedItem();

        if (selectedBuilding == null || selectedBuilding.equals("Select Building")) {
            JOptionPane.showMessageDialog(this, "Please select a building to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int buildingId = Integer.parseInt(selectedBuilding.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM \"Buiding\" WHERE building_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, buildingId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Building deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete building.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the building.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}