import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewBuildingWindow extends JFrame {
    private JComboBox<String> hospitalComboBox;
    private JTextField numberField;
    private JButton saveButton;

    public NewBuildingWindow() {
        super("Add New Building");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Hospital ComboBox
        panel.add(new JLabel("Hospital:"));
        hospitalComboBox = new JComboBox<>();
        hospitalComboBox.addItem("Select Hospital");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String hospitalQuery = "SELECT hospital_id, number FROM \"Hospital\"";
            PreparedStatement statement = connection.prepareStatement(hospitalQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int hospitalId = resultSet.getInt("hospital_id");
                int number = resultSet.getInt("number");
                hospitalComboBox.addItem("Hospital #" + number + " (ID: " + hospitalId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        panel.add(hospitalComboBox);

        // Building Number
        panel.add(new JLabel("Building Number:"));
        numberField = new JTextField();
        panel.add(numberField);

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBuilding();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveBuilding() {
        String selectedHospital = (String) hospitalComboBox.getSelectedItem();
        String number = numberField.getText();

        if (selectedHospital.equals("Select Hospital") || number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select a hospital.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int hospitalId = Integer.parseInt(selectedHospital.split("\\(ID: ")[1].replace(")", ""));

        String query = "INSERT INTO buiding (hospital_id, number) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, hospitalId);
            statement.setInt(2, Integer.parseInt(number));

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Building added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding building.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        hospitalComboBox.setSelectedIndex(0);
        numberField.setText("");
    }
}