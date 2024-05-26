import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryPatientsDetails extends JFrame {
    private JComboBox<String> hospitalComboBox;
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> roomComboBox;
    private JTable patientsTable;

    public executeQueryPatientsDetails() {
        super("Query Patients Details");
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

        // Department ComboBox
        departmentComboBox = new JComboBox<>();
        departmentComboBox.addItem("Select Department");
        topPanel.add(departmentComboBox);

        // Room ComboBox
        roomComboBox = new JComboBox<>();
        roomComboBox.addItem("Select Room");
        topPanel.add(roomComboBox);

        panel.add(topPanel, BorderLayout.NORTH);

        // Patients Table
        patientsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ComboBox ActionListener
        ActionListener comboBoxHospital = e -> {
            String selectedHospital = (String) hospitalComboBox.getSelectedItem();
            if (selectedHospital != null && !selectedHospital.equals("Select Hospital")) {
                int hospitalId = Integer.parseInt(selectedHospital.split("\\(ID: ")[1].replace(")", ""));
                showDepartments(hospitalId);
            }
        };

        ActionListener comboBoxDep = e -> {
            String selectedDepartment = (String) departmentComboBox.getSelectedItem();
            if (selectedDepartment != null && !selectedDepartment.equals("Select Department")) {
                // Extract department ID from the selected department string
                String[] parts = selectedDepartment.split("#");
                int departmentId = Integer.parseInt(parts[1].trim().split(" ")[0]);
                showRooms(departmentId);
            }
        };



        roomComboBox.addActionListener(e -> {
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            if (selectedRoom != null && !selectedRoom.equals("Select Room")) {
                int roomId = Integer.parseInt(selectedRoom.split("\\(ID: ")[1].replace(")", ""));
                showPatients(roomId);
            }
        });


        hospitalComboBox.addActionListener(comboBoxHospital);
        departmentComboBox.addActionListener(comboBoxDep);

        add(panel);
    }

    private void showDepartments(int hospitalId) {
        departmentComboBox.removeAllItems();
        departmentComboBox.addItem("Select Department");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT department_id, disease_group FROM \"Department\" WHERE building_id IN (SELECT building_id FROM \"Buiding\" WHERE hospital_id = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, hospitalId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int departmentId = resultSet.getInt("department_id");
                String diseaseGroup = resultSet.getString("disease_group");
                departmentComboBox.addItem("Department #" + departmentId + " (" + diseaseGroup + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showRooms(int departmentId) {
        roomComboBox.removeAllItems();
        roomComboBox.addItem("Select Room");
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT room_id, number FROM \"Hospital_room\" WHERE department_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, departmentId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int roomId = resultSet.getInt("room_id");
                int number = resultSet.getInt("number");
                roomComboBox.addItem("Room #" + number + " (ID: " + roomId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showPatients(int roomId) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Surname");
        model.addColumn("Temperature");
        model.addColumn("Condition");
        model.addColumn("Admission Date");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "SELECT * FROM patient WHERE hospital_id IN (SELECT hospital_id FROM \"Buiding\" WHERE building_id IN (SELECT building_id FROM \"Hospital_room\" WHERE room_id = ?))";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, roomId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int patientId = resultSet.getInt("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String surname = resultSet.getString("surname");
                float temperature = resultSet.getFloat("temperature");
                String condition = resultSet.getString("condition");
                Date admissionDate = resultSet.getDate("admission_date");
                model.addRow(new Object[]{patientId, firstName, lastName, surname, temperature, condition, admissionDate});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        patientsTable.setModel(model);
    }
}