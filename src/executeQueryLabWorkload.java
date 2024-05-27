import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class executeQueryLabWorkload extends JFrame {
    private JComboBox<String> institutionComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTable resultsTable;

    public executeQueryLabWorkload() {
        super("Query Lab Workload");
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for combo boxes and input fields
        JPanel topPanel = new JPanel(new GridLayout(3, 2));

        // Institution ComboBox
        institutionComboBox = new JComboBox<>();
        institutionComboBox.addItem("Select Institution");
        institutionComboBox.addItem("All Institutions");
        // Populate ComboBox with hospitals and clinics
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            String hospitalQuery = "SELECT hospital_id, number FROM \"Hospital\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(hospitalQuery);
            while (resultSet.next()) {
                int hospitalId = resultSet.getInt("hospital_id");
                int number = resultSet.getInt("number");
                institutionComboBox.addItem("Hospital #" + number + " (ID: " + hospitalId + ")");
            }

            String clinicQuery = "SELECT clinic_id, number FROM \"Clinic\"";
            resultSet = statement.executeQuery(clinicQuery);
            while (resultSet.next()) {
                int clinicId = resultSet.getInt("clinic_id");
                int number = resultSet.getInt("number");
                institutionComboBox.addItem("Clinic #" + number + " (ID: " + clinicId + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        topPanel.add(new JLabel("Institution:"));
        topPanel.add(institutionComboBox);

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

        // ComboBox ActionListener
        ActionListener queryAction = e -> executeQuery();

        institutionComboBox.addActionListener(queryAction);
        startDateField.addActionListener(queryAction);
        endDateField.addActionListener(queryAction);

        add(panel);
    }

    private void executeQuery() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Institution");
        model.addColumn("Average Examinations per Day");

        String selectedInstitution = (String) institutionComboBox.getSelectedItem();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        if (selectedInstitution.equals("All Institutions")) {
            query.append("CASE WHEN hospital_id IS NOT NULL THEN 'Hospital #' || (SELECT number FROM \"Hospital\" WHERE hospital_id = c.hospital_id) ");
            query.append("WHEN clinic_id IS NOT NULL THEN 'Clinic #' || (SELECT number FROM \"Clinic\" WHERE clinic_id = c.clinic_id) ");
            query.append("END AS institution, ");
        } else {
            query.append("'").append(selectedInstitution).append("' AS institution, ");
        }
        query.append("COUNT(r.research_id) / COUNT(DISTINCT r.date) AS avg_examinations ");
        query.append("FROM research r ");
        query.append("INNER JOIN contracts c ON r.lab_id = c.lab_id ");
        query.append("WHERE r.date BETWEEN ? AND ? ");
        if (!selectedInstitution.equals("All Institutions")) {
            if (selectedInstitution.startsWith("Hospital")) {
                int hospitalId = Integer.parseInt(selectedInstitution.split("\\(ID: ")[1].replace(")", ""));
                query.append("AND c.hospital_id = ").append(hospitalId).append(" ");
            } else if (selectedInstitution.startsWith("Clinic")) {
                int clinicId = Integer.parseInt(selectedInstitution.split("\\(ID: ")[1].replace(")", ""));
                query.append("AND c.clinic_id = ").append(clinicId).append(" ");
            }
        }
        query.append("GROUP BY institution");

        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String institution = resultSet.getString("institution");
                float avgExaminations = resultSet.getFloat("avg_examinations");
                model.addRow(new Object[]{institution, avgExaminations});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        resultsTable.setModel(model);
    }
}