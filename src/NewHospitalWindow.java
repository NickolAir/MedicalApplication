import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

public class NewHospitalWindow extends JFrame {
    private JTextField hospitalNumberField;
    private JComboBox<String> clinicComboBox;
    private HashMap<String, Integer> clinicMap;

    public NewHospitalWindow() {
        super("Добавить новую больницу");
        initComponents();
    }

    private void initComponents() {
        setPreferredSize(new Dimension(400, 200));
        setLocation(100, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;

        JLabel numberLabel = new JLabel("Номер больницы");
        numberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(numberLabel, gbc);

        gbc.gridx = 1;
        hospitalNumberField = new JTextField();
        mainPanel.add(hospitalNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel clinicLabel = new JLabel("Привязать клинику");
        clinicLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(clinicLabel, gbc);

        gbc.gridx = 1;
        clinicComboBox = new JComboBox<>();
        try {
            populateClinicComboBox();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке клиник: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        mainPanel.add(clinicComboBox, gbc);

        DialogButtonsPanel dialogButtonsPanel = new DialogButtonsPanel();
        dialogButtonsPanel.cancelButton.addActionListener(e -> dispose());
        dialogButtonsPanel.okButton.addActionListener(e -> applyChanges());

        add(mainPanel, BorderLayout.CENTER);
        add(dialogButtonsPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void populateClinicComboBox() throws SQLException {
        clinicMap = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT clinic_id, number FROM \"Clinic\"")) {

            while (resultSet.next()) {
                int clinicId = resultSet.getInt("clinic_id");
                String clinicNumber = resultSet.getString("number");
                clinicMap.put(clinicNumber, clinicId);
                clinicComboBox.addItem(clinicNumber);
            }
        }
    }

    private void applyChanges() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String insertSQL = "INSERT INTO \"Hospital\" (number, clinic_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertSQL);

            int hospitalNumber = Integer.parseInt(hospitalNumberField.getText());
            statement.setInt(1, hospitalNumber);

            String selectedClinic = (String) clinicComboBox.getSelectedItem();
            int clinicId = clinicMap.get(selectedClinic);
            statement.setInt(2, clinicId);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Больница успешно добавлена");
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "При выполнении запроса произошла ошибка\n" + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат номера больницы\n" + e.getMessage());
        }
    }

    // Класс для кнопок OK и Cancel
    class DialogButtonsPanel extends JPanel {
        JButton okButton;
        JButton cancelButton;

        DialogButtonsPanel() {
            okButton = new JButton("OK");
            cancelButton = new JButton("Cancel");
            add(okButton);
            add(cancelButton);
        }
    }
}