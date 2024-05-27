import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteBedWindow extends JFrame {
    private JComboBox<String> bedComboBox;

    public DeleteBedWindow() {
        super("Delete Bed");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel bedLabel = new JLabel("Select Bed:");
        bedComboBox = new JComboBox<>();
        fillBeds();
        panel.add(bedLabel);
        panel.add(bedComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteBed);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillBeds() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT bed_id, room_id FROM bed";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int bedId = resultSet.getInt("bed_id");
                int roomId = resultSet.getInt("room_id");
                bedComboBox.addItem("Bed in Room #" + roomId + " (ID: " + bedId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve beds.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBed(ActionEvent e) {
        String selectedBed = (String) bedComboBox.getSelectedItem();
        if (selectedBed == null || selectedBed.equals("Select Bed")) {
            JOptionPane.showMessageDialog(this, "Please select a bed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bedId = Integer.parseInt(selectedBed.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM bed WHERE bed_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, bedId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Bed deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete bed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the bed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteBedWindow::new);
    }
}
