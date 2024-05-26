import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryPatientsClinic extends JFrame {
    private JComboBox<String> clinicComboBox;
    private JComboBox<String> specializationComboBox;
    private JTable patientsTable;

    public executeQueryPatientsClinic() {
        super("Query Patients in Clinic");
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for combo boxes
        JPanel topPanel = new JPanel(new GridLayout(1, 2));

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

        // Specialization ComboBox
        specializationComboBox = new JComboBox<>();
        specializationComboBox.addItem("Select Specialization");
        // Populate ComboBox with specializations
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT DISTINCT specialization FROM \"medical_staff\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String specialization = resultSet.getString("specialization");
                specializationComboBox.addItem(specialization);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(specializationComboBox);

        panel.add(topPanel, BorderLayout.NORTH);

        // Patients Table
        patientsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ComboBox ActionListener
        ActionListener comboBoxListener = e -> {
            String selectedClinic = (String) clinicComboBox.getSelectedItem();
            String selectedSpecialization = (String) specializationComboBox.getSelectedItem();

            if ((selectedClinic != null && !selectedClinic.equals("Select Clinic")) &&
                    (selectedSpecialization != null && !selectedSpecialization.equals("Select Specialization"))) {
                try {
                    int clinicId = Integer.parseInt(selectedClinic.split("\\(ID: ")[1].replace(")", ""));
                    showPatients(clinicId, selectedSpecialization);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        clinicComboBox.addActionListener(comboBoxListener);
        specializationComboBox.addActionListener(comboBoxListener);

        add(panel);
    }

    private void showPatients(int clinicId, String specialization) {
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
            String query = "SELECT p.* FROM patient p " +
                    "JOIN doctor_patient dp ON p.patient_id = dp.patient_id " +
                    "JOIN medical_staff ms ON dp.doctor_id = ms.staff_id " +
                    "WHERE ms.specialization = ? AND ms.clinic_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, specialization);
            statement.setInt(2, clinicId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String surname = resultSet.getString("surname");
                float temperature = resultSet.getFloat("temperature");
                String condition = resultSet.getString("condition");
                Date admissionDate = resultSet.getDate("admission_date");
                Date dischargeDate = resultSet.getDate("discharge_date");
                model.addRow(new Object[]{patientId, firstName, lastName, surname, temperature, condition, admissionDate, dischargeDate});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        patientsTable.setModel(model);
    }
}