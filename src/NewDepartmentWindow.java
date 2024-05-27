import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewDepartmentWindow extends JFrame {
    private JComboBox<String> buildingComboBox;
    private JTextField diseaseGroupField;
    private JButton saveButton;

    public NewDepartmentWindow() {
        super("Add New Department");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Building ComboBox
        panel.add(new JLabel("Building:"));
        buildingComboBox = new JComboBox<>();
        buildingComboBox.addItem("Select Building");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String buildingQuery = "SELECT building_id, number, hospital_id FROM \"Buiding\"";
            PreparedStatement statement = connection.prepareStatement(buildingQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int buildingId = resultSet.getInt("building_id");
                int number = resultSet.getInt("number");
                int hospitalId = resultSet.getInt("hospital_id");
                buildingComboBox.addItem("Building #" + number + " (ID: " + buildingId + ", Hospital ID: " + hospitalId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        panel.add(buildingComboBox);

        // Disease Group
        panel.add(new JLabel("Disease Group:"));
        diseaseGroupField = new JTextField();
        panel.add(diseaseGroupField);

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDepartment();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveDepartment() {
        String selectedBuilding = (String) buildingComboBox.getSelectedItem();
        String diseaseGroup = diseaseGroupField.getText();

        if (selectedBuilding.equals("Select Building") || diseaseGroup.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select a building.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int buildingId = Integer.parseInt(selectedBuilding.split("\\(ID: ")[1].split(",")[0]);

        String query = "INSERT INTO department (building_id, disease_group) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, buildingId);
            statement.setString(2, diseaseGroup);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Department added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding department.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        buildingComboBox.setSelectedIndex(0);
        diseaseGroupField.setText("");
    }
}