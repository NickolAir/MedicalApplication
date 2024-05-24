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
        try {
            // Инициализация компонентов окна
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

            // Метка и поле ввода для номера больницы
            JLabel numberLabel = new JLabel("Номер больницы");
            numberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            mainPanel.add(numberLabel, gbc);

            gbc.gridx = 1;
            hospitalNumberField = new JTextField();
            mainPanel.add(hospitalNumberField, gbc);

            // Метка и выпадающий список для выбора клиники
            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel clinicLabel = new JLabel("Привязать клинику");
            clinicLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            mainPanel.add(clinicLabel, gbc);

            gbc.gridx = 1;
            clinicComboBox = new JComboBox<>();
            populateClinicComboBox();
            mainPanel.add(clinicComboBox, gbc);

            // Кнопки "ОК" и "Отмена"
            DialogButtonsPanel dialogButtonsPanel = new DialogButtonsPanel();
            dialogButtonsPanel.cancelButton.addActionListener(e -> dispose());
            dialogButtonsPanel.okButton.addActionListener(e -> applyChanges());

            add(mainPanel, BorderLayout.CENTER);
            add(dialogButtonsPanel, BorderLayout.SOUTH);

            pack();
            setVisible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateClinicComboBox() throws SQLException {
        clinicMap = GetUtils.getClinics();  // Получение списка клиник из базы данных
        for (String clinicName : clinicMap.keySet()) {
            clinicComboBox.addItem(clinicName);
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
}