import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewBedWindow extends JFrame {
    private JComboBox<String> roomComboBox;
    private JTextField patientIdField;
    private JButton saveButton;

    public NewBedWindow() {
        super("Add New Bed");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Room ComboBox
        panel.add(new JLabel("Room:"));
        roomComboBox = new JComboBox<>();
        roomComboBox.addItem("Select Room");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String roomQuery = "SELECT room_id, number, department_id FROM \"Hospital_room\"";
            PreparedStatement statement = connection.prepareStatement(roomQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                int number = resultSet.getInt("number");
                int departmentId = resultSet.getInt("department_id");
                roomComboBox.addItem("Room #" + number + " (ID: " + roomId + ", Department ID: " + departmentId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        panel.add(roomComboBox);

        // Patient ID (optional)
        panel.add(new JLabel("Patient ID (optional):"));
        patientIdField = new JTextField();
        panel.add(patientIdField);

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBed();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveBed() {
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        String patientId = patientIdField.getText();

        if (selectedRoom.equals("Select Room")) {
            JOptionPane.showMessageDialog(this, "Please select a room.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roomId = Integer.parseInt(selectedRoom.split("\\(ID: ")[1].split(",")[0]);

        String query = "INSERT INTO bed (room_id, patient_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, roomId);
            if (patientId.isEmpty()) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, Integer.parseInt(patientId));
            }

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bed added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding bed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        roomComboBox.setSelectedIndex(0);
        patientIdField.setText("");
    }
}