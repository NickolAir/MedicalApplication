import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditScienceGradeWindow extends JFrame {
    private JComboBox<Integer> gradeComboBox;
    private JTextField doctorIdField;
    private JTextField gradeField;
    private JTextField rankField;
    private JButton saveButton;
    private JButton cancelButton;

    public EditScienceGradeWindow() {
        super("Edit Science Grade");
        initComponents();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Grade ComboBox
        panel.add(new JLabel("Select Grade:"));
        gradeComboBox = new JComboBox<>();
        loadGrades();
        panel.add(gradeComboBox);

        // Doctor ID Field
        panel.add(new JLabel("Doctor ID:"));
        doctorIdField = new JTextField();
        panel.add(doctorIdField);

        // Grade Field
        panel.add(new JLabel("Grade:"));
        gradeField = new JTextField();
        panel.add(gradeField);

        // Rank Field
        panel.add(new JLabel("Rank:"));
        rankField = new JTextField();
        panel.add(rankField);

        // Save and Cancel Buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGrade();
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

    private void loadGrades() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT grade_id FROM \"science_grade\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                gradeComboBox.addItem(resultSet.getInt("grade_id"));
            }

            gradeComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadGradeDetails();
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadGradeDetails() {
        int selectedGradeId = (int) gradeComboBox.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT doctor_id, grade, rank FROM \"science_grade\" WHERE grade_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedGradeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                doctorIdField.setText(resultSet.getString("doctor_id"));
                gradeField.setText(resultSet.getString("grade"));
                rankField.setText(resultSet.getString("rank"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveGrade() {
        int selectedGradeId = (int) gradeComboBox.getSelectedItem();
        int doctorId = Integer.parseInt(doctorIdField.getText());
        String grade = gradeField.getText();
        String rank = rankField.getText();

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "UPDATE \"science_grade\" SET doctor_id = ?, grade = ?, rank = ? WHERE grade_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, doctorId);
            statement.setString(2, grade);
            statement.setString(3, rank);
            statement.setInt(4, selectedGradeId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Science Grade updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Science Grade.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the Science Grade.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditScienceGradeWindow::new);
    }
}
