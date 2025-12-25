package com.todoapp;

import com.todoapp.model.ActivityLogEntry;
import com.todoapp.model.Task;
import com.todoapp.model.TaskStatistics;
import com.todoapp.repository.InMemoryTodoRepository;
import com.todoapp.service.TodoService;
import com.todoapp.service.TodoServiceImpl;
import com.todoapp.util.TaskFilter;
import com.todoapp.util.TaskSortCriteria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ToDoApplication {

    public static void main(String[] args) {

        InMemoryTodoRepository repository = InMemoryTodoRepository.getInstance();
        TodoService todoService = new TodoServiceImpl(repository); // Constructor Injection

        // Define users
        final Long USER_1_ID = 1001L;
        final Long USER_2_ID = 1002L;

        // Define a time window for stats and logs
        LocalDateTime START_TIME = LocalDateTime.now().minusHours(1);

        System.out.println("--- Starting TODO Application Simulation ---");
        System.out.println("\n*** USER 1 (" + USER_1_ID + ") Operations ***");

        // --- 1. Add Tasks (Test ADDITION, DEADLINE, TAGS) ---
        Task task1 = Task.builder()
                .title("Project Report Finalize")
                .description("Write final summary and conclusions.")
                .deadline(LocalDate.now().minusDays(1)) // This will be SPILLOVER
                .tags(new java.util.HashSet<>(Arrays.asList("Work", "Report")))
                .build();
        Task t1 = todoService.addTask(task1, USER_1_ID);
        System.out.println("Added Task 1: " + t1.getTitle() + " (ID: " + t1.getId() + ")");

        Task task2 = Task.builder()
                .title("Grocery Shopping")
                .description("Buy milk, eggs, bread.")
                .deadline(LocalDate.now().plusDays(2))
                .tags(new java.util.HashSet<>(Arrays.asList("Personal", "Grocery", "Shopping")))
                .build();
        Task t2 = todoService.addTask(task2, USER_1_ID);
        System.out.println("Added Task 2: " + t2.getTitle() + " (ID: " + t2.getId() + ")");

        // --- 2. Add Future Task ---
        Task task3 = Task.builder()
                .title("Annual Review Preparation")
                .description("Gather performance metrics for review.")
                .plannedStartDate(LocalDate.now().plusDays(5))
                .tags(new java.util.HashSet<>(Arrays.asList("Work")))
                .build();
        Task t3 = todoService.addTask(task3, USER_1_ID);
        System.out.println("Added Task 3 (Future): " + t3.getTitle() + " (ID: " + t3.getId() + ")");

        // --- 3. Modify Task (Test MODIFICATION) ---
        t2.setTitle("Grocery Shopping (Urgent)");
        Task t2Modified = todoService.modifyTask(t2, USER_1_ID);
        System.out.println("Modified Task 2 Title to: " + t2Modified.getTitle());

        // --- 4. Complete Task (Test COMPLETION and Removal) ---
        todoService.completeTask(t2Modified.getId(), USER_1_ID);
        System.out.println("Completed Task 2 and it was removed.");

        // --- 5. User 2 Operations (Test multi-user support) ---
        System.out.println("\n*** USER 2 (" + USER_2_ID + ") Operations ***");
        Task user2Task = Task.builder()
                .title("User 2 Specific Task")
                .description("This should only appear for User 2.")
                .deadline(LocalDate.now().plusDays(1))
                .tags(new java.util.HashSet<>(Arrays.asList("U2")))
                .build();
        Task u2t1 = todoService.addTask(user2Task, USER_2_ID);
        System.out.println("User 2 Added Task 1: " + u2t1.getTitle() + " (ID: " + u2t1.getId() + ")");

        // --- 6. List Tasks with Filters/Sort ---
        System.out.println("\n*** USER 1 Task List (Filter: ALL, Sort: Deadline ASC) ***");
        List<Task> allTasksUser1 = todoService.listTasks(USER_1_ID, TaskFilter.ALL, TaskSortCriteria.BY_DEADLINE_ASC);
        allTasksUser1.forEach(task -> {
            System.out.printf("  [ID:%d] %s (Status: %s, Deadline: %s)\n",
                    task.getId(), task.getTitle(), task.getStatus(), task.getDeadline());
        });

        System.out.println("\n*** USER 1 Future Tasks (Filter: FUTURE) ***");
        List<Task> futureTasksUser1 = todoService.listTasks(USER_1_ID, TaskFilter.FUTURE, TaskSortCriteria.BY_CREATED_DATE_ASC);
        futureTasksUser1.forEach(task -> {
            System.out.printf("  [ID:%d] %s (Starts: %s)\n",
                    task.getId(), task.getTitle(), task.getPlannedStartDate());
        });

        // --- 7. Activity Log (Test LOG) ---
        LocalDateTime END_TIME = LocalDateTime.now().plusMinutes(1);
        System.out.println("\n*** USER 1 Activity Log (" + START_TIME.toLocalTime() + " to " + END_TIME.toLocalTime() + ") ***");
        List<ActivityLogEntry> logUser1 = todoService.getActivityLog(USER_1_ID, START_TIME, END_TIME);
        logUser1.forEach(log -> {
            System.out.printf("  [%s] [%s] %s\n",
                    log.getTimestamp().toLocalTime(), log.getType(), log.getDescription());
        });

        // --- 8. Statistics (Test STATS) ---
        System.out.println("\n*** USER 1 Statistics (" + START_TIME.toLocalTime() + " to " + END_TIME.toLocalTime() + ") ***");
        TaskStatistics stats = todoService.getStatistics(USER_1_ID, START_TIME, END_TIME);
        System.out.println("  Tasks Added: " + stats.getTotalAdded());
        System.out.println("  Tasks Completed: " + stats.getTotalCompleted());
        System.out.println("  Tasks Spilled Over (and created in period): " + stats.getTotalSpilledOver());

        System.out.println("\n--- Simulation Complete ---");
    }
}
