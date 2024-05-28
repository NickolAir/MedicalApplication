import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditClinicWindow extends JFrame {
    private JComboBox<Integer> clinicComboBox;
    private JTextField numberField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditClinicWindow() {
        super("Edit Clinic");
        initComponents();
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Clinic ComboBox
        panel.add(new JLabel("Select Clinic:"));
        clinicComboBox = new JComboBox<>();
        loadClinics();
        panel.add(clinicComboBox);

        // Number Field
        panel.add(new JLabel("Number:"));
        numberField = new JTextField();
        panel.add(numberField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveClinic();
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

    private void loadClinics() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT clinic_id FROM \"Clinic\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                clinicComboBox.addItem(resultSet.getInt("clinic_id"));
            }

            clinicComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadClinicDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadClinicDetails() {
        int selectedClinicId = (int) clinicComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT number FROM \"Clinic\" WHERE clinic_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedClinicId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                numberField.setText(String.valueOf(resultSet.getInt("number")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveClinic() {
        int selectedClinicId = (int) clinicComboBox.getSelectedItem();
        int number = Integer.parseInt(numberField.getText());

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"Clinic\" SET number = ? WHERE clinic_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, number);
            statement.setInt(2, selectedClinicId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Clinic updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update clinic.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the clinic.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditClinicWindow::new);
    }
}