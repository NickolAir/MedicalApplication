import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewRoomWindow extends JFrame {
    private JComboBox<String> departmentComboBox;
    private JTextField numberField;
    private JButton saveButton;

    public NewRoomWindow() {
        super("Add New Room");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Department ComboBox
        panel.add(new JLabel("Department:"));
        departmentComboBox = new JComboBox<>();
        departmentComboBox.addItem("Select Department");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String departmentQuery = "SELECT department_id, disease_group, building_id FROM \"Department\"";
            PreparedStatement statement = connection.prepareStatement(departmentQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int departmentId = resultSet.getInt("department_id");
                String diseaseGroup = resultSet.getString("disease_group");
                int buildingId = resultSet.getInt("building_id");
                departmentComboBox.addItem("Department: " + diseaseGroup + " (ID: " + departmentId + ", Building ID: " + buildingId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        panel.add(departmentComboBox);

        // Room Number
        panel.add(new JLabel("Room Number:"));
        numberField = new JTextField();
        panel.add(numberField);

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRoom();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveRoom() {
        String selectedDepartment = (String) departmentComboBox.getSelectedItem();
        String number = numberField.getText();

        if (selectedDepartment.equals("Select Department") || number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select a department.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int departmentId = Integer.parseInt(selectedDepartment.split("\\(ID: ")[1].split(",")[0]);

        String query = "INSERT INTO hospital_room (department_id, number) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, departmentId);
            statement.setInt(2, Integer.parseInt(number));

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        departmentComboBox.setSelectedIndex(0);
        numberField.setText("");
    }
}