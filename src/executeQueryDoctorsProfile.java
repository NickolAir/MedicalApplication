import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class executeQueryDoctorsProfile extends JFrame {
    private JComboBox<String> clinicHospitalComboBox;
    private JTable doctorsTable;
    private JLabel totalDoctorsLabel;

    public executeQueryDoctorsProfile() {
        super("Query Doctors Profile");
        initComponents();
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

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
        panel.add(clinicHospitalComboBox, BorderLayout.NORTH);

        // Doctors Table
        doctorsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Total Doctors Label
        totalDoctorsLabel = new JLabel("Total Doctors: ");
        panel.add(totalDoctorsLabel, BorderLayout.SOUTH);

        // ComboBox ActionListener
        clinicHospitalComboBox.addActionListener(e -> {
            String selectedItem = (String) clinicHospitalComboBox.getSelectedItem();
            if (selectedItem != null && (selectedItem.startsWith("Clinic") || selectedItem.startsWith("Hospital"))) {
                int id = Integer.parseInt(selectedItem.split("\\(ID: ")[1].replace(")", ""));
                showDoctors(id);
            }
        });

        add(panel);
    }

    private void showDoctors(int id) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("specialization");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query;
            if (id < 1000) { // Check if it's a Clinic or Hospital
                query = "SELECT staff_id, first_name, last_name, specialization FROM medical_staff WHERE clinic_id = ?";
            } else {
                query = "SELECT staff_id, first_name, last_name, specialization FROM medical_staff WHERE hospital_id = ?";
            }
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            int totalDoctors = 0;
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String specialization = resultSet.getString("specialization");
                model.addRow(new Object[]{staffId, firstName, lastName, specialization});
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