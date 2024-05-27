import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteContractWindow extends JFrame {
    private JComboBox<String> contractComboBox;

    public DeleteContractWindow() {
        super("Delete Contract");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel contractLabel = new JLabel("Select Contract:");
        contractComboBox = new JComboBox<>();
        fillContracts();
        panel.add(contractLabel);
        panel.add(contractComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteContract);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillContracts() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT contract_id, lab_id FROM contracts";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int contractId = resultSet.getInt("contract_id");
                int labId = resultSet.getInt("lab_id");
                contractComboBox.addItem("Contract with Lab #" + labId + " (ID: " + contractId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve contracts.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteContract(ActionEvent e) {
        String selectedContract = (String) contractComboBox.getSelectedItem();
        if (selectedContract == null || selectedContract.equals("Select Contract")) {
            JOptionPane.showMessageDialog(this, "Please select a contract to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int contractId = Integer.parseInt(selectedContract.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM contracts WHERE contract_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, contractId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Contract deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete contract.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the contract.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteContractWindow::new);
    }
}
