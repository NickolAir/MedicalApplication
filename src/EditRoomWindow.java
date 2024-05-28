import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditRoomWindow extends JFrame {
    private JComboBox<Integer> roomComboBox;
    private JTextField departmentIdField;
    private JTextField numberField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditRoomWindow() {
        super("Edit Room");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Room ComboBox
        panel.add(new JLabel("Select Room:"));
        roomComboBox = new JComboBox<>();
        loadRooms();
        panel.add(roomComboBox);

        // Department ID Field
        panel.add(new JLabel("Department ID:"));
        departmentIdField = new JTextField();
        panel.add(departmentIdField);

        // Number Field
        panel.add(new JLabel("Number:"));
        numberField = new JTextField();
        panel.add(numberField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRoom();
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

    private void loadRooms() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT room_id FROM \"Hospital_room\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                roomComboBox.addItem(resultSet.getInt("room_id"));
            }

            roomComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadRoomDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadRoomDetails() {
        int selectedRoomId = (int) roomComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT department_id, number FROM \"Hospital_room\" WHERE room_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedRoomId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                departmentIdField.setText(resultSet.getString("department_id"));
                numberField.setText(resultSet.getString("number"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveRoom() {
        int selectedRoomId = (int) roomComboBox.getSelectedItem();
        int departmentId = Integer.parseInt(departmentIdField.getText());
        int number = Integer.parseInt(numberField.getText());

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"Hospital_room\" SET department_id = ?, number = ? WHERE room_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, departmentId);
            statement.setInt(2, number);
            statement.setInt(3, selectedRoomId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Room updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Room.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditRoomWindow::new);
    }
}
