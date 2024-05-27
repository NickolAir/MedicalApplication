import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryBedsAvailability extends JFrame {
    private JComboBox<String> hospitalComboBox;
    private JTable resultsTable;

    public executeQueryBedsAvailability() {
        super("Query Beds Availability");
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for combo box
        JPanel topPanel = new JPanel(new GridLayout(1, 1));

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

        panel.add(topPanel, BorderLayout.NORTH);

        // Results Table
        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ComboBox ActionListener
        ActionListener comboBoxListener = e -> {
            String selectedHospital = (String) hospitalComboBox.getSelectedItem();
            if (selectedHospital != null && !selectedHospital.equals("Select Hospital")) {
                int hospitalId = Integer.parseInt(selectedHospital.split("\\(ID: ")[1].replace(")", ""));
                showBedsAvailability(hospitalId);
            }
        };

        hospitalComboBox.addActionListener(comboBoxListener);

        add(panel);
    }

    private void showBedsAvailability(int hospitalId) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Department ID");
        model.addColumn("Total Rooms");
        model.addColumn("Total Beds");
        model.addColumn("Available Beds");
        model.addColumn("Fully Available Rooms");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String query = "WITH department_rooms AS (" +
                    "    SELECT d.department_id, COUNT(hr.room_id) AS total_rooms " +
                    "    FROM \"Department\" d " +
                    "    JOIN \"Hospital_room\" hr ON d.department_id = hr.department_id " +
                    "    WHERE d.building_id IN (SELECT building_id FROM \"Buiding\" WHERE hospital_id = ?) " +
                    "    GROUP BY d.department_id" +
                    "), department_beds AS (" +
                    "    SELECT d.department_id, COUNT(b.bed_id) AS total_beds, " +
                    "           SUM(CASE WHEN b.patient_id IS NULL THEN 1 ELSE 0 END) AS available_beds " +
                    "    FROM \"Department\" d " +
                    "    JOIN \"Hospital_room\" hr ON d.department_id = hr.department_id " +
                    "    JOIN \"bed\" b ON hr.room_id = b.room_id " +
                    "    WHERE d.building_id IN (SELECT building_id FROM \"Buiding\" WHERE hospital_id = ?) " +
                    "    GROUP BY d.department_id" +
                    "), fully_available_rooms AS (" +
                    "    SELECT d.department_id, COUNT(hr.room_id) AS fully_available_rooms " +
                    "    FROM \"Department\" d " +
                    "    JOIN \"Hospital_room\" hr ON d.department_id = hr.department_id " +
                    "    WHERE d.building_id IN (SELECT building_id FROM \"Buiding\" WHERE hospital_id = ?) " +
                    "      AND hr.room_id NOT IN (SELECT room_id FROM \"bed\" WHERE patient_id IS NOT NULL) " +
                    "    GROUP BY d.department_id" +
                    ")" +
                    "SELECT dr.department_id, dr.total_rooms, db.total_beds, db.available_beds, far.fully_available_rooms " +
                    "FROM department_rooms dr " +
                    "JOIN department_beds db ON dr.department_id = db.department_id " +
                    "LEFT JOIN fully_available_rooms far ON dr.department_id = far.department_id";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, hospitalId);
            statement.setInt(2, hospitalId);
            statement.setInt(3, hospitalId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int departmentId = resultSet.getInt("department_id");
                int totalRooms = resultSet.getInt("total_rooms");
                int totalBeds = resultSet.getInt("total_beds");
                int availableBeds = resultSet.getInt("available_beds");
                int fullyAvailableRooms = resultSet.getInt("fully_available_rooms");
                model.addRow(new Object[]{departmentId, totalRooms, totalBeds, availableBeds, fullyAvailableRooms});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        resultsTable.setModel(model);
    }
}