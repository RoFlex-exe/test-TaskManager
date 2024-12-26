public class Task {
    private int id;
    private String description;
    private boolean completed;
    private int userId;

    public Task(int id, String description, boolean completed, int userId) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format("%-5d | %-40s | %-10s | %-20d", id, description, completed, userId);
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

    public int getUserId() {
        return userId;
    }
}
