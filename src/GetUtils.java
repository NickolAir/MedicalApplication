import java.sql.*;
import java.util.HashMap;

public class GetUtils {
    public static HashMap<String, Integer> getEventTypes() throws SQLException{
        HashMap<String, Integer> genres = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM event_type");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("type_id");
                String name = resultSet.getString("type_name");
                genres.put(name, id);
            }

        }

        return genres;
    }
    public static HashMap<String, Integer> getNames(boolean isImpresario) throws SQLException {
        HashMap<String, Integer> names = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet;
            if (isImpresario) {
                resultSet = statement.executeQuery("SELECT * FROM impresario");
            } else {
                resultSet = statement.executeQuery("SELECT * FROM artist");
            }

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("last_name");
                name = name + " " + resultSet.getString("first_name");
                name = name + " " + resultSet.getString("surname");

                names.put(name, id);

            }

        }
        return names;
    }

    public static HashMap<String, Integer> getGenres() throws SQLException {
        HashMap<String, Integer> genres = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM genre");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                genres.put(name, id);
            }

        }

        return genres;
    }

    public static HashMap<String, Integer> getBuildingTypes() throws SQLException {
        HashMap<String, Integer> types = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM building_type");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("type_id");
                String name = resultSet.getString("type_label");
                types.put(name, id);
            }

        }

        return types;
    }

    public static HashMap<String, Integer> getEvents() throws SQLException {
        HashMap<String, Integer> events = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT id, name FROM event");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                events.put(name, id);
            }

        }

        return events;
    }

    public static HashMap<String, Integer> getBuildingNames() throws SQLException {
        HashMap<String, Integer> buildings = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)){
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT id, name, address FROM building");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                name += " " + resultSet.getString("address");

                buildings.put(name, id);
            }
        }
        return buildings;
    }

    public static HashMap<String, Integer> getCompetitions() throws SQLException {
        HashMap<String, Integer> competitions = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("""
                    select id, name from event\s
                    join event_type on event.type_id = event_type.type_id\s
                    where event_type.type_name = 'Конкурс'""");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                competitions.put(name, id);
            }
        }
        return competitions;
    }

    public static String getBuildingTypeTableName(String typeName) {
        switch (typeName) {
            case "Кинотеатр" -> {
                return "cinema";
            }
            case "Театр" -> {
                return "theatre";
            }
            case "Эстрада" -> {
                return "estrade";
            }
        }
        return null;
    }
}