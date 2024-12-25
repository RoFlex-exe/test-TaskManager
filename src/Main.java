import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager("jdbc:sqlite:tasks.db");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Добавить задачу");
            System.out.println("2. Показать задачи");
            System.out.println("3. Завершить задачу");
            System.out.println("4. Выход");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Введите описание задачи: ");
                    String description = scanner.nextLine();
                    taskManager.addTask(description);
                    break;
                case 2:
                    List<Task> tasks = taskManager.getTasks();
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 3:
                    System.out.print("Введите ID задачи для завершения: ");
                    int taskId = scanner.nextInt();
                    taskManager.markTaskAsCompleted(taskId);
                    break;
                case 4:
                    taskManager.close();
                    System.out.println("Выход из программы.");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }
}
