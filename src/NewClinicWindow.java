import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewClinicWindow extends JFrame {
    private JTextField numberField;

    public NewClinicWindow() {
        super("Add New Clinic");
        initComponents();
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));

        JLabel numberLabel = new JLabel("Clinic Number:");
        numberField = new JTextField();
        inputPanel.add(numberLabel);
        inputPanel.add(numberField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClinic();
            }
        });

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);

        add(panel);
    }

    private void addClinic() {
        int number = Integer.parseInt(numberField.getText().trim());

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String insertSQL = "INSERT INTO \"Clinic\" (number) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setInt(1, number);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Clinic added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the window after successful addition
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add clinic.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while adding clinic:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}