import { TASK_STATUSES, type Task, type TaskStatus } from "../types/task";

interface TaskItemProps {
  task: Task;
  isUpdating: boolean;
  onStatusChange: (taskId: string, status: TaskStatus) => Promise<void>;
}

const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: "Pendente",
  IN_PROGRESS: "Em andamento",
  DONE: "Concluida",
  CANCELLED: "Cancelada",
};

export function TaskItem({ task, isUpdating, onStatusChange }: TaskItemProps) {
  return (
    <article className="task-item">
      <div className="task-content">
        <div className="task-heading">
          <h3>{task.title}</h3>
          <span className={`status-pill status-${task.status.toLowerCase()}`}>
            {STATUS_LABELS[task.status]}
          </span>
        </div>

        {task.description && <p>{task.description}</p>}

        <dl className="task-dates">
          <div>
            <dt>Criada</dt>
            <dd>{formatDate(task.createdAt)}</dd>
          </div>
          <div>
            <dt>Atualizada</dt>
            <dd>{formatDate(task.updatedAt)}</dd>
          </div>
        </dl>
      </div>

      <label className="status-select">
        Status
        <select
          value={task.status}
          disabled={isUpdating}
          onChange={(event) => onStatusChange(task.id, event.target.value as TaskStatus)}
        >
          {TASK_STATUSES.map((status) => (
            <option key={status} value={status}>
              {STATUS_LABELS[status]}
            </option>
          ))}
        </select>
      </label>
    </article>
  );
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(value));
}
