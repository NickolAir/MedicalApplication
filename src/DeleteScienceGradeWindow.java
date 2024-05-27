import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DeleteScienceGradeWindow extends JFrame {
    private JComboBox<String> scienceGradeComboBox;

    public DeleteScienceGradeWindow() {
        super("Delete Science Grade");
        initComponents();
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel scienceGradeLabel = new JLabel("Select Science Grade:");
        scienceGradeComboBox = new JComboBox<>();
        fillScienceGrades();
        panel.add(scienceGradeLabel);
        panel.add(scienceGradeComboBox);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteScienceGrade);
        panel.add(deleteButton);

        add(panel);
    }

    private void fillScienceGrades() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT grade_id, grade, rank FROM science_grade";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int gradeId = resultSet.getInt("grade_id");
                String grade = resultSet.getString("grade");
                String rank = resultSet.getString("rank");
                scienceGradeComboBox.addItem("Grade: " + grade + ", Rank: " + rank + " (ID: " + gradeId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve science grades.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteScienceGrade(ActionEvent e) {
        String selectedScienceGrade = (String) scienceGradeComboBox.getSelectedItem();
        if (selectedScienceGrade == null || selectedScienceGrade.equals("Select Science Grade")) {
            JOptionPane.showMessageDialog(this, "Please select a science grade to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int gradeId = Integer.parseInt(selectedScienceGrade.split("\\(ID: ")[1].replace(")", ""));

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "DELETE FROM science_grade WHERE grade_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, gradeId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Science grade deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete science grade.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the science grade.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteScienceGradeWindow::new);
    }
}
