import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager("jdbc:sqlite:tasks.db");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Добавить пользователя");
            System.out.println("2. Показать пользователей");
            System.out.println("3. Добавить задачу");
            System.out.println("4. Показать задачи");
            System.out.println("5. Завершить задачу");
            System.out.println("6. Изменить задачу");
            System.out.println("7. Удалить задачу");
            System.out.println("8. Экспортировать задачи в CSV");
            System.out.println("9. Импортировать задачи из CSV");
            System.out.println("10. Выход");
            System.out.print("Выберите действие: ");

            int choice = getValidIntegerInput(scanner);

            switch (choice) {
                case 1:
                    System.out.print("Введите имя пользователя: ");
                    String name = scanner.nextLine();
                    taskManager.addUser (name);
                    System.out.println("Пользователь добавлен.");
                    break;
                case 2:
                    List<User> users = taskManager.getUsers();
                    System.out.println("Список пользователей:");
                    for (User  user : users) {
                        System.out.println(user);
                    }
                    break;
                case 3:
                    System.out.print("Введите описание задачи: ");
                    String description = scanner.nextLine();
                    System.out.print("Введите ID пользователя: ");
                    int userId = getValidIntegerInput(scanner);
                    if (taskManager.userExists(userId)) {
                        taskManager.addTask(description, userId);
                        System.out.println("Задача добавлена.");
                    } else {
                        System.out.println("Ошибка: Пользователь с ID " + userId + " не найден.");
                    }
                    break;
                case 4:
                    List<Task> tasks = taskManager.getTasks();
                    System.out.printf("%-5s | %-40s | %-10s | %-20s%n", "Номер", "Задача", "Статус задачи", "Пользователь");
                    System.out.println("---------------------------------------------------------------");
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 5:
                    System.out.print("Введите ID задачи для завершения: ");
                    int taskIdToComplete = getValidIntegerInput(scanner);
                    if (taskManager.markTaskAsCompleted(taskIdToComplete)) {
                        System.out.println("Задача успешно завершена.");
                    } else {
                        System.out.println("Ошибка: Задача с ID " + taskIdToComplete + " не найдена.");
                    }
                    break;
                case 6:
                    System.out.print("Введите ID задачи для изменения: ");
                    int taskIdToUpdate = getValidIntegerInput(scanner);
                    if (taskManager.taskExists(taskIdToUpdate)) {
                        System.out.print("Введите новое описание задачи: ");
                        String newDescription = scanner.nextLine();
                        System.out.print("Введите новый статус завершения (true/false): ");
                        boolean newStatus = Boolean.parseBoolean(scanner.nextLine());
                        if (taskManager.updateTask(taskIdToUpdate, newDescription, newStatus)) {
                            System.out.println("Задача успешно изменена.");
                        } else {
                            System.out.println("Ошибка при обновлении задачи.");
                        }
                    } else {
                        System.out.println("Ошибка: Задача с ID " + taskIdToUpdate + " не найдена.");
                    }
                    break;
                case 7:
                    System.out.print("Введите ID задачи для удаления: ");
                    int taskIdToDelete = getValidIntegerInput(scanner);
                    if (taskManager.taskExists(taskIdToDelete)) {
                        if (taskManager.deleteTask(taskIdToDelete)) {
                            System.out.println("Задача успешно удалена.");
                        } else {
                            System.out.println("Ошибка при удалении задачи.");
                        }
                    } else {
                        System.out.println("Ошибка: Задача с ID " + taskIdToDelete + " не найдена.");
                    }
                    break;
                case 8:
                    System.out.print("Введите путь для сохранения файла (например, tasks.csv): ");
                    String exportPath = scanner.nextLine();
                    taskManager.exportTasksToCSV(exportPath);
                    System.out.println("Задачи экспортированы в " + exportPath + ".");
                    break;
                case 9:
                    System.out.print("Введите путь к файлу для импорта (например, tasks.csv): ");
                    String importPath = scanner.nextLine();
                    taskManager.importTasksFromCSV(importPath);
                    System.out.println("Задачи успешно импортированы из " + importPath + ".");
                    break;
                case 10:
                    taskManager.close();
                    System.out.println("Выход из программы.");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static int getValidIntegerInput(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Пожалуйста, введите число: ");
            }
        }
    }
}
