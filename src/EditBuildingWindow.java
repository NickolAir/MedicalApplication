import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditBuildingWindow extends JFrame {
    private JComboBox<Integer> buildingComboBox;
    private JTextField hospitalIdField;
    private JTextField numberField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditBuildingWindow() {
        super("Edit Building");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Building ComboBox
        panel.add(new JLabel("Select Building:"));
        buildingComboBox = new JComboBox<>();
        loadBuildings();
        panel.add(buildingComboBox);

        // Hospital ID Field
        panel.add(new JLabel("Hospital ID:"));
        hospitalIdField = new JTextField();
        panel.add(hospitalIdField);

        // Number Field
        panel.add(new JLabel("Number:"));
        numberField = new JTextField();
        panel.add(numberField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBuilding();
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

    private void loadBuildings() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT building_id FROM \"Buiding\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                buildingComboBox.addItem(resultSet.getInt("building_id"));
            }

            buildingComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadBuildingDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadBuildingDetails() {
        int selectedBuildingId = (int) buildingComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT hospital_id, number FROM \"Buiding\" WHERE building_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedBuildingId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                hospitalIdField.setText(resultSet.getString("hospital_id"));
                numberField.setText(resultSet.getString("number"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveBuilding() {
        int selectedBuildingId = (int) buildingComboBox.getSelectedItem();
        int hospitalId = Integer.parseInt(hospitalIdField.getText());
        int number = Integer.parseInt(numberField.getText());

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"Buiding\" SET hospital_id = ?, number = ? WHERE building_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, hospitalId);
            statement.setInt(2, number);
            statement.setInt(3, selectedBuildingId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Building updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Building.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Building.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditBuildingWindow::new);
    }
}
