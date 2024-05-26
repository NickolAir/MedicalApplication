import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryServiceStaffSpecialty extends JFrame {
    private JComboBox<String> clinicHospitalComboBox;
    private JComboBox<String> specialtyComboBox;
    private JTable serviceStaffTable;
    private JLabel totalServiceStaffLabel;

    public executeQueryServiceStaffSpecialty() {
        super("Query Service Staff Specialty");
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

        // Specialty ComboBox
        specialtyComboBox = new JComboBox<>();
        specialtyComboBox.addItem("Select Specialty");
        // Populate ComboBox with specialties
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT DISTINCT specialization FROM Service_staff";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String specialization = resultSet.getString("specialization");
                specialtyComboBox.addItem(specialization);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(specialtyComboBox);

        panel.add(topPanel, BorderLayout.NORTH);

        // Service Staff Table
        serviceStaffTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(serviceStaffTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Total Service Staff Label
        totalServiceStaffLabel = new JLabel("Total Service Staff: ");
        panel.add(totalServiceStaffLabel, BorderLayout.SOUTH);

        // ComboBox ActionListener
        ActionListener comboBoxListener = e -> {
            String selectedClinicHospital = (String) clinicHospitalComboBox.getSelectedItem();
            String selectedSpecialty = (String) specialtyComboBox.getSelectedItem();
            if (selectedClinicHospital != null && selectedSpecialty != null
                    && !selectedClinicHospital.equals("Select Clinic/Hospital")
                    && !selectedSpecialty.equals("Select Specialty")) {
                int id = Integer.parseInt(selectedClinicHospital.split("\\(ID: ")[1].replace(")", ""));
                showServiceStaff(id, selectedSpecialty);
            }
        };

        clinicHospitalComboBox.addActionListener(comboBoxListener);
        specialtyComboBox.addActionListener(comboBoxListener);

        add(panel);
    }

    private void showServiceStaff(int id, String specialty) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Specialization");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query;
            if (id < 1000) { // Check if it's a Clinic or Hospital
                query = "SELECT staff_id, first_name, last_name, specialization FROM Service_staff WHERE clinic_id = ? AND specialization = ?";
            } else {
                query = "SELECT staff_id, first_name, last_name, specialization FROM Service_staff WHERE hospital_id = ? AND specialization = ?";
            }
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, specialty);
            ResultSet resultSet = statement.executeQuery();

            int totalServiceStaff = 0;
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String specialization = resultSet.getString("specialization");
                model.addRow(new Object[]{staffId, firstName, lastName, specialization});
                totalServiceStaff++;
            }
            resultSet.close();
            statement.close();

            serviceStaffTable.setModel(model);
            totalServiceStaffLabel.setText("Total Service Staff: " + totalServiceStaff);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}