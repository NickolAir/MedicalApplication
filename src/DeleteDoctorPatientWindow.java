import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteDoctorPatientWindow extends JFrame {
    private JComboBox<String> doctorPatientComboBox;

    public DeleteDoctorPatientWindow() {
        super("Delete Doctor-Patient Association");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel doctorPatientLabel = new JLabel("Select Doctor-Patient Association:");
        doctorPatientComboBox = new JComboBox<>();
        fillDoctorPatients();
        panel.add(doctorPatientLabel);
        panel.add(doctorPatientComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteDoctorPatient);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillDoctorPatients() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT doctor_id, patient_id FROM doctor_patient";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int doctorId = resultSet.getInt("doctor_id");
                int patientId = resultSet.getInt("patient_id");
                doctorPatientComboBox.addItem("Doctor ID: " + doctorId + " - Patient ID: " + patientId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve doctor-patient associations.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDoctorPatient(ActionEvent e) {
        String selectedDoctorPatient = (String) doctorPatientComboBox.getSelectedItem();
        if (selectedDoctorPatient == null || selectedDoctorPatient.equals("Select Doctor-Patient Association")) {
            JOptionPane.showMessageDialog(this, "Please select a doctor-patient association to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int doctorId = Integer.parseInt(selectedDoctorPatient.split(" - ")[0].split(": ")[1]);
        int patientId = Integer.parseInt(selectedDoctorPatient.split(" - ")[1].split(": ")[1]);

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM doctor_patient WHERE doctor_id = ? AND patient_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, doctorId);
            statement.setInt(2, patientId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Doctor-Patient association deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete doctor-patient association.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the doctor-patient association.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteDoctorPatientWindow::new);
    }
}
