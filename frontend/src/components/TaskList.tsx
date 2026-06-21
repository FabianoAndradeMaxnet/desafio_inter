import type { Task, TaskStatus } from "../types/task";
import { TaskItem } from "./TaskItem";

interface TaskListProps {
  tasks: Task[];
  updatingTaskId: string | null;
  onStatusChange: (taskId: string, status: TaskStatus) => Promise<void>;
}

export function TaskList({ tasks, updatingTaskId, onStatusChange }: TaskListProps) {
  if (tasks.length === 0) {
    return (
      <section className="empty-state">
        <h2>Nenhuma tarefa encontrada</h2>
        <p>Crie a primeira tarefa para acompanhar o fluxo.</p>
      </section>
    );
  }

  return (
    <section className="task-list" aria-label="Lista de tarefas">
      {tasks.map((task) => (
        <TaskItem
          key={task.id}
          task={task}
          isUpdating={updatingTaskId === task.id}
          onStatusChange={onStatusChange}
        />
      ))}
    </section>
  );
}
