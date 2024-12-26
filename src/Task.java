public class Task {
    private int id;
    private String description;
    private boolean completed;
    private String userName;


    public Task(int id, String description, boolean completed, String userName) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return String.format("%-5d | %-40s | %-10s | %-20s", id, description, completed ? "Выполнено" : "Не выполнено", userName); // Выводим имя пользователя
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getUserName() {
        return userName;
    }
}
