import { useCallback, useEffect, useState } from "react";
import { ErrorMessage } from "./components/ErrorMessage";
import { Loading } from "./components/Loading";
import { TaskForm } from "./components/TaskForm";
import { TaskList } from "./components/TaskList";
import { ApiRequestError, createTask, listTasks, updateTaskStatus } from "./services/api";
import type { CreateTaskPayload, PageMetadata, Task, TaskStatus } from "./types/task";

interface UiError {
  message: string;
  details: string[];
}

const DEFAULT_PAGE = 0;
const DEFAULT_PAGE_SIZE = 20;

export function App() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [metadata, setMetadata] = useState<PageMetadata | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  const [updatingTaskId, setUpdatingTaskId] = useState<string | null>(null);
  const [error, setError] = useState<UiError | null>(null);

  const loadTasks = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await listTasks(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
      setTasks(response.data);
      setMetadata(response.meta);
    } catch (caughtError) {
      setError(toUiError(caughtError));
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadTasks();
  }, [loadTasks]);

  async function handleCreateTask(payload: CreateTaskPayload) {
    setIsCreating(true);
    setError(null);

    try {
      const createdTask = await createTask(payload);
      setTasks((currentTasks) => [createdTask, ...currentTasks]);
      setMetadata((currentMetadata) =>
        currentMetadata
          ? {
              ...currentMetadata,
              totalItems: currentMetadata.totalItems + 1,
            }
          : currentMetadata,
      );
    } catch (caughtError) {
      setError(toUiError(caughtError));
    } finally {
      setIsCreating(false);
    }
  }

  async function handleStatusChange(taskId: string, status: TaskStatus) {
    const currentTask = tasks.find((task) => task.id === taskId);

    if (!currentTask || currentTask.status === status) {
      return;
    }

    setUpdatingTaskId(taskId);
    setError(null);

    try {
      const updatedTask = await updateTaskStatus(taskId, status);
      setTasks((currentTasks) =>
        currentTasks.map((task) => (task.id === updatedTask.id ? updatedTask : task)),
      );
    } catch (caughtError) {
      setError(toUiError(caughtError));
    } finally {
      setUpdatingTaskId(null);
    }
  }

  return (
    <main className="app-shell">
      <section className="page-header">
        <div>
          <p className="eyebrow">Todo Senior Challenge</p>
          <h1>Tarefas</h1>
        </div>
        {metadata && (
          <span className="task-count">
            {metadata.totalItems} {metadata.totalItems === 1 ? "tarefa" : "tarefas"}
          </span>
        )}
      </section>

      <section className="content-grid">
        <aside className="create-panel">
          <h2>Nova tarefa</h2>
          <TaskForm isSubmitting={isCreating} onSubmit={handleCreateTask} />
        </aside>

        <section className="tasks-panel">
          <div className="panel-heading">
            <h2>Lista</h2>
            <button type="button" className="secondary-button" onClick={loadTasks} disabled={isLoading}>
              Atualizar
            </button>
          </div>

          {error && <ErrorMessage message={error.message} details={error.details} onRetry={loadTasks} />}

          {isLoading ? (
            <Loading />
          ) : (
            <TaskList tasks={tasks} updatingTaskId={updatingTaskId} onStatusChange={handleStatusChange} />
          )}
        </section>
      </section>
    </main>
  );
}

function toUiError(error: unknown): UiError {
  if (error instanceof ApiRequestError) {
    return {
      message: error.message,
      details: error.details,
    };
  }

  if (error instanceof Error) {
    return {
      message: error.message,
      details: [],
    };
  }

  return {
    message: "Erro inesperado.",
    details: [],
  };
}
