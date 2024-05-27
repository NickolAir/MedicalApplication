import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeletePatientWindow extends JFrame {
    private JComboBox<String> patientComboBox;

    public DeletePatientWindow() {
        super("Delete Patient");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel patientLabel = new JLabel("Select Patient:");
        patientComboBox = new JComboBox<>();
        fillPatients(); // Fill patient combo box with data from database
        panel.add(patientLabel);
        panel.add(patientComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deletePatient);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillPatients() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT patient_id, first_name, last_name FROM patient";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                patientComboBox.addItem(firstName + " " + lastName + " (ID: " + patientId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve patients.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient(ActionEvent e) {
        String selectedPatient = (String) patientComboBox.getSelectedItem();

        if (selectedPatient == null || selectedPatient.equals("Select Patient")) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int patientId = Integer.parseInt(selectedPatient.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM patient WHERE patient_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, patientId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Patient deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the patient.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeletePatientWindow::new);
    }
}
