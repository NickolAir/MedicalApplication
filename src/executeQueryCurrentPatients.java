import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryCurrentPatients extends JFrame {
    private JComboBox<String> hospitalComboBox;
    private JComboBox<String> profileComboBox;
    private JComboBox<String> doctorComboBox;
    private JTable resultsTable;

    public executeQueryCurrentPatients() {
        super("Query Current Patients Load");
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for combo boxes
        JPanel topPanel = new JPanel(new GridLayout(1, 3));

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

        // Profile ComboBox
        profileComboBox = new JComboBox<>();
        profileComboBox.addItem("Select Profile");
        // Populate ComboBox with profiles
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
        topPanel.add(profileComboBox);

        // Doctor ComboBox
        doctorComboBox = new JComboBox<>();
        doctorComboBox.addItem("Select Doctor");
        topPanel.add(doctorComboBox);

        panel.add(topPanel, BorderLayout.NORTH);

        // Results Table
        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ComboBox ActionListener
        ActionListener hospitalListener = e -> updateDoctorComboBox();
        hospitalComboBox.addActionListener(hospitalListener);
        profileComboBox.addActionListener(hospitalListener);

        doctorComboBox.addActionListener(e -> {
            String selectedDoctor = (String) doctorComboBox.getSelectedItem();
            if (selectedDoctor != null && !selectedDoctor.equals("Select Doctor")) {
                int doctorId = Integer.parseInt(selectedDoctor.split("\\(ID: ")[1].replace(")", ""));
                showCurrentPatientsLoad(doctorId);
            }
        });

        add(panel);
    }

    private void updateDoctorComboBox() {
        doctorComboBox.removeAllItems();
        doctorComboBox.addItem("Select Doctor");

        String selectedHospital = (String) hospitalComboBox.getSelectedItem();
        String selectedProfile = (String) profileComboBox.getSelectedItem();
        if ((selectedHospital != null && !selectedHospital.equals("Select Hospital")) ||
                (selectedProfile != null && !selectedProfile.equals("Select Profile"))) {

            try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
                StringBuilder query = new StringBuilder("SELECT staff_id, first_name, last_name, surname FROM \"medical_staff\" WHERE ");
                boolean hasCondition = false;

                if (selectedHospital != null && !selectedHospital.equals("Select Hospital")) {
                    int hospitalId = Integer.parseInt(selectedHospital.split("\\(ID: ")[1].replace(")", ""));
                    query.append("hospital_id = ").append(hospitalId);
                    hasCondition = true;
                }

                if (selectedProfile != null && !selectedProfile.equals("Select Profile")) {
                    if (hasCondition) {
                        query.append(" AND ");
                    }
                    query.append("specialization = '").append(selectedProfile).append("'");
                }

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString());
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
        }
    }

    private void showCurrentPatientsLoad(int doctorId) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Doctor ID");
        model.addColumn("Doctor Name");
        model.addColumn("Patient Count");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT ms.staff_id, ms.first_name || ' ' || ms.last_name || ' ' || ms.surname AS doctor_name, COUNT(dp.patient_id) AS patient_count " +
                    "FROM \"medical_staff\" ms " +
                    "LEFT JOIN \"doctor_patient\" dp ON ms.staff_id = dp.doctor_id " +
                    "WHERE ms.staff_id = ? " +
                    "GROUP BY ms.staff_id, doctor_name";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String doctorName = resultSet.getString("doctor_name");
                int patientCount = resultSet.getInt("patient_count");
                model.addRow(new Object[]{staffId, doctorName, patientCount});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        resultsTable.setModel(model);
    }
}