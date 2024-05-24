import java.sql.*;
import java.util.HashMap;

public class GetUtils {

    public static HashMap<String, Integer> getHospitals() throws SQLException {
        HashMap<String, Integer> hospitals = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT hospital_id, number FROM \"Hospital\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("hospital_id");
                String number = resultSet.getString("number");
                hospitals.put("Hospital #" + number, id);
            }
        }
        return hospitals;
    }

    public static HashMap<String, Integer> getClinics() throws SQLException {
        HashMap<String, Integer> clinics = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT clinic_id, number FROM \"Clinic\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("clinic_id");
                String number = resultSet.getString("number");
                clinics.put("Clinic #" + number, id);
            }
        }
        return clinics;
    }

    public static HashMap<String, Integer> getBuildings() throws SQLException {
        HashMap<String, Integer> buildings = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT building_id, number FROM \"Building\""));

            while (resultSet.next()) {
                Integer id = resultSet.getInt("building_id");
                String number = resultSet.getString("number");
                buildings.put("Building #" + number, id);
            }
        }
        return buildings;
    }

    public static HashMap<String, Integer> getDepartments() throws SQLException {
        HashMap<String, Integer> departments = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT department_id, disease_group FROM \"Department\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("department_id");
                String diseaseGroup = resultSet.getString("disease_group");
                departments.put(diseaseGroup, id);
            }
        }
        return departments;
    }

    public static HashMap<String, Integer> getRooms() throws SQLException {
        HashMap<String, Integer> rooms = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_id, department_id FROM \"Hospital_room\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("room_id");
                Integer departmentId = resultSet.getInt("department_id");
                rooms.put("Room #" + id + " (Dept. " + departmentId + ")", id);
            }
        }
        return rooms;
    }

    public static HashMap<String, Integer> getPatients() throws SQLException {
        HashMap<String, Integer> patients = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT patient_id, first_name, last_name, surname FROM \"patient\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("patient_id");
                String name = resultSet.getString("last_name") + " " + resultSet.getString("first_name") + " " + resultSet.getString("surname");
                patients.put(name, id);
            }
        }
        return patients;
    }

    public static HashMap<String, Integer> getMedicalStaff() throws SQLException {
        HashMap<String, Integer> medicalStaff = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT staff_id, first_name, last_name, surname FROM \"medical_staff\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("staff_id");
                String name = resultSet.getString("last_name") + " " + resultSet.getString("first_name") + " " + resultSet.getString("surname");
                medicalStaff.put(name, id);
            }
        }
        return medicalStaff;
    }

    public static HashMap<String, Integer> getServiceStaff() throws SQLException {
        HashMap<String, Integer> serviceStaff = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT staff_id, first_name, last_name, surname FROM \"service_staff\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("staff_id");
                String name = resultSet.getString("last_name") + " " + resultSet.getString("first_name") + " " + resultSet.getString("surname");
                serviceStaff.put(name, id);
            }
        }
        return serviceStaff;
    }

    public static HashMap<String, Integer> getLabs() throws SQLException {
        HashMap<String, Integer> labs = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT lab_id, number FROM \"lab\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("lab_id");
                String number = resultSet.getString("number");
                labs.put("Lab #" + number, id);
            }
        }
        return labs;
    }

    public static HashMap<String, Integer> getLabTypes() throws SQLException {
        HashMap<String, Integer> labTypes = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT type_id, type FROM \"lab_type\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("type_id");
                String type = resultSet.getString("type");
                labTypes.put(type, id);
            }
        }
        return labTypes;
    }

    public static HashMap<String, String> getDoctorPatients() throws SQLException {
        HashMap<String, String> doctorPatients = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            String query = """
            SELECT 
                ms.first_name AS doctor_first_name, 
                ms.last_name AS doctor_last_name, 
                ms.surname AS doctor_surname, 
                p.first_name AS patient_first_name, 
                p.last_name AS patient_last_name, 
                p.surname AS patient_surname
            FROM \"doctor_patient\" dp
            JOIN \"medical_staff\" ms ON dp.doctor_id = ms.staff_id
            JOIN \"patient\" p ON dp.patient_id = p.patient_id
            """;

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String doctorFIO = resultSet.getString("doctor_last_name") + " " +
                        resultSet.getString("doctor_first_name") + " " +
                        resultSet.getString("doctor_surname");
                String patientFIO = resultSet.getString("patient_last_name") + " " +
                        resultSet.getString("patient_first_name") + " " +
                        resultSet.getString("patient_surname");
                doctorPatients.put(doctorFIO, patientFIO);
            }
        }
        return doctorPatients;
    }

    public static HashMap<String, Integer> getScienceGrades() throws SQLException {
        HashMap<String, Integer> scienceGrades = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT grade_id, grade, rank FROM \"science_grade\"");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("grade_id");
                String grade = resultSet.getString("grade");
                String rank = resultSet.getString("rank");
                scienceGrades.put("Grade " + grade + " rank " + rank, id);
            }
        }
        return scienceGrades;
    }
}