import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditBedWindow extends JFrame {
    private JComboBox<Integer> bedComboBox;
    private JTextField roomIdField;
    private JTextField patientIdField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditBedWindow() {
        super("Edit Bed");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Bed ComboBox
        panel.add(new JLabel("Select Bed:"));
        bedComboBox = new JComboBox<>();
        loadBeds();
        panel.add(bedComboBox);

        // Room ID Field
        panel.add(new JLabel("Room ID:"));
        roomIdField = new JTextField();
        panel.add(roomIdField);

        // Patient ID Field
        panel.add(new JLabel("Patient ID:"));
        patientIdField = new JTextField();
        panel.add(patientIdField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBed();
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

    private void loadBeds() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT bed_id FROM \"bed\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                bedComboBox.addItem(resultSet.getInt("bed_id"));
            }

            bedComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadBedDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadBedDetails() {
        int selectedBedId = (int) bedComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT room_id, patient_id FROM \"bed\" WHERE bed_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedBedId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                roomIdField.setText(resultSet.getString("room_id"));
                patientIdField.setText(resultSet.getString("patient_id"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveBed() {
        int selectedBedId = (int) bedComboBox.getSelectedItem();
        int roomId = Integer.parseInt(roomIdField.getText());
        int patientId = Integer.parseInt(patientIdField.getText());

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"bed\" SET room_id = ?, patient_id = ? WHERE bed_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, roomId);
            statement.setInt(2, patientId);
            statement.setInt(3, selectedBedId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Bed updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Bed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Bed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditBedWindow::new);
    }
}
