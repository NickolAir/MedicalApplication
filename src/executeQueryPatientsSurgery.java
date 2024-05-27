import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class executeQueryPatientsSurgery extends JFrame {
    private JComboBox<String> hospitalComboBox;
    private JComboBox<String> clinicComboBox;
    private JComboBox<String> doctorComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTable resultsTable;

    public executeQueryPatientsSurgery() {
        super("Query Patients Surgery");
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for combo boxes and date fields
        JPanel topPanel = new JPanel(new GridLayout(2, 3));

        // Hospital ComboBox
        hospitalComboBox = new JComboBox<>();
        hospitalComboBox.addItem("Select Hospital");
        // Populate ComboBox with hospitals
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT hospital_id, number FROM \"Hospital\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int hospitalId = resultSet.getInt("hospital_id");
                String number = resultSet.getString("number");
                hospitalComboBox.addItem("Hospital #" + number + " (ID: " + hospitalId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(hospitalComboBox);

        // Clinic ComboBox
        clinicComboBox = new JComboBox<>();
        clinicComboBox.addItem("Select Clinic");
        // Populate ComboBox with clinics
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT clinic_id, number FROM \"Clinic\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int clinicId = resultSet.getInt("clinic_id");
                int number = resultSet.getInt("number");
                clinicComboBox.addItem("Clinic #" + number + " (ID: " + clinicId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(clinicComboBox);

        // Doctor ComboBox
        doctorComboBox = new JComboBox<>();
        doctorComboBox.addItem("Select Doctor");
        // Populate ComboBox with doctors
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id, first_name, last_name, surname FROM \"medical_staff\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String surname = resultSet.getString("surname");
                doctorComboBox.addItem(firstName + " " + lastName + " " + surname + " (ID: " + staffId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(doctorComboBox);

        // Start Date Field
        startDateField = new JTextField();
        topPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        topPanel.add(startDateField);

        // End Date Field
        endDateField = new JTextField();
        topPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        topPanel.add(endDateField);

        panel.add(topPanel, BorderLayout.NORTH);

        // Results Table
        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ActionListener for performing the query
        ActionListener queryListener = e -> performQuery();
        hospitalComboBox.addActionListener(queryListener);
        clinicComboBox.addActionListener(queryListener);
        doctorComboBox.addActionListener(queryListener);
        startDateField.addActionListener(queryListener);
        endDateField.addActionListener(queryListener);

        add(panel);
    }

    private void performQuery() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Patient ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Surname");
        model.addColumn("Operation Type");
        model.addColumn("Operation Date");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            StringBuilder query = new StringBuilder(
                    "SELECT p.patient_id, p.first_name, p.last_name, p.surname, o.operation_type, o.date " +
                            "FROM \"patient\" p " +
                            "JOIN \"operations\" o ON p.patient_id = o.patient_id " +
                            "WHERE o.date BETWEEN ? AND ? ");

            int paramIndex = 3;
            if (hospitalComboBox.getSelectedIndex() > 0) {
                query.append("AND p.hospital_id = ? ");
            } else if (clinicComboBox.getSelectedIndex() > 0) {
                query.append("AND p.clinic_id = ? ");
            } else if (doctorComboBox.getSelectedIndex() > 0) {
                query.append("AND o.doctor_id = ? ");
            }

            PreparedStatement statement = connection.prepareStatement(query.toString());

            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            statement.setString(1, dateFormat.format(dateFormat.parse(startDate)));
            statement.setString(2, dateFormat.format(dateFormat.parse(endDate)));

            if (hospitalComboBox.getSelectedIndex() > 0) {
                int hospitalId = Integer.parseInt(hospitalComboBox.getSelectedItem().toString().split("\\(ID: ")[1].replace(")", ""));
                statement.setInt(paramIndex++, hospitalId);
            } else if (clinicComboBox.getSelectedIndex() > 0) {
                int clinicId = Integer.parseInt(clinicComboBox.getSelectedItem().toString().split("\\(ID: ")[1].replace(")", ""));
                statement.setInt(paramIndex++, clinicId);
            } else if (doctorComboBox.getSelectedIndex() > 0) {
                int doctorId = Integer.parseInt(doctorComboBox.getSelectedItem().toString().split("\\(ID: ")[1].replace(")", ""));
                statement.setInt(paramIndex++, doctorId);
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String surname = resultSet.getString("surname");
                String operationType = resultSet.getString("operation_type");
                Date operationDate = resultSet.getDate("date");
                model.addRow(new Object[]{patientId, firstName, lastName, surname, operationType, operationDate});
            }
        } catch (SQLException | java.text.ParseException ex) {
            ex.printStackTrace();
        }

        resultsTable.setModel(model);
    }
}