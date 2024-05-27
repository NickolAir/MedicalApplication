import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryDoctorsOperations extends JFrame {
    private JComboBox<String> institutionComboBox;
    private JComboBox<String> profileComboBox;
    private JTextField minOperationsField;
    private JTable resultsTable;

    public executeQueryDoctorsOperations() {
        super("Query Doctors Operations");
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for combo boxes and input fields
        JPanel topPanel = new JPanel(new GridLayout(3, 2));

        // Institution ComboBox
        institutionComboBox = new JComboBox<>();
        institutionComboBox.addItem("Select Institution");
        institutionComboBox.addItem("All Institutions");
        // Populate ComboBox with hospitals and clinics
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String hospitalQuery = "SELECT hospital_id, number FROM \"Hospital\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(hospitalQuery);
            while (resultSet.next()) {
                int hospitalId = resultSet.getInt("hospital_id");
                String number = resultSet.getString("number");
                institutionComboBox.addItem("Hospital #" + number + " (ID: " + hospitalId + ")");
            }
            String clinicQuery = "SELECT clinic_id, number FROM \"Clinic\"";
            resultSet = statement.executeQuery(clinicQuery);
            while (resultSet.next()) {
                int clinicId = resultSet.getInt("clinic_id");
                int number = resultSet.getInt("number");
                institutionComboBox.addItem("Clinic #" + number + " (ID: " + clinicId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(new JLabel("Institution:"));
        topPanel.add(institutionComboBox);

        // Profile ComboBox
        profileComboBox = new JComboBox<>();
        profileComboBox.addItem("Select Profile");
        // Populate ComboBox with doctor profiles
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT DISTINCT specialization FROM \"medical_staff\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String specialization = resultSet.getString("specialization");
                profileComboBox.addItem(specialization);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(new JLabel("Profile:"));
        topPanel.add(profileComboBox);

        // Minimum Operations Field
        topPanel.add(new JLabel("Minimum Operations:"));
        minOperationsField = new JTextField();
        topPanel.add(minOperationsField);

        panel.add(topPanel, BorderLayout.NORTH);

        // Results Table
        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ActionListener for performing the query
        ActionListener queryListener = e -> performQuery();
        institutionComboBox.addActionListener(queryListener);
        profileComboBox.addActionListener(queryListener);
        minOperationsField.addActionListener(queryListener);

        add(panel);
    }

    private void performQuery() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Doctor ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Surname");
        model.addColumn("Specialization");
        model.addColumn("Number of Operations");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            StringBuilder query = new StringBuilder(
                    "SELECT ms.staff_id, ms.first_name, ms.last_name, ms.surname, ms.specialization, od.operations_count " +
                            "FROM \"medical_staff\" ms " +
                            "JOIN \"operating_doctors\" od ON ms.staff_id = od.doctor_id " +
                            "WHERE od.operations_count >= ?");

            if (institutionComboBox.getSelectedIndex() > 1) {
                String selectedInstitution = (String) institutionComboBox.getSelectedItem();
                if (selectedInstitution.contains("Hospital")) {
                    query.append(" AND ms.hospital_id = ?");
                } else if (selectedInstitution.contains("Clinic")) {
                    query.append(" AND ms.clinic_id = ?");
                }
            }

            if (profileComboBox.getSelectedIndex() > 0) {
                query.append(" AND ms.specialization = ?");
            }

            PreparedStatement statement = connection.prepareStatement(query.toString());

            int paramIndex = 1;
            int minOperations = Integer.parseInt(minOperationsField.getText());
            statement.setInt(paramIndex++, minOperations);

            if (institutionComboBox.getSelectedIndex() > 1) {
                String selectedInstitution = (String) institutionComboBox.getSelectedItem();
                int institutionId = Integer.parseInt(selectedInstitution.split("\\(ID: ")[1].replace(")", ""));
                statement.setInt(paramIndex++, institutionId);
            }

            if (profileComboBox.getSelectedIndex() > 0) {
                String selectedProfile = (String) profileComboBox.getSelectedItem();
                statement.setString(paramIndex++, selectedProfile);
            }

            ResultSet resultSet = statement.executeQuery();
            int doctorCount = 0;
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String surname = resultSet.getString("surname");
                String specialization = resultSet.getString("specialization");
                int operationsCount = resultSet.getInt("operations_count");
                model.addRow(new Object[]{staffId, firstName, lastName, surname, specialization, operationsCount});
                doctorCount++;
            }
            model.addRow(new Object[]{"Total", "", "", "", "", doctorCount});
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        resultsTable.setModel(model);
    }
}