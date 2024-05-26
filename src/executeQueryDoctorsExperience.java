import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryDoctorsExperience extends JFrame {
    private JComboBox<String> clinicHospitalComboBox;
    private JComboBox<Integer> experienceComboBox;
    private JTable doctorsTable;
    private JLabel totalDoctorsLabel;

    public executeQueryDoctorsExperience() {
        super("Query Doctors Experience");
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

        // Experience ComboBox
        experienceComboBox = new JComboBox<>();
        experienceComboBox.addItem(0); // Add option for no experience requirement
        for (int i = 1; i <= 20; i++) {
            experienceComboBox.addItem(i);
        }
        topPanel.add(experienceComboBox);

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
            int selectedExperience = (int) experienceComboBox.getSelectedItem();
            if (selectedClinicHospital != null && !selectedClinicHospital.equals("Select Clinic/Hospital")) {
                int id = Integer.parseInt(selectedClinicHospital.split("\\(ID: ")[1].replace(")", ""));
                showDoctors(id, selectedExperience);
            }
        };

        clinicHospitalComboBox.addActionListener(comboBoxListener);
        experienceComboBox.addActionListener(comboBoxListener);

        add(panel);
    }

    private void showDoctors(int id, int experience) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Specialization");
        model.addColumn("Experience");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT staff_id, first_name, last_name, specialization, experience " +
                    "FROM medical_staff " +
                    "WHERE clinic_id = ? AND experience >= ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setInt(2, experience);
            ResultSet resultSet = statement.executeQuery();

            int totalDoctors = 0;
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String specialization = resultSet.getString("specialization");
                int doctorExperience = resultSet.getInt("experience");
                model.addRow(new Object[]{staffId, firstName, lastName, specialization, doctorExperience});
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