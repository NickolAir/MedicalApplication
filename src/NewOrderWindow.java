import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewOrderWindow extends JFrame {
    private JComboBox<String> patientComboBox;
    private JTextField orderField;
    private JButton saveButton;

    public NewOrderWindow() {
        super("Add New Order");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Patient ComboBox
        panel.add(new JLabel("Patient:"));
        patientComboBox = new JComboBox<>();
        patientComboBox.addItem("Select Patient");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String patientQuery = "SELECT patient_id, first_name, last_name FROM \"patient\"";
            PreparedStatement statement = connection.prepareStatement(patientQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                patientComboBox.addItem(firstName + " " + lastName + " (ID: " + patientId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        panel.add(patientComboBox);

        // Order Field
        panel.add(new JLabel("Order:"));
        orderField = new JTextField();
        panel.add(orderField);

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveOrder();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveOrder() {
        String selectedPatient = (String) patientComboBox.getSelectedItem();
        String order = orderField.getText();

        if (selectedPatient.equals("Select Patient")) {
            JOptionPane.showMessageDialog(this, "Please select a patient.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int patientId = Integer.parseInt(selectedPatient.split("\\(ID: ")[1].replace(")", ""));

        String query = "INSERT INTO orders (patient_id, order) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, patientId);
            statement.setString(2, order);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding order.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        patientComboBox.setSelectedIndex(0);
        orderField.setText("");
    }
}