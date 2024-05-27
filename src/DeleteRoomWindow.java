import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteRoomWindow extends JFrame {
    private JComboBox<String> roomComboBox;

    public DeleteRoomWindow() {
        super("Delete Room");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel roomLabel = new JLabel("Select Room:");
        roomComboBox = new JComboBox<>();
        fillRooms();
        panel.add(roomLabel);
        panel.add(roomComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteRoom);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillRooms() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT room_id, number FROM \"Hospital_room\"";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                int number = resultSet.getInt("number");
                roomComboBox.addItem("Room #" + number + " (ID: " + roomId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve rooms.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom(ActionEvent e) {
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        if (selectedRoom == null || selectedRoom.equals("Select Room")) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roomId = Integer.parseInt(selectedRoom.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM \"Hospital_room\" WHERE room_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, roomId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteRoomWindow::new);
    }
}
