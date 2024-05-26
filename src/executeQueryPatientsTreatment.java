import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryPatientsTreatment extends JFrame {
    private JComboBox<String> hospitalComboBox;
    private JComboBox<String> doctorComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTable patientsTable;

    public executeQueryPatientsTreatment() {
        super("Query Patients Treatment");
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for inputs
        JPanel topPanel = new JPanel(new GridLayout(3, 2));

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

        // Doctor ComboBox
        doctorComboBox = new JComboBox<>();
        doctorComboBox.addItem("Select Doctor");
        // Populate ComboBox with doctors
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id, first_name, last_name FROM \"medical_staff\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                doctorComboBox.addItem("Dr. " + firstName + " " + lastName + " (ID: " + staffId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(doctorComboBox);

        // Date fields
        startDateField = new JTextField("Start Date (YYYY-MM-DD)");
        endDateField = new JTextField("End Date (YYYY-MM-DD)");
        topPanel.add(startDateField);
        topPanel.add(endDateField);

        panel.add(topPanel, BorderLayout.NORTH);

        // Patients Table
        patientsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ActionListener for fetching data
        ActionListener fetchPatientsListener = e -> {
            String selectedHospital = (String) hospitalComboBox.getSelectedItem();
            String selectedDoctor = (String) doctorComboBox.getSelectedItem();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();

            if ((selectedHospital != null && !selectedHospital.equals("Select Hospital")) ||
                    (selectedDoctor != null && !selectedDoctor.equals("Select Doctor"))) {
                try {
                    int hospitalId = selectedHospital != null && !selectedHospital.equals("Select Hospital") ? Integer.parseInt(selectedHospital.split("\\(ID: ")[1].replace(")", "")) : -1;
                    int staffId = selectedDoctor != null && !selectedDoctor.equals("Select Doctor") ? Integer.parseInt(selectedDoctor.split("\\(ID: ")[1].replace(")", "")) : -1;

                    showPatients(hospitalId, staffId, startDate, endDate);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Add action listeners
        hospitalComboBox.addActionListener(fetchPatientsListener);
        doctorComboBox.addActionListener(fetchPatientsListener);

        add(panel);
    }

    private void showPatients(int hospitalId, int staffId, String startDate, String endDate) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Surname");
        model.addColumn("Temperature");
        model.addColumn("Condition");
        model.addColumn("Admission Date");
        model.addColumn("Discharge Date");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            StringBuilder query = new StringBuilder("SELECT * FROM patient p ");
            boolean hasHospital = hospitalId != -1;
            boolean hasDoctor = staffId != -1;

            if (hasHospital) {
                query.append("WHERE p.hospital_id = ? ");
            }
            if (hasDoctor) {
                if (hasHospital) {
                    query.append("AND ");
                } else {
                    query.append("WHERE ");
                }
                query.append("p.patient_id IN (SELECT dp.patient_id FROM doctor_patient dp WHERE dp.doctor_id = ?) ");
            }
            query.append(hasHospital || hasDoctor ? "AND " : "WHERE ");
            query.append("p.admission_date >= ? AND p.admission_date <= ?");

            PreparedStatement statement = connection.prepareStatement(query.toString());

            int paramIndex = 1;
            if (hasHospital) {
                statement.setInt(paramIndex++, hospitalId);
            }
            if (hasDoctor) {
                statement.setInt(paramIndex++, staffId);
            }
            statement.setDate(paramIndex++, Date.valueOf(startDate));
            statement.setDate(paramIndex, Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String surname = resultSet.getString("surname");
                float temperature = resultSet.getFloat("temperature");
                String condition = resultSet.getString("condition");
                Date admissionDate = resultSet.getDate("admission_date");
                Date dischargeDate = resultSet.getDate("discharge_date"); // New field
                model.addRow(new Object[]{patientId, firstName, lastName, surname, temperature, condition, admissionDate, dischargeDate});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        patientsTable.setModel(model);
    }
}
