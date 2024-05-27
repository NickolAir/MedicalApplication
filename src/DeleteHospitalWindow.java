import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteHospitalWindow extends JFrame {
    private JComboBox<String> hospitalComboBox;

    public DeleteHospitalWindow() {
        super("Delete Hospital");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel hospitalLabel = new JLabel("Select Hospital:");
        hospitalComboBox = new JComboBox<>();
        fillHospitals(); // Fill hospital combo box with data from database
        panel.add(hospitalLabel);
        panel.add(hospitalComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteHospital);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillHospitals() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT hospital_id, number FROM \"Hospital\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int hospitalId = resultSet.getInt("hospital_id");
                int number = resultSet.getInt("number");
                hospitalComboBox.addItem("Hospital #" + number + " (ID: " + hospitalId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve hospitals.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteHospital(ActionEvent e) {
        String selectedHospital = (String) hospitalComboBox.getSelectedItem();

        if (selectedHospital == null || selectedHospital.equals("Select Hospital")) {
            JOptionPane.showMessageDialog(this, "Please select a hospital to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int hospitalId = Integer.parseInt(selectedHospital.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM \"Hospital\" WHERE hospital_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, hospitalId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Hospital deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete hospital.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the hospital.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteHospitalWindow::new);
    }
}
