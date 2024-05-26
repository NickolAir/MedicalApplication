import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryDoctorsDegree extends JFrame {
    private JComboBox<String> clinicHospitalComboBox;
    private JComboBox<String> specializationComboBox;
    private JTable doctorsTable;
    private JLabel totalDoctorsLabel;

    public executeQueryDoctorsDegree() {
        super("Query Doctors Degree");
        initComponents();
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for combo boxes
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        // Clinic/Hospital ComboBox
        clinicHospitalComboBox = new JComboBox<>();
        clinicHospitalComboBox.addItem("Select Clinic/Hospital");
        // Populate ComboBox with clinic/hospital numbers
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT clinic_id, number FROM \"Clinic\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int clinicId = resultSet.getInt("clinic_id");
                String number = resultSet.getString("number");
                clinicHospitalComboBox.addItem("Clinic #" + number + " (ID: " + clinicId + ")");
            }
            resultSet.close();
            statement.close();

            query = "SELECT hospital_id, number FROM \"Hospital\"";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int hospitalId = resultSet.getInt("hospital_id");
                String number = resultSet.getString("number");
                clinicHospitalComboBox.addItem("Hospital #" + number + " (ID: " + hospitalId + ")");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(clinicHospitalComboBox);

        // Specialization ComboBox
        specializationComboBox = new JComboBox<>();
        specializationComboBox.addItem("Select Specialization");
        // Populate ComboBox with specializations
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT DISTINCT specialization FROM medical_staff";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String specialization = resultSet.getString("specialization");
                specializationComboBox.addItem(specialization);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(specializationComboBox);

        panel.add(topPanel, BorderLayout.NORTH);

        // Doctors Table
        doctorsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Total Doctors Label
        totalDoctorsLabel = new JLabel("Total Doctors: ");
        panel.add(totalDoctorsLabel, BorderLayout.SOUTH);

        // ComboBox ActionListener
        ActionListener comboBoxListener = e -> {
            String selectedClinicHospital = (String) clinicHospitalComboBox.getSelectedItem();
            String selectedSpecialization = (String) specializationComboBox.getSelectedItem();
            if (selectedClinicHospital != null && selectedSpecialization != null
                    && !selectedClinicHospital.equals("Select Clinic/Hospital")
                    && !selectedSpecialization.equals("Select Specialization")) {
                int id = Integer.parseInt(selectedClinicHospital.split("\\(ID: ")[1].replace(")", ""));
                showDoctors(id, selectedSpecialization);
            }
        };

        clinicHospitalComboBox.addActionListener(comboBoxListener);
        specializationComboBox.addActionListener(comboBoxListener);

        add(panel);
    }

    private void showDoctors(int id, String specialization) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Specialization");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query;
            if (id < 1000) { // Check if it's a Clinic or Hospital
                query = "SELECT ms.staff_id, ms.first_name, ms.last_name, ms.specialization " +
                        "FROM medical_staff ms " +
                        "JOIN science_grade sg ON ms.staff_id = sg.doctor_id " +
                        "WHERE ms.clinic_id = ? AND ms.specialization = ? " +
                        "AND (sg.grade = 'Кандидат медицинских наук' OR sg.grade = 'Доктор медицинских наук') " +
                        "AND (sg.rank = 'Доцент' OR sg.rank = 'Профессор')";
            } else {
                query = "SELECT ms.staff_id, ms.first_name, ms.last_name, ms.specialization " +
                        "FROM medical_staff ms " +
                        "JOIN science_grade sg ON ms.staff_id = sg.doctor_id " +
                        "WHERE ms.hospital_id = ? AND ms.specialization = ? " +
                        "AND (sg.grade = 'Кандидат медицинских наук' OR sg.grade = 'Доктор медицинских наук') " +
                        "AND (sg.rank = 'Доцент' OR sg.rank = 'Профессор')";
            }
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, specialization);
            ResultSet resultSet = statement.executeQuery();

            int totalDoctors = 0;
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String staffSpecialization = resultSet.getString("specialization");
                model.addRow(new Object[]{staffId, firstName, lastName, staffSpecialization});
                totalDoctors++;
            }
            resultSet.close();
            statement.close();

            doctorsTable.setModel(model);
            totalDoctorsLabel.setText("Total Doctors: " + totalDoctors);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}