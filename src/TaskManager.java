import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private Connection connection;

    public TaskManager(String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)";
            String createTasksTable = "CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, completed BOOLEAN, userId INTEGER, FOREIGN KEY (userId) REFERENCES users(id))";
            stmt.execute(createUsersTable);
            stmt.execute(createTasksTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser (String name) {
        String sql = "INSERT INTO users (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    public void addTask(String description, int userId) {
        String sql = "INSERT INTO tasks (description, completed, userId) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setBoolean(2, false); // По умолчанию задача не завершена
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении задачи: " + e.getMessage());
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                users.add(new User(id, name));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении пользователей: " + e.getMessage());
        }
        return users;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                int userId = rs.getInt("userId");
                tasks.add(new Task(id, description, completed, userId));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении задач: " + e.getMessage());
        }
        return tasks;
    }

    public boolean userExists(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Если количество больше 0, то пользователь существует
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке пользователя: " + e.getMessage());
        }
        return false;
    }

    public boolean taskExists(int taskId) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Если количество больше 0, то задача существует
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке задачи: " + e.getMessage());
        }
        return false;
    }

    public boolean markTaskAsCompleted(int taskId) {
        String sql = "UPDATE tasks SET completed = true WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка при завершении задачи: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTask(int taskId, String newDescription, boolean newStatus) {
        String sql = "UPDATE tasks SET description = ?, completed = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newDescription);
            pstmt.setBoolean(2, newStatus);
            pstmt.setInt(3, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении задачи: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении задачи: " + e.getMessage());
            return false;
        }
    }

    public void exportTasksToCSV(String filePath) {
        String sql = "SELECT * FROM tasks";
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


            writer.write("ID,Description,Completed,User Id");
            writer.newLine();


            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                int userId = rs.getInt("userId");


                writer.write("\"" + id + "\",\"" + description + "\",\"" + completed + "\",\"" + userId + "\"");
                writer.newLine();
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении задач для экспорта: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    public void importTasksFromCSV(String filePath) {
        String sql = "INSERT INTO tasks (description, completed, userId) VALUES (?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String description = parts[1].replace("\"", ""); // Удалить кавычки
                    boolean completed = Boolean.parseBoolean(parts[2].replace("\"", ""));
                    int userId = Integer.parseInt(parts[3].replace("\"", ""));

                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, description);
                        pstmt.setBoolean(2, completed);
                        pstmt.setInt(3, userId);
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при импорте задач: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}
